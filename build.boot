(require 'clojure.edn)
(set-env! :dependencies (clojure.edn/read-string (slurp "deps.edn"))
          :source-paths #{"src"})

(task-options!
   pom {:project 'filemporium
        :version "0.2.0-SNAPSHOT"})

(require '[boot-tasks.pack-npm :refer :all])
