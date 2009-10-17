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

4.2        ; float

nil        ; nil

true       ; true

false      ; false

5/72       ; ratio

:key       ; keyword

a          ; symbol

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

;{{{ 7 - Vars

; map symbols to values
(def x 1)

; thread local and dynamically scoped
(defn printx []
  (println x))

(printx)

(binding [x 2]
  (printx))

(printx)

; *in* *out* are examples in Clojure core

(import '(java.io StringWriter))

(binding [*out* (StringWriter.)]
  (print "hello")
  (print " ")
  (print "world")
  *out*)

;}}}

;{{{ 8 - Functions

; functions are first class
(def sum (fn [a b] (+ a b))

; defn is a convenience macro for defining functions
(defn product [a b]
  (* a b))

; functions can be overloaded by arity
(defn greeting
  ([] "Hello")
  ([name] (str "Hello " name)))

;}}}

;{{{ 9 - Sequences
  
; interface
 
(first [:a :b :c])

(rest [:a :b :c])

(cons :d [:a :b :c])

; conj - like cons but with more dwim

(conj [:a :b :c] :d :e :f)

(conj '(:a :b :c) :d)

; rich set of utilities for manipulating sequences

(map #(* 5 %) [1 2 3])

(reduce + '[1 2 3])

(reverse [:a :b :c])

(sort [1 58 12 23 4])

(filter #(> % 5) [1 58 12 23 4])

; ... many more

; anything you can think of in terms of first and rest can become a seq

(def whole-numbers (lazy-seq (cons 1 (map #(+ 1 %) whole-numbers))))

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

;{{{ Compojure Demo - Final Code

(ns com.djwhitt.cljchat
  (:use [clojure.contrib.pprint])
  (:use [compojure]))

(def messages (ref ["test message 1" "test message 2"]))

(defn render-messages [msgs]
  (map #(vector :p %) msgs))

(defn render-message-form []
  (form-to [:post "/"]
    (text-field {:size 50} :message)
    (submit-button "Post")))

(defn post-message [msgs msg]
  (do
    (dosync (commute msgs conj msg))
    (redirect-to "/")))

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

(defroutes cljchat
  (GET  "/"  (view @messages))
  (POST "/"  (post-message messages (params :message))))

(defn run []
  (run-server {:port 8080}
    "/*" (servlet cljchat)))

;}}}

; vim: set foldmethod=marker:
