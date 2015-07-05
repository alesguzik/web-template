(require 'clojure.edn)
(set-env! :dependencies (clojure.edn/read-string (slurp "deps.edn"))
          :source-paths #{"src"})

(task-options!
   pom {:project "Filemporium"
        :version "0.2.0-SNAPSHOT"})

;; cider support in boot repl
(require 'boot.repl)
(swap! boot.repl/*default-dependencies* concat '[[cider/cider-nrepl "0.9.1"]])
(swap! boot.repl/*default-middleware* conj 'cider.nrepl/cider-middleware)

(require '[me.raynes.conch :as sh])
(deftask pack-npm
  "
This task does the following:
1) Run `lein npm install` to install all project's npm packages
2) Generate input.js, which `require`s all npm modules
     from lein-npm and places them in global npm object.
3) Run browserify on input.js to combine all code with code of
     dependencies into bundle.js
4) Run uglifyjs on bundle.js to get minified version in bundle.min.js


To use generated bundle you need to do the following:

1) Add
     :foreign-libs [{:file \"resources/public/assets/javascripts/bundle.js\"
                     :provides [\"npm-packages\"]}]
     to ClojureScript compiler options.
2) Require npm-packages namespace in file which uses npm modules
3) Access modules via (aget js/npm \"node-module-name\")
"
  []
  (fn [next-handler]
    (fn [fileset]
      (let [project-data (:project-data (get-env))
            node-modules (->> (:node-dependencies project-data)
                              (map first))
            require-lines (map #(str "window.npm[\"" % "\"] = require('" % "');\n") node-modules)
            require-str (str "window.npm = {}; " (apply str require-lines))
            npm-root (:npm-root project-data)
            in-file-path (str npm-root "/input.js")
            out-file-path (str npm-root "/bundle.js")
            out-min-file-path (str npm-root "/bundle.min.js")]
        (spit in-file-path require-str)
        (sh/with-programs [lein browserify uglifyjs]
          (lein "npm" "install")
          (print (browserify in-file-path "-o" out-file-path))
          (uglifyjs out-file-path "-o" out-min-file-path)))
      (next-handler fileset))))
