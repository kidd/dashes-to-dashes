(ns clojure-getting-started.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [cheshire.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.client :as http]
            [taoensso.carmine :as car :refer (wcar)]
            [clojure.java.io :as io]
            [environ.core  :refer [env]]
            [ring.adapter.jetty :as jetty]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [environ.core :refer [env]]))

(def server1-conn {:pool {} :spec {:uri (env :redistogo-url)}}) ; See `wcar` docstring for opts

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
         (car/expire k exp))
  v)

(defn usercontrib-json []
  (let [redis-doc (or (wcar* (car/get json-redis-key))
                      (let [{:keys [status headers body error] :as resp}
                             @(http/get "http://sanfrancisco.kapeli.com/feeds/zzz/user_contributed/build/index.json")]
                        (set-expire json-redis-key (packages body) 3600)))]
     redis-doc))

(defn parse [s]
   (xml/parse
    (java.io.ByteArrayInputStream. (.getBytes s))))

(defn prepare-json [xml]
  (map (fn [ds]
        (let [name (:name ds)
              archive (-> (filter #( = (:tag %1) :url) (-> @(http/get (:download-url ds))
                                                         :body
                                                         parse
                                                         :content))
                        first :content first)]
          {:name name
           :archive archive}

          ))
       xml))

(defn official-json []
  (let [feed "https://api.github.com/repos/Kapeli/feeds/contents/"
        docsets-url "https://raw.github.com/Kapeli/feeds/master"
        docsets (parse-string (:body @(http/get feed)))
        ds-symbolized-keys (map (fn [x] {:name (get x "name") :download-url (get x "download_url")}) docsets)
        ds-only-xml (filter (fn [x] (re-find  #".xml$" (:name x)))  ds-symbolized-keys)
        ignored-docsets (set ["Bootstrap.xml" "Drupal.xml" "Zend_Framework.xml" "Ruby_Installed_Gems.xml" "Man_Pages.xml"])
        ds-filter-ignored (filter #(not (contains? ignored-docsets (:name %1))) ds-only-xml)
        res (prepare-json ds-filter-ignored)]
    res))

(defn docsets [f]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (f)})




(defroutes app
  (GET "/" []
       (splash))
  (GET "/docsets/contrib" []
       (docsets usercontrib-json))
  (GET "/docsets/official" []
       (docsets official-json))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
