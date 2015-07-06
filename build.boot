(require 'clojure.edn)
(set-env! :dependencies (clojure.edn/read-string (slurp "deps.edn"))
          :source-paths #{"src"})

(task-options!
   pom {:project 'filemporium
        :version "0.2.0-SNAPSHOT"})

(def task-namespaces
  '[;; External
    adzerk.boot-cljs
    adzerk.boot-reload
    boot-deps
    boot.immutant
    danielsz.boot-environ

    ;; Internal
    boot-tasks.pack-npm])

(require '[adzerk.boot-cljs-repl :refer [cljs-repl
                                         start-repl]]
         '[system.boot :refer [system run]])

(mapv #(require [% :refer :all]) task-namespaces)
