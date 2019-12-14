(ns go-engine.core
  (:gen-class))

(defrecord GoGame [matrix size turn]) ;GET RID OF SIZE

(defn create-new-game
  "Creates a size x size GoGame where board is blank and it's black's turn"
  [size]
  (GoGame. (vec (repeat size (vec (repeat size :empty)))) size :black))

(defn get-at-coord
  "Returns the stone (or lack thereof) at the requested coordinates - :black, :white, or :empty"
  [matrix [x y]]
  )

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
  [matrix [x y]])

(defn get-same-colored-neighbors
  [matrix [x y]])

(defn is-completely-surrounded
  "Returns true if [x y] has no empty cells adjacent to it"
  [matrix [x y]])

(defn get-connected-component
  "Returns a set of coordinates representing all of the stones connected to the stone at [x y]"
  [matrix [x y]])

(defn is-component-choked
  "Determines whether the component is choked (no adjacent empty cells)
   coords is a collection of the coordinates of the stones in the component"
  [matrix coords])

(defn remove-if-choked
  "Removes the component containing [x y] from the board if it is choked, returns updated matrix"
  [matrix [x y]])

(defn remove-captured-opponents
  "Remove any choked opponent components from the board caused by stone played at [x y]"
  [matrix [x y]])

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
