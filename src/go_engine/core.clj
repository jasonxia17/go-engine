(ns go-engine.core
  (:gen-class)
  (:require [clojure2d.core :refer :all]
            [clojure2d.color :as c]))

(defrecord GoGame [matrix turn])

(defn create-new-game
  "Creates a size x size GoGame where board is blank and it's black's turn"
  [size]
  (GoGame. (vec (repeat size (vec (repeat size :empty)))) :black))

(defn get-at-coord
  "Returns the stone (or lack thereof) at the requested coordinates - :black, :white, or :empty
   NOTE: x = row, y = col"
  [matrix [x y]]
  ((matrix x) y)
  )

(defn set-coord
  "Sets the stone (or lack thereof) at the requested coordinates - :black, :white, or :empty
   NOTE: x = row, y = col"
  [matrix [x y] val]
  (assoc matrix x (assoc (matrix x) y val)))

(defn opposite-color
  [color]
  (case color
    :black :white
    :white :black
    (println "Opposite color: Invalid argument provided!"))
  )

(defn is-in-bounds
  "Check if the coordinates specify a valid point on the go board"
  [matrix [x y]]
  (let [board-dim (count matrix)]
    (and (>= x 0) (< x board-dim) (>= y 0) (< y board-dim))))

(defn get-neighbors
  "Returns a list of adjacent points to [x y]
  (only the four cardinal direction, and in the bounds of the board"
  [matrix [x y]]
  (filter #(is-in-bounds matrix %) (list [(dec x) y] [(inc x) y] [x (dec y)] [x (inc y)])))

(defn get-same-colored-neighbors
  [matrix [x y]]
  (filter #(= (get-at-coord matrix [x y]) (get-at-coord matrix %)) (get-neighbors matrix [x y])))

(defn is-completely-surrounded
  "Returns true if [x y] has no empty cells adjacent to it"
  [matrix [x y]]
  (every? #(not= :empty (get-at-coord matrix %)) (get-neighbors matrix [x y])))

(defn get-connected-component
  "Returns a set of coordinates representing all of the stones connected to the stone at [x y]"
  [matrix [x y]]
  (loop [stack (list [x y])
         visited #{}]
    (cond (empty? stack) visited
          (visited (first stack)) (recur (rest stack) visited) ; don't re-explore nodes
          :else (let [curr (first stack)
                      popped-stack (rest stack)
                      neighbors (get-same-colored-neighbors matrix curr)
                      new-stack (into popped-stack neighbors)
                      new-visited (conj visited curr)]
                  (recur new-stack new-visited)))))

(defn is-component-choked
  "Determines whether the component is choked
   (A component is choked if no stone in the component has an adjacent empty cell)
   @param  stone-coords is a set of the coordinates of the stones in the component"
  [matrix stone-coords]
  (every? #(is-completely-surrounded matrix %) stone-coords))

(defn remove-if-choked
  "Removes the component containing [x y] from the board if it is choked, returns updated matrix.
   If component is not choked, returns board without modifications."
  [matrix [x y]]
  (let [stone-coords (get-connected-component matrix [x y])]
    (if-not (is-component-choked matrix stone-coords)
      matrix ; don't remove unchoked component
      (loop [matrix matrix
             stones-to-remove (seq stone-coords)]
        (if (empty? stones-to-remove) matrix
            (recur (set-coord matrix (first stones-to-remove) :empty) (rest stones-to-remove)))))))

(defn remove-captured-opponents
  "Remove any choked opponent components from the board caused by stone played at [x y].
   All components that could potentially be removed must be a neighbor of [x y]"
  [matrix [x y]]
  (let [opponent-color (opposite-color (get-at-coord matrix [x y]))]
    (loop [matrix matrix
           neighbors-to-check (get-neighbors matrix [x y])]
      (cond (empty? neighbors-to-check) matrix

            (not= (get-at-coord matrix (first neighbors-to-check)) opponent-color)
            (recur matrix (rest neighbors-to-check)) ; skip non-opponent neighbors

            :else (recur (remove-if-choked matrix (first neighbors-to-check)) (rest neighbors-to-check))))))

(defn handle-move
  "Attempts to place a stone of the current player's color at the requested location.
   Returns the updated game. If the requested move is invalid, the game is returned unchanged."
  [game [x y]]
  (let [{:keys [matrix turn]} game]
    (cond (not (is-in-bounds matrix [x y])) game
          (not= (get-at-coord matrix [x y]) :empty) game

          :else (let [board-after-stone-placed (set-coord matrix [x y] turn)
                      new-board (remove-captured-opponents board-after-stone-placed [x y])
                      component (get-connected-component new-board [x y])]
                  (if (is-component-choked new-board component)
                    game ; move is invalid if resulting component has no liberties, return unchanged game
                    (GoGame. new-board (opposite-color turn)))))))

;; create window
(def window (show-window {:canvas (canvas 900 900)
                          :window-name "Go!"
                          :state (create-new-game 19)}))

(defn draw-board
  [matrix]
  (let [c (canvas 900 900)
        dim (count matrix)
        cell-width (/ 900 dim)
        padding (/ cell-width 2)
        board-width (* cell-width (dec dim))
        stone-radius (* 0.8 cell-width)]
    (with-canvas [c c] ;; prepare drawing context in canvas
      (translate c padding padding)
      (set-background c :yellow) ;; clear background
      (set-color c :black) ;; set color
      (set-stroke c 2.0) ;; set line width
      
      ;; draw gridlines
      (doseq [x (range dim)]
        (line c (* cell-width x) 0 (* cell-width x) board-width)
        (line c 0 (* cell-width x) board-width (* cell-width x)))

      ;; draw stones
      (doseq [x (range dim)
              y (range dim)]
        (let [stone-color (get-at-coord matrix [x y])]
          (if-not (= stone-color :empty)
            (do (set-color c stone-color)
                (ellipse c (* cell-width x) (* cell-width y) stone-radius stone-radius))))))
    (replace-canvas window c)))

(defn -main
  [& args]
  (draw-board (:matrix (get-state window)))
  
  (defmethod mouse-event ["Go!" :mouse-pressed] [e game-state] ;; event on mouse click
    (let [dim (count (:matrix game-state))
          cell-width (/ 900 dim)
          padding (/ cell-width 2)
          x-coord (Math/round (double (/ (- (mouse-x e) padding) cell-width)))
          y-coord (Math/round (double (/ (- (mouse-y e) padding) cell-width)))
          updated-game (handle-move game-state [x-coord y-coord])]
      (draw-board (:matrix updated-game))
      updated-game)))
