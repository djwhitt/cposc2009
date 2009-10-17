### Atoms ###

- Atom = old school lisp term
- Indivisible values
- Most of these probably don't need much explanation
- But a few that are unusual
    - Ratios
        - that's a literal not division
        - but integer division also results in a ratio (no precision loss)
    - Keywords
        - evaluate to themselves
        - similar to symbols in Ruby or interned strings in Java
        - often used as keys in maps
    - Symbols
        - generally used to refer to other things
        - evaluated at run time
        - but still a data type (evaluation can be prevented using quote or ')

### Collections ###

- Atoms by themselves aren't that useful unless you're just doing math

- Need ways to group and arrange them to do much of anything interesting

- Lists
    - quoting required for literals to prevent evaluation
    - fast adding to the front
    - order n access to the rest of the list
    - modified by adding to the front

- Vectors
    - fast adding to the end of the vector
    - fast indexed access to any location in the vector

- Maps
    - key value pairs
    - both key and value can be anything including complex data types
    - fast indexed access by keys

- Sets
    - collections of unique values
    - mostly used to test for membership (ie you have a set of x is y in it)

- Overview
    - Immutability
        - All immutable - anything that "changes" these actually creates a new collection
        - ... but they're still fast because the copies share structure
    - Clojure is different from most Lisp in that it provides literal
      representations for all of these

### Expressions ###

- Data types are fine, but how do you actually do anything with them?

- EVERYTHING is an expression (has a return value)
- All expressions follow the same basic form - (fn/macro/sepcial arg1 arg2 ...)
- 3 types of things can occupy the first position in an expression
    - function
    - builtin/special-form - 13 + 3 interop
    - macro
- Functions
    - most common
    - evaluation rule - first evaluate arguments then call the funtion on results
- Special forms and macros are for the exceptions (eg "if", "and")
    - can delay or prevent evaluation

### Truthiness ###

- Before we get too far lets talk just a little bit about truth in conditionals.

- nil and false are considered false in conditionals everything else is true (same as Ruby)
- Only reason for false is for interop
- Here are some examples

### Collections 2 ###

In talking about collections before I just described what they look like and
some of their properties. I didn't really cover how to access or manipulate
them at all (that would have required expressions).

- Access (2 ways + 1 extra for maps)
    - via get
    - via calling data structure
        - maps, vectors, and sets can all be thought of as functions
    - via calling key on map

Of course getting things out of collecions is just one thing you'll want to do
with them but we'll cover more later.

### Vars ###

Up until now we haven't really talked about how you might store a value for
later use. Even though Clojure emphasises functional programming with immutable
data, it's practical and provides support for shared and even mutable data
through its reference types.

Vars are the most common of the reference types. In fact we've already seen
them in action. Functions and macros are stored in vars (that's why we're
covering them first) and that's why we're covering them first.

Here's what you need to know about vars:

- Thread local 
- Name spaces map symbols to vars
- Can have root bindings (but don't have to)
- Can be rebound dynamically using binding

### Functions ###

Now that we know how to store values for later use in Clojure, I want to zero
in on one particular type of value you might want to store, functions. In fact
functions are probably the most common thing stored in vars.

- First class values
- Can be stored in vars bound to symbols
- Syntax - (fn arg-vector body)
- Often define using the convenience macro defn
- Overloadable by arity

In Clojure your programs are mostly composed of functions.

### Sequences ###

Now that we've talked about functions I want to circle back and look at
collections a bit more. Specifically the sequence interface. We talked a little
bit about indexed access earlier but obviously there has to be more to
collections than that. We need ways to construct collections and perform
complex operations on them.

So how do we do these things?

In olden times Lisp provided a rich set of libraries for manipulating lists
defined in terms of cons cells accessed using car (first) and cdr (rest). This
was all well and good until you needed to manipulate something other than
lists.  Then you lost your beautiful library.

Clojure solves this problem by defining utilities for manipulating sequences in
terms of a sequence interface rather than a concrete data structure. That way
anything implementing the sequence can be manipulated by Clojure rich set of
sequence functions.

- Lots of things are seqable
    - Clojure data structures
    - Java collections
    - XML
    - You can make one for anything you think of in terms of first and rest

### Refs and Transactions ###

Before we get to the demo app there's one more reference type I want to cover,
refs which are used for coordinated state changes.

- Immutability is nice and all, but what if you really need to share state between threads?
- This is where Clojure's STM comes in
- Refs
    - A box containing something (an immutable data structure of some sort)
    - The box can be shared between threads
    - You can look in the box and change what's in it
    - The changes happen in transactions
    - If the contents of the box change while a transaction is happening the
      transaction will be retried
    - Reads do not require transactions and are never blocked by writes
    - Commutes are like writes that don't care about order - also never blocked by writes

- There are other types of concurrency constructs (atoms, agents) but I won't talk about them
  because we don't really have time

### Compojure Demo - 1 ###

- Going to do some handwaving
- I'm not a Compojure expert
- The main purpose is to show incremental development and illustrate the
  language features previously described

- So here's a little "Hello world" Compojure app
- We're going to incrementally turn it into a chat app

- ns
    - macro
    - creates a namespace
    - namespaces map symbols to vars
    - prevent name collisions
    - use imports symbol var mappings from other namespaces
    - should use "include" to only pull in only what's needed
    - allow common lisp style macros in a lisp 1

- defroutes
    - macro
    - used by Compojure to map urls to actions

- html
    - turns vectors into html

- run-server
    - starts server on a port
    - mounts a servlet using our routes at a location

### Compojure Demo - 2 ###

- inline html generation in the routes kind of sucks
- lets make a function and extract it

### Compojure Demo - 3 ###

- What else do we need to turn this into a web chat app?
    - A list of messages and something to render them right?
    - But how do we deal with multiple threads modifying the message list at the same time?
    - We'll use a ref containing a vector and use transactions to modify it.

- So here are the ...
    - ref definition
    - function to render the messages
    - view function with message rendering added
    - redefined routes with messages passed to view
    
### Compojure Demo - 4 ###

- Of course a chat client is pretty boring if all you can do is view messages.
- You want to be able to post messages too.
- So lets add a form.

- render-message-form
    - uses some Compojure html helpers (similar to a helper in Rails)
        - form-to
        - text-field
        - submit-button

- We'll just add it below our message list

### Compojure Demo - 5 ###

- Of course just rendering the form isn't enough
- We need to be able to do something when we post

- post-message
    - Function that describes what we want to happen when we post message
    - Takes a ref containing the messages and the message to add
    - Uses dosync to commute the ref adding the new message at the end
    - Redirects after the transaction completes

- We just add that to our routes and we're set

- Demo it

### Compojure Demo - 6 ###

- So as it stands now we're generating some pretty crappy html
- Lets clean it up a little

- layout
    - function
    - takes n number of params as body
    - wraps them in some typical html stuff
