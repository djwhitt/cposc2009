(ns com.djwhitt.cljchat
  (:use [clojure.contrib.pprint])
  (:use [compojure]))

(def messages (ref ["Test message 1" "Test message 2"]))

(defn html-doc
  [title & body]
  (html
    (doctype :html4)
    [:html
     [:head
      [:title title]]
     [:body
      body]]))

(defn header []
  [:h1 "Welcome to Clojure Chat"])

(defn message-html []
  (map (fn [msg] [:p msg]) @messages))

(defn msg-form []
  (form-to [:post "/"]
    (text-field {:size 50} :msg)
    (submit-button "Post")))

(defn main-page []
  (html-doc "Clojure Chat"
    (header)
    (message-html)
    (msg-form)))

(defn post-message [msg]
  (do (dosync
        (alter messages conj msg))
    (redirect-to "/")))

(defroutes cljchat
  (GET "/"
    (main-page))
  (POST "/"
    (post-message (params :msg))))

(defn run-cljchat []
  (run-server {:port 8080}
    "/*" (servlet cljchat)))

(clojure.main/repl)
