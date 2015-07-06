(require 'clojure.edn)
(set-env! :dependencies (clojure.edn/read-string (slurp "deps.edn"))
          :resource-paths #{"resources"}
          :source-paths #{"src"})

(task-options!
   pom {:project 'filemporium
        :version "0.2.0-SNAPSHOT"})

(def task-namespaces
  '[;; External
    adzerk.boot-cljs
    adzerk.boot-reload
    boot-deps
    danielsz.boot-environ

    ;; Internal
    boot-tasks.pack-npm
    fe.systems])

(require '[adzerk.boot-cljs-repl :refer [cljs-repl
                                         start-repl]]
         '[system.boot :refer [system run]])

(mapv #(require [% :refer :all]) task-namespaces)

(deftask dev
  "Run a restartable system in the Repl"
  []
  (comp
   (environ :env {:http-port 3000})
   (watch :verbose true)
   (system :sys #'dev-system :auto-start true :hot-reload true :files ["handler.clj"])
   (reload)
   (cljs-repl)
   (cljs :source-map true)
   (repl :server true)))

(deftask dev-run
  "Run a dev system from the command line"
  []
  (comp
   (environ :env {:http-port 3000})
   (cljs)
   (run :main-namespace "holy-grail.core" :arguments [#'dev-system])
   (wait)))

(deftask prod-run
  "Run a prod system from the command line"
  []
  (comp
   (environ :env {:http-port 8008
                  :repl-port 8009})
   (cljs :optimizations :advanced)
   (run :main-namespace "holy-grail.core" :arguments [#'prod-system])
   (wait)))
