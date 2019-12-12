(ns go-engine.core
  (:gen-class))

(defrecord GoGame [matrix size turn])

(defn create-new-game
  "Creates a size x size GoGame where board is blank and it's black's turn"
  [size]
  (GoGame. (vec (repeat size (vec (repeat size :empty)))) size :black))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
