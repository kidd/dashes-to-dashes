(ns clojure-getting-started.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [cheshire.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.client :as http]
            [taoensso.carmine :as car :refer (wcar)]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(def server1-conn {:pool {} :spec {}}) ; See `wcar` docstring for opts
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (generate-string ["foo" :from 'Heroku])})

(defn packages [json-pkgs]
  (let [docsets (:docsets (parse-string json-pkgs true))
        url "http://newyork.kapeli.com/feeds/zzz/user_contributed/build/%s/%s"
        archives (map (fn [x] {:archive (format url (name x) (:archive (get docsets x)) )
                              :name (:name (get docsets x))
                              })
                      (keys docsets))]
    (generate-string archives)))

(def json-redis-key "doc")

(defn set-expire [k v exp]
  (wcar* (car/set k v)
         (car/expire k exp)
         v))

(defn get-json []
  (let [redis-doc (or (wcar* (car/get json-redis-key))
                      (let [{:keys [status headers body error] :as resp}
                             @(http/get "http://sanfrancisco.kapeli.com/feeds/zzz/user_contributed/build/index.json")]
                        (set-expire json-redis-key (packages body) 60)))]
     redis-doc))

(defn redis-get []
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (get-json)})


(defroutes app
  (GET "/" []
       (splash))
  (GET "/redis-get" []
       (redis-get))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
