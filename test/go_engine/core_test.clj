(ns go-engine.core-test
  (:require [clojure.test :refer :all]
            [go-engine.core :refer :all]))

(deftest create-new-game-test
  (let [game (create-new-game 2)]
    (testing "Board matrix is correct"
      (is (= (:matrix game)
             [[:empty :empty]
              [:empty :empty]])))
    (testing "Board size is correct"
      (is (= (:size game) 2)))
    (testing "Next player turn is correct"
      (is (= (:turn game) :black)))))
