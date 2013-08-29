(ns haml.core
  (:require [clojure.string :as string]))

(def string (slurp "./resources/sample.haml"))

(def tab-size 2)

(def previous-block-level (atom nil))

(def current-block-level  (atom nil))

(defn create-node
  [line]
  (let
    [[whitespaces expression] (rest (re-matches #"(\s*)(.*)" line))
      level (/ (.length whitespaces) tab-size)]
    (zipmap [:level :expression] [level expression])))


(defn -main []
  (map create-node (string/split-lines string)))
