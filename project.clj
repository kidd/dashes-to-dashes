(defproject clojure-getting-started "1.0.0-SNAPSHOT"
  :description "Demo Clojure web app"
  :url "http://clojure-getting-started.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [compojure "1.7.0"]
                 [cheshire "5.11.0"]
                 [http-kit "2.6.0"]
                 [com.taoensso/carmine "3.2.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [environ "1.2.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"] [cider/cider-nrepl "0.28.5"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "clojure-getting-started-standalone.jar"
  :profiles {:production {:env {:production true}}})
