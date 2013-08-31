(ns haml.core
  (:require [clojure.string :as string]
            [clj-yaml.core :as yaml]))

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

(defn print-yml [x]
  (doall (map println (string/split-lines (yaml/generate-string x))))
  nil)

(defn llast [x] (last (pop x)))

(defn butllast [x] (vec (butlast (butlast x))))

(defn add-to-last-of-stack-children [stack, line]
  (conj (butlast stack)
        (assoc (last stack) :children
               (conj (:children (last stack)) {:expression (:expression line) :children []}))))

(defn add-to-stack [stack line]
  (conj stack {:expression (:expression line) :children []}))

(defn pop-stack [times stack]
  ;(println times stack)
  (if (zero? times)
      (vec stack)
      (recur (dec times)
                 (conj (butllast stack)
                       (assoc (llast stack)
                              :children (conj (:children (llast stack))
                                              (last stack)))))))


(defn build-tree [lines]
    (print-yml (loop [prev-level 0
           stack [{:children []}]
           cnt 0]
      (let [
            line (if (= (count lines) cnt) nil (nth lines cnt))
            current-level (:level line)
            _ '(println (- current-level prev-level))]
      (if (= (count lines) cnt)
        (pop-stack prev-level stack)
        (recur current-level
               (vec (case (- current-level prev-level)
                 0 (add-to-last-of-stack-children stack line)
                 1 (add-to-stack stack line)
                 (add-to-last-of-stack-children (pop-stack (- prev-level current-level) stack) line)))
               (inc cnt)))))))

(defn -main []
  (build-tree (parsed-lines)))
