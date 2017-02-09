;;--------------
;;
;; A bit about Clojure functions
;;
;;--------------

;; Functions are first class values in Clojure.

;;--------------
;; 1. fn form
;;--------------

;; Functions are created using the fn form which also folds the semantics of let and do

(fn [x]
  (inc x))


((fn [x]
   (inc x)) 3)

;(fn [x] <- fn accepts a let-style binding vector so
;           all the semantics we explained in the destructuring talk can be used here
;  (println x) <- The forms following the binding vector are the body of the function.
;  (dec x)        This body is placed in an implicit do form, so each function body may
;  (println x)    contain any number of forms and the last form in the body supplies the
;  8)             result returned to the function caller

(do
  (println "a")
  (dec 4))

((fn [x]
   (println x)
   (dec x)
   (println x)
   8) 10)

;; The arguments to a function are matched to each name in the destructuring form based on
;; their position in the calling form

((fn [x] (inc x)) 2)

; It would be equivalent to this let form:
(let [x 2]
  (inc x))

;;--------------
;; 2. Arity.
;;--------------

;;--------------
;; 2.1 The arity of a function is strict
;;--------------
;((fn [x] (inc x)) 2 4) ;;-> clojure.lang.ArityException: Wrong number of args (2) passed...

;;--------------
;; 2.2 Functions with multiple arities
;;--------------
(def add                 ; <- giving it a name in the current namespace
  (fn add-self-reference ; <- optional internal name (inside body)
    ([x] (add-self-reference x 1))
    ([x y] (+ x y))))

; Function calls are dispatched on the number of arguments. The arity is still strict, though.
(add 4)
(add 4 3)
;(add 4 3 8) ;-> clojure.lang.ArityException: Wrong number of args (3) passed...

; Notice the optional name given to the function 'add-self-reference'.
; This optional first argument to fn can be used within the function's body to refer to itself.

;;--------------
;; 3. defn macro
;;--------------
; defn is a macro that encapsulates the functionality of def and fn to concisely define functions
; that are named and registered in the current namespace with a given name

; This definition would be equivalent to the previous one;
(defn add
  ([x] (add x 1))
  ([x y] (+ x y)))

(add 4)
(add 4 3)

;;--------------
;; 3.1 Doc strings
;;--------------

(defn pow
  "it returns 'base' to the power of 'exp'"
  [base exp]
  (reduce * (repeat exp base)))

(doc pow)

;;--------------
;; 3.2 Destructuring function arguments
;;--------------

; The defn macro reuses the fn for which reuses the let form
; to bind function arguments for the scope of the function's body.
; This means that everything we saw in the previous talk about
; destructuring can be done to function arguments.

(defn neighbors [[x-cell y-cell]]
  (set (for [x (range (dec x-cell) (+ x-cell 2))
             y (range (dec y-cell) (+ y-cell 2))
             :when (not (and (= x x-cell) (= y y-cell)))]
         [x y])))

(neighbors [0 0])

; This makes possible some useful idioms for function:

;;--------------
;; 3.2.1 Variadic arguments (using sequential destructuring)
;;--------------

(defn greetings [greeeting & people]
  (map #(str greeeting " " %) people))

(greetings "hola" "koko" "moko")

(defn +-mine [& args] ; variadic
  (apply + args))

(+-mine 1 2 3)

(defn print-separated-by-spaces [& args]
  (clojure.string/join " " args))

(print-separated-by-spaces 1 5 "hola")

;;--------------
;; 3.2.2 Keyword arguments a la Python (using map destructuring)
;;--------------

; With them you can define a function that can accept many arguments, some of which
; might be optional and some of which might have defaults.
; You can also avoid forcing a particular argument ordering.

(defn fn-with-named-parameters [{name :name}]
  name)

(fn-with-named-parameters "a")

(fn-with-named-parameters {:name ["a"]})

(fn-with-named-parameters {:name "b"})

(defn more-complete-fn-with-named-parameters
  [req1 req2             ; <- required params
   & {:keys [a b c d e]  ; <- optional params
      :or {a 1 c 3 d 0}  ; <- a, c and d have default values
                         ;    b and e will take nil if not specified on call
      :as mapOfParamsSpecifiedOnCall}] ; it'll be nil if no optional parameters are specified on call
  (print-separated-by-spaces req1 req2 mapOfParamsSpecifiedOnCall a b c d e))

(more-complete-fn-with-named-parameters 1 2)

(more-complete-fn-with-named-parameters 1 2 :a "a" :c "c" :d "d")

; call ordering doesn't matter for keyword arguments
(more-complete-fn-with-named-parameters 1 2 :d "d" :c "c" :a "a")

(more-complete-fn-with-named-parameters 1 2 :d "d" :c "c" :a "a" :b 3)

(more-complete-fn-with-named-parameters 1 2 :d "d" :c "c" :a "a" :e 85 :b "koko")

(more-complete-fn-with-named-parameters 1 2 :d "d" :c "c" :a "a" :b 3)

;;--------------
;; 3.3 defn- macro
;;--------------

; Same as defn, but yielding a non-public def


;;--------------
;; 4. Preconditions and postconditions
;;--------------

; fn provides support for preconditions and postconditions which are used to perform assertions
; on functions arguments and results, respectively.

; They are valuable for testing and to enforce function invariants

(Math/sqrt 4)

(Math/sqrt -4)

(defn root-square [x]
  {:pre  [(or (zero? x) (pos? x))]}
  (Math/sqrt x))

(root-square 4)

(root-square 0)

;(root-square -4) ; java.lang.AssertionError: Assert failed: (>= x 0)

(try (root-square -4)
  (catch AssertionError e
    (.getMessage e)))

(defn constrained-fn [f x y]
  {:pre  [(pos? x) (neg? y)]
   :post [(> % -3)]} ; <- postcondition on the result (%) of the function
  (f x y))

(constrained-fn #(* %1 %2) 2 -1)

;(constrained-fn #(* %1 %2) -2 -1) ;java.lang.AssertionError: Assert failed: (pos? x)

;(constrained-fn #(* 5 %1 %2) 2 -1) ; java.lang.AssertionError: Assert failed: (> % -3)

;(set! *assert* false) ; <- it works at compiling time not at run time...
;(root-square -4) ; NaN in this case

;; Check also clojure.core.contracts -> https://github.com/clojure/core.contracts

;;--------------
;; 5. Function literals
;;--------------

; When you need to define an anonymous function (especially a very simple one)
; they are the most concise way to do it.

;;--------------
;; 5.1 Syntactic sugar on fn
;;--------------
(map (fn [x] (Math/pow x 2)) [1 2 3])

(map #(Math/pow %1 2) [1 2 3])

(read-string "#(Math/pow %1 2)")

;;--------------
;; 5.2 They are not exactly the same, though
;;--------------

;;--------------
;; 5.2.1 No implicit do form
;;--------------

; fn and all its derivatives (defn, defn-,...) wrap their function bodies
; in an implicit do form.
; Allowing to do things like:

(fn [x y]
  (println (str x " " y))
  (+ x y))

; The equivalent function literal requires an explicit do form:
#(do
   (println (str %1 " " %2))
   (+ %1 %2))

;;--------------
;; 5.2.2 Arity and arguments specified using unnamed position symbols
;;--------------

; The literal uses unnamed positional symbols, where %1 is the first argument,
; %2 the second, etc.

(map #(Math/pow %1 %2) [1 2 3] [1 2 3])

; The highest positional arity symbol defines the arity of the function.

(#(inc %3) "ignored" "ignored" 4)

; You can use % to refer to the first parameter (prefer the shorter notation in general)

(map #(Math/pow % 2) [1 2 3])

(map #(Math/pow % %2) [1 2 3] [1 2 3])

; You can define a variadic function and refer to the rest of parameters
((fn [x & rest]
  (- x (apply + rest))) 10 1 2 3)

(#(- % (apply + %&)) 10 1 2 3)

;;--------------
;; 5.2.3 Function literals can't be nested
;;--------------

(((fn [x]
  (fn [y]
    (str x " " y))) 2) 3)

;((#(#(str % " " %)) 2) 3) ; -> java.lang.IllegalStateException: Nested #()s are not allowed


;; References:
;; Clojure Programming, Practical Lisp for the Java World. Chas Emerick, Brian Carper, Christophe Grand
;;
;; Clojure Docs defn -> https://clojuredocs.org/clojure.core/defn
;;
;; Clojure Docs defn- -> https://clojuredocs.org/clojure.core/defn-
;;
;; Clojure’s :pre and :post -> http://blog.fogus.me/2009/12/21/clojures-pre-and-post/

;; To learn more:
;; Clojure Programming, Practical Lisp for the Java World. Chas Emerick, Brian Carper, Christophe Grand
;;
;; A first take on contracts in Clojure -> http://ianrumford.github.io/blog/2012/11/17/first-take-on-contracts-in-clojure/
;;
;; Contracts programming for Clojure -> https://github.com/clojure/core.contracts




