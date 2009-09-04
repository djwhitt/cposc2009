(ns com.djwhitt.cljchat
  (:use [clojure.contrib.pprint])
  (:use [compojure]))

(def messages (ref ["Test message 1" "Test message 2"]))

(defn layout [& body]
  (html
    (doctype :html4)
    [:html
     [:head
      [:title "Clojure Chat"]]
     [:body
      [:h1 "Welcome to Clojure Chat"]
      body]]))

(defn render-messages []
  (map (fn [message] [:p message]) @messages))

(defn render-message-form []
  (form-to [:post "/"]
    (text-field {:size 50} :message)
    (submit-button "Post")))

(defn view []
  (layout
    (render-messages)
    (render-message-form)))

(defn post-message [message]
  (do
    (dosync (alter messages conj message))
    (redirect-to "/")))

(defroutes cljchat
  (GET  "/*" (or (serve-file (params :*)) :next))
  (GET  "/"  (view))
  (POST "/"  (post-message (params :message))))

(defn run []
  (run-server {:port 8080}
    "/*" (servlet cljchat)))

(clojure.main/repl)
