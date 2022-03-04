(ns app
  (:require
    [cljs.core.async :refer [go <! timeout]]
    [cljs.core.async.interop :refer-macros [<p!]]
    ["express" :as express]
    ["gcp-metadata" :as gcp]))

;; This program queries the GCP metadata server, and prints out
;; what it finds. Based upon Go lang blog example:
;; https://dev.to/amammay/effective-go-on-cloud-run-the-metadata-server-1ljc
;;
;;  Nick Brandaleone - March 2022

;; A sample ClojureScript/Express app can be seen here:
;; https://ian-says.com/articles/clojurescript-expressjs-docker-api-server/

;; Instance and Project level metadata will only be available if
;; running inside of a Google Cloud compute environment such as
;; Cloud Functions, App Engine, Kubernetes Engine, or Compute Engine.
;; To learn more about the differences between instance and project
;; level metadata, see:
;; https://cloud.google.com/compute/docs/storing-retrieving-metadata#project-instance-metadata

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; easy printing to js/console
(enable-console-print!)

(defonce server (atom nil))

(defn handler [req res]
;  (if (= "https" (aget (.-headers req) "x-forwarded-proto"))
;    (.redirect res (str "http://" (.get req "Host") (.-url req)))
    (do ; could/should be go block
;      (.set res "Content-Type" "text/html")
      (.type res "text/plain") ;; Also "status" method
      (.send res "Hello from ClojureScript and Express!\n")))

(defn get-instance-info []
  "Print out all available instance and region keys"
  (println "Instance and Project data:")
  (-> (js/Promise.resolve (.instance gcp))
      (.then #(println %))
      (.catch #(println %)))
  (-> (js/Promise.resolve (.project gcp))
      (.then #(js/console.log %))
      (.catch #(js/console.log %))))

;; in case we want to leverage a for/doseq loop
(def instance-data ["id" "region" "zone" "service-accounts/default/email"])

(defn get-metadata [req res]
  "Print out GCP instance metadata information to HTTP client"
  (.type res "text/plain")
  (go
    (let [isAvailable (<p! (.isAvailable gcp))]
      (println (str "Metadata service is available?: " isAvailable))
      (if isAvailable (get-instance-info))
      (if isAvailable
        (let [id (<p! (.instance gcp "id"))
              region (<p! (.instance gcp "region"))
              zone (<p! (.instance gcp "zone"))
              sa (<p! (.instance gcp "service-accounts/default/email"))]
          (.write res "Metadata service is available.\n")
          (.write res "Running on GCP hardware.\n\n")
          (.write res (str "Project is: " (aget (.split region "/") 1) "\n"))
          (.write res (str "Instance ID is: " id "\n"))
          (.write res (str "Code is running in: " (.pop (.split region "/")) "\n"))
          (.write res (str "Zone is: " (.pop (.split zone "/")) "\n"))
          (.write res (str "Default service account: " sa "\n")))
        (.write res "No Metadata service found. Likely NOT running on GCP.\n"))
      (.end res))))

(defn start-server []
  (println "Starting Web Server")
  (let [app (express)
        port (or (.. js/process -env -PORT) 8080)]
   (-> app
     (.get "/" handler)
     (.get "/metadata" get-metadata)
     (.listen port (fn [] (println "ClojureScript/Express app listening on port:" port))))))

(defn start! []
  ;; called by main and after reloading code
  (reset! server (start-server)))

(defn reload! []
  ;; called before reloading code
  (.close @server)
  (reset! server nil)
  (start!))

(defn main []
  ;; executed once on startup.
  (start!))
