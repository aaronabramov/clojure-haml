(ns haml.core
  (:require [clojure.string :as string]))

(def string (slurp "./resources/sample.haml"))

(def tab-size 2)

(def previous-block-level (atom 0))

(def current-block-level  (atom 0))

(def stack (atom nil))

(defn delta [] (- current-block-level previous-block-level))

(defn attributes-map
  [line]
  (let
    [[whitespaces expression] (rest (re-matches #"(\s*)(.*)" line))
      level (/ (.length whitespaces) tab-size)]
    (zipmap [:level :expression] [level expression])))

(defn parsed-lines []
  (map attributes-map (string/split-lines string)))

(defn create-node [node]
  (let [{:keys [expression level]} node]
    (do
      (reset! previous-block-level @current-block-level)
      (reset! current-block-level level)
      (if (= (delta) 1)


    {:expression expression}
    ))))

(defn build-tree [lines]
    (loop [prev-level 0
           stack [[]]
           cnt 0]
      (let [
            line (nth lines cnt)
            current-level (:level line)
            _ '(println (:expression line) current-level (- current-level prev-level))]
      (if (= (dec (count lines)) cnt)
        stack
        (recur current-level
               (case (- current-level prev-level)
                  0 (conj stack [(:expression line)])
                  1 (conj (butlast stack) (conj (last stack) [(:expression line)]))
                  (conj (butlast (butlast stack)) (conj (last (butlast stack)) (last stack))))
               (inc cnt))))))





(defn -main []
  (build-tree (parsed-lines)))
