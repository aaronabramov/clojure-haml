(ns haml.core
  (:require [clojure.string :as string]
            [clj-yaml.core :as yaml]))


(def haml-string (slurp "./resources/sample.haml"))

(defn lines [sting]
  (->> string
      (string/split-lines)
      (map (fn [line]
             (let [[whitespaces tag] (rest (re-matches #"(\s*)(.*)" line))
                   indentation (/ (.length whitespaces) 2)]
               {:tag tag :indentation indentation})))))

;(def lines
;    "Imagining that this was parsed from the following HAML:
;
;       %div#haha
;       %p
;         %a.link
;       %p
;     %div"
;    [{:tag :div, :indentation 0, :attributes {:id "haha"}}
;        {:tag :p, :indentation 2}
;        {:tag :a, :indentation 4, :attributes {:class "link"}}
;        {:tag :p, :indentation 2}
;        {:tag :div, :indentation 0}])

(declare parse-all)

(defn parse-element
    "Given a vector of line-description maps, returns [el remaining-lines] where
       el is in hiccup format."
    [lines]
    (let [[{:keys [indentation tag attributes], :as header} & more] lines,
                  [nested remaining] (split-with #(> (:indentation %) indentation) more)]
          [(apply vector tag attributes (parse-all nested))
                remaining]))

(defn parse-all
    "Returns a list of parsed elements."
    [lines]
    (loop [els []
                    lines lines]
          (if (empty? lines)
                  els
                  (let [[el remaining] (parse-element lines)]
                            (recur (conj els el) remaining)))))

(defn -main []
  (parse-all lines))

;; => [[:div {:id "haha"}
;; ;;      [:p nil [:a {:class "link"}]]
;; ;;      ;;      [:p nil]]
;; ;;      ;;      ;;     [:div nil]]
;
