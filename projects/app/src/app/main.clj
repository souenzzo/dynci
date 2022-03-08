(ns app.main
  (:require [clj-yaml.core :as yaml]
            [clojure.java.io :as io]))


(def dynjob
  (let [job {:docker [{:image "clojure:tools-deps"}]
             :steps  ["checkout"]}]
    {:version   "2.1"
     :jobs      {:build (update job :steps conj {:run "echo hello"})
                 :test  (update job :steps conj {:run "echo world"})}
     :workflows {:build_and_test {:jobs [:build :test]}}}))


(def static-config
  {:version   "2.1"
   :setup     true
   :orbs      {:continuation "circleci/continuation@0.1.2"}
   :jobs      {:setup {:docker [{:image "clojure:tools-deps"}]
                       :steps  ["checkout"
                                {:run "apt-get update && apt-get install -y curl jq && rm -rf /var/lib/apt/lists/*"}
                                {:run {:name    "Generate config"
                                       :command "cd projects/app && clojure -M --report stderr -m app.main > generated_config.yml"}}
                                {"continuation/continue" {:configuration_path "projects/app/generated_config.yml"}}]}}
   :workflows {:setup {:jobs [:setup]}}})

(defn -main
  [& _]
  (yaml/generate-stream *out* dynjob))


(defn static
  []
  (with-open [config (io/writer (io/file ".." ".." ".circleci" "config.yml"))]
    (yaml/generate-stream config static-config)))

(comment
 (static))


