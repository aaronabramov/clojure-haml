(ns haml.core)

(def string (slurp "./resources/sample.haml"))


(defn -main []
  (println (clojure.string/split-lines string))
  )
