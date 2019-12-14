(ns go-engine.core
  (:gen-class))

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
  "Returns the stone (or lack thereof) at the requested coordinates - :black, :white, or :empty
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
   Returns a 2-element vector containing the updated game state and
   a bool representing whether the move was valid."
  [game [x y]]
  nil)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
