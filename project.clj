(defproject ring-cssgen "0.0.1-SNAPSHOT"
  :description "Ring middleware to automatically regenerate cssgen stylesheets."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/tools.namespace "0.1.0"]
                 [cssgen "0.3.0-SNAPSHOT"]]
  :dev-dependencies [[org.clojure/clojure-contrib "1.2.0"]
                     [speclj "1.5.2"]]
  :test-path "spec/"
  :main ring-cssgen.core)
