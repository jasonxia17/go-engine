(ns go-engine.core-test
  (:require [clojure.test :refer :all]
            [go-engine.core :refer :all]))

(deftest create-new-game-test
  (let [game (create-new-game 2)]
    (testing "Board matrix is correct"
      (is (= (:matrix game)
             [[:empty :empty]
              [:empty :empty]])))
    (testing "Next player turn is correct"
      (is (= (:turn game) :black)))))

(def test-board-3by3
  [[:black :black :white]
   [:empty :black :white]
   [:white :white :black]])

(deftest get-at-coord-test
  (is (= (get-at-coord test-board-3by3 [0 1]) :black))
  (is (= (get-at-coord test-board-3by3 [2 1]) :white))
  (is (= (get-at-coord test-board-3by3 [1 0]) :empty)))

(deftest get-neighbors-test
  (is (= (set (get-neighbors test-board-3by3 [2 0])) #{[1 0] [2 1]}))
  (is (= (set (get-neighbors test-board-3by3 [1 2])) #{[0 2] [1 1] [2 2]}))
  (is (= (set (get-neighbors test-board-3by3 [1 1])) #{[1 0] [0 1] [1 2] [2 1]})))

(deftest get-same-colored-neighbors-test
  (is (= (set (get-same-colored-neighbors test-board-3by3 [0 1])) #{[0 0] [1 1]})))

(deftest is-completely-surrounded-test
  (is (= (is-completely-surrounded test-board-3by3 [0 1]) true))
  (is (= (is-completely-surrounded test-board-3by3 [2 2]) true))
  (is (= (is-completely-surrounded test-board-3by3 [1 1]) false))
  (is (= (is-completely-surrounded test-board-3by3 [2 0]) false)))

(deftest get-connected-component-test
  (is (= (get-connected-component test-board-3by3 [0 0]) #{[0 0] [0 1] [1 1]}))
  (is (= (get-connected-component test-board-3by3 [0 1]) #{[0 0] [0 1] [1 1]}))
  (is (= (get-connected-component test-board-3by3 [1 1]) #{[0 0] [0 1] [1 1]}))
  
  (is (= (get-connected-component test-board-3by3 [2 0]) #{[2 0] [2 1]}))
  (is (= (get-connected-component test-board-3by3 [1 2]) #{[1 2] [0 2]}))
  (is (= (get-connected-component test-board-3by3 [2 2]) #{[2 2]})))