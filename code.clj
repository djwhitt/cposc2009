;{{{ REPL

; Clojure has a REPL just like Python, Ruby, Erlang ... etc.
;
; One useful feature for this presentation: *1 *2 *3 refer can be used
; to refer to previous return values.

;}}}

;{{{ Atoms

"hi"   ; string

123    ; number

:key   ; keyword

5/72   ; ratio

4.2    ; float

nil    ; nil

true   ; true

false  ; false

;}}}

;{{{ Collections

'(:key 123 "string")  ; list

[:key 123 "string"]   ; vector

{:width 2, :height 4} ; map

#{0 1 2 3}            ; set

;}}}

;{{{ Collections 2

; vectors, maps, and sets are callable

([\a \b \c] 0)

([\a \b \c] 1)

;}}}

;{{{ Sequences

;}}}

;{{{ Truthiness

; is there a builtin function for this?
(defn truthy? [x]
  (if x
    true
    false))

(truthy? true)

(truthy? 1)

(truthy? 0)

(truthy [1 2 3])

(truthy? nil)

(truthy? false)

;}}}


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
    (dosync (commute messages conj message))
    (redirect-to "/")))

(defroutes cljchat
  (GET  "/*" (or (serve-file (params :*)) :next))
  (GET  "/"  (view))
  (POST "/"  (post-message (params :message))))

(defn run []
  (run-server {:port 8080}
    "/*" (servlet cljchat)))

(clojure.main/repl)

; vim: set foldmethod=marker:
