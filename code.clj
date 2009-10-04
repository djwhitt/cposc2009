;{{{ 1 - REPL

; Clojure has a REPL just like Python, Ruby, Erlang ... etc.
;
; One useful feature for this presentation: *1 *2 *3 refer can be used
; to refer to previous return values.

;}}}

;{{{ 2 - Atoms

"a string" ; string

\a         ; character

123        ; integer

:key       ; keyword

5/72       ; ratio

4.2        ; float

nil        ; nil

true       ; true

false      ; false

;}}}

;{{{ 3 - Collections

'(:a :b :c)  ; list

[:a :b :c]   ; vector

{:a 1, :b 2} ; map

#{:a :b :c}  ; set

;}}}

;{{{ 4 - Expressions

; Everything is an expression. There are no statements.
; 
; Expressions are either atom, (fn ...), (special ...), or (macro ...)

42

(* 6 7)                           ; function

(if (> 2 1) "expected" "bizzaro") ; special form

(and 1 2)                         ; macro

;}}}

;{{{ 5 - Truthiness

; false and nil are considered false in an if expression
; everything else is considered true (including 0)

; is there a builtin function for this?
(defn truthy? [x]
  (if x
    true
    false))

(truthy? true)

(truthy? 1)

(truthy? 0)

(truthy? [1 2 3])

(truthy? nil)

(truthy? false)

;}}}
 
;{{{ 6 - Collections 2

; get is used to access items in indexed collections
  
(get [:a :b :c] 0)

(get {:a 1, :b 2} :a)

(get #{:a :b :c} :a)

; vectors, maps, and sets are callable
 
([:a :b :c] 0)

([:a :b :c] 1)

({:a 1, :b 2} :a)

(#{:a :b :c} :a)

(#{:a :b :c} :d)

; keywords are also callable - they look themselves up in maps
 
(:a {:a 1, :b 2})

;}}}

;{{{ 7 - Sequences

;}}}

;{{{ 8 - Functions

;}}}

;{{{ 9 - Vars

;}}}
  
;{{{ 10 - Refs and Transactions

(def messages (ref ["First post" "blah blah blah" "... comparison to Hitler ..."]))

messages

@messages

(dosync (ref-set messages (conj @messages "flame flame flame")))

(dosync (alter messages conj "flame flame flame"))

(dosync (commute messages conj "flame flame flame"))

;}}}

;{{{ 11 - Compojure Demo Part 1 - Hello world

(ns com.djwhitt.cljchat
  (:use [clojure.contrib.pprint])
  (:use [compojure]))

(defroutes cljchat
  (GET  "/" (html [:h1 "Hello CPOSC 2009"])))

(defn run []
  (run-server {:port 8080}
    "/*" (servlet cljchat)))

;}}}

;{{{ 12 - Compojure Demo Part 2 - Adding a view function

(defn view []
  (html [:h1 "Hello CPOSC 2009"]))

(defroutes cljchat
  (GET  "/" (view)))

;}}}

;{{{ 13 - Compojure Demo Part 3 - Rendering messages

(def messages (ref ["test message 1" "test message 2"]))

(defn render-messages [msgs]
  (map #(vector :p %) msgs))

(defn view [msgs]
  (html (render-messages msgs)))

(defroutes cljchat
  (GET  "/" (view @messages)))

;}}}

;{{{ 14 - Compojure Demo Part 4 - Adding a form

(defn render-message-form []
  (form-to [:post "/"]
    (text-field {:size 50} :message)
    (submit-button "Post")))

(defn view [msgs]
  (html
    (render-messages msgs)
    (render-message-form)))

;}}}

;{{{ 15 - Compojure Demo Part 5 - Adding a post action

(defn post-message [msgs msg]
  (do
    (dosync (commute msgs conj msg))
    (redirect-to "/")))

(defroutes cljchat
  (GET  "/"  (view @messages))
  (POST "/"  (post-message messages (params :message))))

;}}}

;{{{ 16 - Compojure Demo Part 6 - Adding a layout
 
(defn layout [& body]
  (html
    (doctype :html4)
    [:html
     [:head
      [:title "Clojure Chat"]]
     [:body
      [:h1 "Welcome to Clojure Chat"]
      body]]))

(defn view [msgs]
  (layout
    (render-messages msgs)
    (render-message-form)))

;}}}

;{{{ 17 - Compojure Demo Part 7 - Adding some ajax

(defroutes cljchat
  (GET  "/*" (or (serve-file (params :*)) :next))
  (GET  "/"  (view))
  (POST "/"  (post-message (params :message))))

; TODO: add some fancy ajax stuff

;}}}

;{{{ Compojure Demo - Final Code

; TODO: make sure this matches the above steps

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

;}}}

; vim: set foldmethod=marker:
