(defproject clojure-getting-started "1.0.0-SNAPSHOT"
  :description "Demo Clojure web app"
  :url "http://clojure-getting-started.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [cheshire "5.4.0"]
                 [http-kit "2.1.16"]
                 [com.taoensso/carmine "2.9.2"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [environ "0.5.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"] [cider/cider-nrepl "0.9.0-SNAPSHOT"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "clojure-getting-started-standalone.jar"
  :profiles {:production {:env {:production true}}})
