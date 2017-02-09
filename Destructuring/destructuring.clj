;;
;; Destructuring
;;

;; Destructuring is a concise syntax for declaratively pulling apart collections and
;; binding values contained therein as named locals within a let form.
;; (Clojure Programming, Chas Emerick, Brian Carper, Christophe Grand)

;; Since it's a facility provided by let, it can be used
;; in any expression that implicitly uses let (like fn, defn, loop, etc).


;; It comes in two flavours:

;;
;; 1. Sequential Destructuring
;;

(def v [51 25 3 [4 5] 6 7 [88 [99 11] 33]])

;; 1.1 Simple examples

;; [51 25 3 [4 5] 6 7 [88 [99 11] 33]]
;; [x  y                             ]
(let [[x y] v]
  (vector x y))

;; [51 25 3 [4 5] 6 7 [88 [99 11] 33]]
;; [x  _  y                          ]
(let [[x _ y] v]
  (vector x y))

;; [51 25 3 [4 5] 6 7 [88 [99 11] 33]]
;; [x  _  _ v1                       ]
(let [[x _ _ v1] v]
  (vector x v1))

;; _ does not have a special semantics.
;; It is just a valid name for a var
;; that is used as a convention meaning
;; that you are not interested in its value.
(let [[x _ _ v1] v]
  _)

;; 1.2 Nested examples

;; [51 25 3 [4 5] 6 7 [88 [99 11] 33]]
;; [x  _  _ [_ y]                    ]
(let [[x _ _ [_ y]] v]
  (+ x y))

;; [51 25 3 [4 5] 6 7 [88 [99 11] 33]]
;; [_  _  _ [_ _] _ _ [_  [_   x]  y]]
(let [[_ _ _ _ _ _ [_ [_ x] y]] v]
  (+ x y))

;; [51 25 3 [4 5 8 9 3] 6 7 [88 [99 11] 33]]
;; [_  _  _ [_ z]       _ _ [_  [_   x]  y]]
(let [[_ _ _ [_ z] _ _ [_ [_ x] y]] v]
  (vector z x y))

;; 1.3 Rest of values: &
(def values1 ["hi" "there" "koko" "moko"])

;; ["hi" "there" "koko" "moko"]
;; [_   & the-rest            ]
(let [[_ & the-rest] values1]
  the-rest)

;; Notice that how the-rest is a sequence, regardless the original collection being a vector

;; ["hi" "there" "koko" "moko"]
;; [_     _     & the-rest            ]
(let [[_ _ & the-rest] values1]
  the-rest)

;; ["hi" "there" "koko" "moko"]
;; [& the-rest                ]
(let [[& the-rest] values1]
  the-rest)

(rest [])
(next [])

;; & has the same semantics as next (like rest except that (= nil (next '())))
(let [[_ _ _ _ & the-rest] values1]
  the-rest)

; This is how you can have varargs in functions
(defn +-mine [& args]
  (apply + args))

(+-mine)

(defn show-separated-by-commas [& args]
  (clojure.string/join ", " args))

(show-separated-by-commas 1 5 "hola")

;; 1.4 Retaining the original collection: :as
(def coll [3 5 8 9])

(let [[x y & the-rest :as orig-coll] coll]
  (show-separated-by-commas x y the-rest orig-coll))

;; 1.5 Sequencial destructuring works with:

;; 1.5.a. Clojure lists, vectors and seqs

;; Lists
(let [[a _ c] '(1 2 3)]
  (show-separated-by-commas a c))

;; Seqs
(let [[a _ c] (filter odd? '(1 2 3 4 5 6 7))]
  (show-separated-by-commas a c))


;; 1.5.b. Any collection that implements java.util.List
(def an-array-list
  (doto (java.util.ArrayList.) (.add 1) (.add 2) (.add 3)))

(let [[a _ c] an-array-list]
  (show-separated-by-commas a c))

;; 1.5.c. Java arrays
(def an-array (int-array [1 2 3]))

(let [[a _ c d] an-array]
  (vector a c d))

;; 1.5.d. Strings, which are destructured into characters
(let [[& characters] "koko"]
  characters)

(seq "koko")

;;
;; 2. Map Destructuring
;;

(def a-map {:a 1
            :b 3
            :c [7 8 9]
            :d {:e "koko" :f "moko"}
            "foo" 88
            'g 99
            9 "nine"
            [1 3] "hola"})

;; 2.1 Simple examples
(let [{x :a y :b} a-map]
  (- x y))

;; Order is not important
(let [{y :b x :a} a-map]
  (- x y))

(:a a-map)
(:b a-map)

(get a-map :a)
(get a-map :b)

(let [{y "foo"} a-map]
  y)

;("foo" a-map)

(get a-map "foo")

(let [{y 'g} a-map]
  y)

(get a-map 'g)

(let [{y 9} a-map]
  y)

(get a-map 9)

(let [{x [1 3]} a-map]
  x)

;; 2.2 Nested examples
;; From a previous example:
;; (def a-map {:a 1
;;             :b 3
;;             :c [7 8 9]
;;             :d {:e "koko" :f "moko"}
;;             "foo" 88
;;             'g 99
;;             9 "nine"
;;             [1 3] "hola"})

;; 2.2.a Maps nested inside maps
(let [{inner-map :d} a-map]
  inner-map)

(let [{{word :f} :d} a-map]
  word)

;; Equivalent to using let with successive bindings:
(let [{inner-map :d} a-map
      {word :f} inner-map]
  word)

;; Or to using get-in:
(get-in a-map [:d :f])

(let [{x :a {word :f} :d other-num :b} a-map]
  (show-separated-by-commas x word other-num))

;; 2.2.b Vectors nested inside maps
(def map-with-vector {:a "koko" :b ["hello" "two"]})

(let [{[_ number] :b} map-with-vector]
  number)

(let [[_ {x :b}] [1 {:a "x" :b 7}]]
  x)

;; 2.3 Map destructuring works with anything that get function works with:

;; 2.3.a Clojure hash-maps, array-maps and records

; array-maps
(def an-array-map (array-map :a 10 :b 20))

(let [{a :a b :b} an-array-map]
  (show-separated-by-commas a b))

; Records
(defrecord Point [x y])
(def a-point (Point. 1 2))

a-point

(let [{x :x y :y} a-point]
  (show-separated-by-commas x y))

;; 2.3.b Any collection that implements java.util.Map
(def a-java-hash-map
  (doto (java.util.HashMap.)
    (.put "a" "Hola")
    (.put "b" "koko")
    (.put "c" "moko")))

(let [{x "a" y "c" } a-java-hash-map]
  (show-separated-by-commas x y))

;; 2.3.a Any value that is supported by the get function using indices as keys:
;; Clojure vectors, Strings and arrays

;; Vectors

;; From a previous example -> (def v [51 25 3 [4 5] 6 7 [88 [99 11] 33]])

;; [51 25 3 [4 5] 6 7 [88 [99 11] 33]]
;; [_  _  _ [_ _] _ _ [_  [_   x]  y]]
(let [[_ _ _ _ _ _ [_ [_ x] y]] v]
  (show-separated-by-commas x y))

; It can be also done with map-destructuring and the indexes:
(let [{{{x 1} 1 y 2} 6} v]
  (show-separated-by-commas x y))

(let [{[_ [_ x] y] 6} v]
  (show-separated-by-commas x y))

; Or using get-in
(get-in v [6 1 1])
(get-in v [6 2])

; Another example:
; From a previous example -> (def map-with-vector {:a "koko" :b ["hello" "two"]})
(let [{[_ num] :b} map-with-vector]
  num)

(get-in map-with-vector [:b 1])

(let [{{num 1} :b} map-with-vector]
  num)

;; Arrays
;; From a previous example -> (def an-array (int-array [1 2 3]))

(let [[a _ c] an-array]
  (show-separated-by-commas a c))

(let [{a 0 c 2} an-array]
  (show-separated-by-commas a c))

;; Strings
(let [[a _ c] "koko"]
  (show-separated-by-commas a c))

(let [{a 0 c 2} "koko"]
  (show-separated-by-commas a c))

;; 2.4 Retaining the original collection: :as
; From a previous example -> (def map-with-vector {:a "koko" :b ["hello" "two"]})

(let [{a :a :as orig-map} map-with-vector]
  (show-separated-by-commas a orig-map))

;; 2.5 Default values: :or
; From a previous example -> (def map-with-vector {:a "koko" :b ["hello" "two"]})
(let [{k :unknown a :a} map-with-vector]
  (vector a k))

(let [{k :unknown a :a
       :or {k "default-value" a "not-going-to-appear!"}} map-with-vector]
  (show-separated-by-commas a k))

;; You can get a similar behaviour using:
(let [{k :unknown} map-with-vector
      k (or k "default-value")]
  k)

;; But this last code fails to distinguish false from nil
;; So that in this wicked example k gets incorrectly bound -> k should be false!!
(let [{k :unknown} {:unknown false}
      k (or k "wrong-value!!!")]
  k)

;; It works if you use :or (k is bound to false)
(let [{k :unknown
       :or {k "wrong-value!!!"}} {:unknown false}]
  k)

;; 2.6 Binding values to their keys names: :keys, :syms, :strs
;; This is very repetitive:
(let [{x :x y :y} {:x 1 :y 2}]
  (show-separated-by-commas x y))

;; You can write it in a more convenient way:
(let [{:keys [x y]} {:x 1 :y 2}]
  (show-separated-by-commas x y))

(get '(:x 1 :y 2) :x)

(let [{:keys [x y]} '(:x 1 :y 2)]
  (show-separated-by-commas x y))

(let [{x 0 y 1} [1 2]]
  (show-separated-by-commas x y))

(let [{:strs [x y]} {"x" 1 "y" 2}]
  (show-separated-by-commas x y))

(let [{:syms [x y]} {'x 1 'y 2}]
  (show-separated-by-commas x y))

;; Notice how the binding names have to be equal to the keys

;; It also works for records
;; From a previous example -> (defrecord Point [x y])
(let [{x :x y :y} (Point. 1 2)]
  (show-separated-by-commas x y))

(let [{:keys [x y]} (Point. 1 2)]
  (show-separated-by-commas x y))

;; And for maps using strings as keys
(let [{:keys [x y]} {:x 1 "y" 2}]
  (vector x y))

(let [{:keys [x] :strs [y]} {:x 1 "y" 2}]
  (vector x y))

(let [{:keys [x] pepito "y"} {:x 1 "y" 2}]
  (vector x pepito))

;; And for maps using strings as keys
(let [{x "x" y "y"} {"x" 1 "y" 2}]
  (show-separated-by-commas x y))

(let [{:strs [x y]} {"x" 1 "y" 2}]
  (show-separated-by-commas x y))

;; And for maps using symbols as keys
(let [{x 'x y 'y} {'x 1 'y 2}]
  (show-separated-by-commas x y))

(let [{:syms [x y]} {'x 1 'y 2}]
  (show-separated-by-commas x y))

;; 2.7 Rest of key-value pairs: &
(def user-info ["Koko" 47 :address "Sesamo Street 26" :color "blue"])

(let [[name age & {:keys [address color]}] user-info]
  (show-separated-by-commas name age address color))

(defn f [ [name age & {:keys [address color]}] ]
  (show-separated-by-commas name age address color))

(f user-info)

(defn make-tamagotchi [& {:keys [fulness tiredness]
                          :or {fulness 0 tiredness 0}}]
  {:fulness fulness
   :tiredness tiredness})

(make-tamagotchi)

(make-tamagotchi :tiredness 4)

(make-tamagotchi :fulness 4)

(make-tamagotchi :fulness 4 :tiredness 8)

(let [{:keys [fulness tiredness]
       :or {fulness 0 tiredness 0}} '(:fulness 4)]
  (vector fulness tiredness))

;; This can be used to have function with keyword arguments.

;; To learn more:
;; Clojure Programming, Practical Lisp for the Java World. Chas Emerick, Brian Carper, Christophe Grand
;;
;; The complete guide to Clojure destructuring -> http://blog.brunobonacci.com/2014/11/16/clojure-complete-guide-to-destructuring/
;;
;; Pattern Matching vs. Destructuring… to the death! -> http://blog.fogus.me/2011/01/12/pattern-matching-vs-destructuring-to-the-death/
;;
;; Clojure: Destructuring -> http://blog.jayfields.com/2010/07/clojure-destructuring.html
;;
;; Clojure’s Mini-languages -> http://blog.fogus.me/2010/03/23/clojures-mini-languages/
;;
;; Destructuring on ClojureBridge -> https://clojurebridge.github.io/community-docs/docs/clojure/destructuring/
;;
;; Clojure Binding Forms (Destructuring) -> http://clojure.org/special_forms#binding-forms
;;
;; Clojure Destructuring Tutorial and Cheat Sheet -> https://gist.github.com/john2x/e1dca953548bfdfb9844
;;
;; Beautiful Clojure – Destructuring data -> https://www.laliluna.de/articles/2013/010/29/clojure-destructuring.html
;
;; Grover -> http://en.wikipedia.org/wiki/Grover
