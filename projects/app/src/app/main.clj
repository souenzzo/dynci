(ns app.main
  (:require [clj-yaml.core :as yaml]))


(def dynjob
  (let [job {:docker [{:image "clojure:tools-deps"}]
             :steps  ["checkout"]}]
    {:version   "2.1"
     :jobs      {:build (update job :steps conj {:run "echo hello"})
                 :test  (update job :steps conj {:run "echo world"})}
     :workflows {:build_and_test {:jobs [:build :test]}}}))


(defn -main
  [& _]
  (yaml/generate-stream *out* dynjob))
