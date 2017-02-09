;;;;;;;;;;;;;;;;;;;
;; Collections in Clojure
;;;;;;;;;;;;;;;;;;;

;; Distinctive characteristics
;;;;;;;;;;;;;;;;;;;
;; * They are mainly used in terms of abstractions, not the details of concrete implementations.
;; * They are inmutable and persistent.

;;;;;;;;;;;;;;;;;;;
;; 1. Clojure collection data structures
;;;;;;;;;;;;;;;;;;;
;; 1.1. Vectors
[]
[1 "ab" 3 true]

(vector 1 2 3)

(nth [1 3 6] 1)
; (nth [1 3 6] 4) ;; java.lang.IndexOutOfBoundsException: null

(conj [3 4 5] 1)
(conj [3 4 5] 1 2)

(get [1 "ab" 3 true] 2)

([1 "ab" 3 true] 2)

;; 1.2. Lists
'(2 "a" :c)
(list 3 \t "q")

(conj '(1 2) 3)
(conj '(1 2) 3 4)

(nth '(1 3 6) 1)

;(nth '(1 3 6) 4) ;; java.lang.IndexOutOfBoundsException: null

;('(1 3 6) 2) ; java.lang.ClassCastException: clojure.lang.PersistentList cannot be cast to clojure.lang.IFn


;; 1.3. Maps
{"a" 1 :b :value 1 "hola"}

(hash-map :a 5 :b "hola")

{[1 2] "2" :a {:b 4}}

(def m {:a "hola" :b 5 :c "p"})

(assoc m :d 3)

m

(assoc m :a 3)

(assoc m :a 3 :f 7)

(conj m [:h "pepe"])

(dissoc m :a)

(get {"a" 1 :b :value 1 "hola"} :b)

(get m :a)

(m :a)

(:a m)

(:c {:c 0})

;("a" {"a" 1 :b 5}) ; java.lang.ClassCastException: java.lang.String cannot be cast to clojure.lang.IFn/media/trikitrok/0d7e8f3c-a84a-4aec-9319-054a8b4d873f/MisCosas/Documentos/Clojure/0_MiClojure/CharlasClojure/Collections/collections.clj:74 user/eval4774

({"a" 1 :b 5} "a")


;; 1.4. Sets
#{1 3 2}

(hash-set 1 2 3)

(contains? #{1 2 3} 4)

(conj #{2 3 5} 1 4 5)

(disj #{2 3 5} 3)

(#{2 4 3} 3)
(#{3 4} 5)

(get #{2 4 3} 3)
(get #{3 4} 5)

;;;;;;;;;;;;;;;;;;;
;; 2. Abstractions
;;;;;;;;;;;;;;;;;;;
;; Small approachable APIs, on top of which auxiliary functions are built.
;; From a user point of view, these "core" functions and auxiliary functions are indistinguishable.
;; There are 7 different primary abstractions in which Clojure's data structure implementations participate:
;; * Collection
;; * Sequence
;; * Associative
;; * Indexed
;; * Stack
;; * Set
;; * Sorted

;; 2.1 Collections.
;;;;;;;;;;;;;;;;;;;
;; All data structures in Clojure participate in this abstraction.
;; A collection is a value that can be used with the set of core collection functions: conj, seq, count, empty, =
;; They are polymorphic respect to the concrete type of collection being operated upon.
;; Each operation provides semantics consistent with the constraints of each data structure implementation.

;; empty examples
(empty [1 3 4])
(empty '())
(empty {:f 3 :g 6})
(empty #{3 6})

;; conj examples
(conj [1 2 3] 5)
(conj '(1 2 3) 5)
(conj {:a 1 :v 4} [:b 6])
(conj #{3 6} 6 7 3)

;; seq
(seq [3 4 6])
(seq '(3 4 6))
(seq #{3 4 6})
(seq {:a 1 :v 4})

;; count
(count [3 4 6])
(count '(3 4 6))
(count #{3 4 6})
(count {:a 1 :b 3})

;; coll?
(coll? nil)
(coll? [1 2 3])
(coll? {:language "ClojureScript" :file-extension "cljs"})
(coll? "ClojureScript")

;; Helper functions use them implicitly to be able to work with different data structures
(into [] {1 2, 3 4})
(into {} {1 2, 3 4})

(map identity [:a 1 :v 4])
(map identity {:a 1 :v 4})
;(doc map)


;; 2.2 Sequences.
;;;;;;;;;;;;;;;;;;;
;; The sequence abstractions defines a way to obtain and traverse sequential views over some source of values:
;; either another collection or successive values that are the result of some computation.
;; They involve some more operation in addition to the base provided by the collection abstraction:
;; cons, list*, first, rest, next, lazy-seq

;; Types that are sequable includes:
;; - All Clojure collection types
;; - All Java collections
;; - All Java maps
;; - All java.lang.CharSequences including String
;; - All type that implements java.langIterable
;; - Arrays
;; - nil
;; - Anything that implements Clojure's clojure.lang.Seqable interface

;; Examples of sequable stuff:
(seq (java.util.ArrayList. [1 2]))
(seq "hola")
(seq {})
(seq nil)

;; seq? and seqable?
;; (ClojureScript has seqable but Clojure doesn't)
;; We got this possible definition for Clojure
;; from https://github.com/clojure/core.incubator/blob/master/src/main/clojure/clojure/core/incubator.clj
(defn seqable?
  "Returns true if (seq x) will succeed, false otherwise."
  [x]
  (or (seq? x)
      (instance? clojure.lang.Seqable x)
      (nil? x)
      (instance? Iterable x)
      (.isArray (.getClass ^Object x))
      (string? x)
      (instance? java.util.Map x)))

(seq? nil)
(seqable? nil)

(seq? [])
(seqable? [])

(seq? #{1 2 3})
(seqable? #{1 2 3})

(seq? "ClojureScript")
(seqable? "ClojureScript")

;; Many functions that work with sequences call seq
;; on their argument(s) implicitly
(into [] {1 2, 3 4})
(into {} {1 2, 3 4})

(map identity [:a 1 :v 4])
(map identity {:a 1 :v 4})

;; next vs rest
;(= (next x) (seq (rest x)))

(next [1 2 3])
(rest [1 2 3])

(next [])
(rest [])

;; destructuring head from tail implicitly uses next
(defn head-tail [[head & tail]]
  [head tail])

(head-tail [1 2 3])
(head-tail [1])

;; * Sequences are not iterators
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; They are inmutable persistent collections

;; * Sequences are not lists
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; - Obtaining the length of a sequence carries a cost.
;;
;; - The content of sequences can be computed lazily and actually
;;   realized only when the values involved are accessed.
;;
;; - The computation that is producing values for a lazy sequence
;;   can opt to produce an unlimited progression of those values,
;;   thus making it possible for sequences to be infinite
;;   and therefore uncountable.

;; * Creating seqs
;;;;;;;;;;;;;;;;;;;
;; - Using functions from the sequence library
;;   (most of them return lazy sequences)
;;
;; - Directly with cons or list*
;;   Mostly used for writing macros and to assemble lazy sequences.

(cons 2 (cons 1 [1 2]))
(list* 1 3 4 [1 2])

;; Lazy sequences
;; Their contents are evaluated lazily.
;; Values are produced as the result of a computation performed on demand
;; when a consumer tries to access them.
;; They can be created lazy-seq or using functions
;; from the sequence library (most of them return lazy sequences)

(defn natural-numbers
  ([] (natural-numbers 1))
  ([n]
   (println n)
   (cons n (lazy-seq (natural-numbers (inc n))))))

(def nums (natural-numbers))
(dorun (take 2 nums))
(println "koko")
(dorun (take 5 nums))
(println "koko")
(dorun (take 10 nums))

;; rest is better for laziness that next
;; because next evaluates the head of the tail

;; 2.3 Associative
;; The associative abstraction is shared by data structures
;; that link keys and values in some way.
;; It's defined by four operations: assoc, dissoc, get, contains?

;; The canonical associative data structure is the map.

;; contains? and get also works on sets
(contains? #{3 5} 3)
(contains? #{2 5} 3)

(get #{3 5} 3)
(get #{2 5} 3)

;; assoc and get also work on vectors
(assoc [1 3 2 4] 4 "5")
(get [1 2 3 4] 2)

;; but not on lists
;(assoc '(1 3 2 4) 4 "5") ; java.lang.ClassCastException: clojure.lang.PersistentList cannot be cast to clojure.lang.Associative

(get '(1 2 3 4) 2)

; contains? returns true if there's a value associated with the given key
(contains? [3 5] 3)
(contains? [3 5] 0)

;; For vectors and lists better use the some function
(#{2 4 3} 3)
(#{3 4} 5)

(some #{3} [3 5])
(some #{3} [2 5])

(defn includes? [coll value]
  (not= nil (some #{value} coll)))

(includes? [3 5] 3)
(includes? [3 5] 1)

;; Beware nil values in maps
; How do you know if nil is a value associated to a key
; or the result of not finding anything?
(get {:a 1 :b nil :c "hola"} :b)
(get {:a 1 :b nil :c "hola"} :d)
; You can use a default value
(get {:a 1 :b nil :c "hola"} :d "default")
; But how do you know if your default value is a value associated to a key
; or the result of not finding anything?
(get {:a 1 :b nil :c "default"} :c "default")
(get {:a 1 :b nil :c "default"} :d "default")
;; Better use find
(find {:a 1 :b nil :c "default"} :c)
(find {:a 1 :b nil :c "default"} :d)

;; Maps are functions on keys
({:a 1 :b 3 :c "koko"} :c)

;; And keywords are functions on maps
(:c {:a 1 :b 3 :c "koko"})


;; 2.4 Indexed
;; It consists of only one function nth, which is a specialization of get
;; that throws an exception when dealing with out-of-bounds indices (get returns nil).

;; Vectors, lists and seqs can support it.
(nth [2 3 6] 2)
;(nth [2 3 6] 3) ; java.lang.IndexOutOfBoundsException: null
(nth '(2 3 6) 2)
(nth (seq {:a 5 :b 3}) 1)


;; 2.5 Stack
;; Collections supporting LIFO semantics
;; Clojure doesn't have a distinct stack data structure.
;; It supports a stack abstraction via 3 operations: conj, pop and peek
;; Both lists and vectors can be used as stacks, only differing in where
;; the top of the stack is (where conj can efficiently operate)

;; The 3 functions are consistent operating on the right end of the data structure,
;; so you don't care if it's either a vector or a list.
(def v [1])
(peek v)
(pop v)
(conj v 4)
(peek (conj v 4))

(def ls '(1))
(peek ls)
(pop ls)
(conj ls 4)
(peek (conj ls 4))


;; 2.6 Sets.
;; As we saw sets participate partially in the associative abstraction: get and contains?
;; A sort of degenerate map.
;; Set fundamental operations are in clojure.set name space
(use 'clojure.set)

(clojure.set/intersection #{1 3 5 7} #{3 5 2})
(clojure.set/union #{1 3 5 7} #{3 5 2})
(clojure.set/difference #{1 3 5 7} #{3 5 2})
(clojure.set/subset? #{1 3 5 7} #{1 5})
(clojure.set/superset? #{1 3 5 7} #{1 5})

;; 2.6 Sorted.
;; Collections that participate in the sorted abstraction
;; guarantee that their values will be maintained in a stable
;; ordering that is optionally defined by a predicate or
;; implementation of a special comparator interface.
;; This allows to efficiently obtain in-order and reverse-order seqs
;; over all or a subrange of such collections' values.
;; rseq, subseq, rsubseq (rseq is actually from reversible abstraction)

;; Only maps and sets are available in sorted variants
(sorted-map :a 5 :b "hola")
(sorted-set 1 2 3)

(subseq (sorted-map :a 5 :c "koko" :b "hola") > :a < :c)
(rsubseq (sorted-map :a 5 :c "koko" :b "hola") > :a)

;; Vectors support reversible but not sorted
(rseq [1 2 3])
;(subseq [1 2 3] < 2) ; java.lang.ClassCastException: clojure.lang.PersistentVector cannot be cast to clojure.lang.Sorted

;; Lists none of them
;(rsubseq '(1 2 3) < 2) ; java.lang.ClassCastException: clojure.lang.PersistentVector cannot be cast to clojure.lang.Sorted
;(rseq '(1 2 3)) ; java.lang.ClassCastException: clojure.lang.PersistentList cannot be cast to clojure.lang.Reversible

;; Compare defines the default ascending sort (like starship operator)
(compare 2 1)
(compare 2 2)
(compare 2 3)

(compare "a" "b")
(compare :a :b)

(compare [1 3] [1 1])
(compare [1 3] [1 3])
(compare [1 3] [1 4])

;; Comparators are predicates to define order
(sort < (repeatedly 10 #(rand-int 100)))

(sort-by first < (map-indexed vector "Clojure"))

;; sorted-set-by, sorted-map-by
;; You can pass this factory functions a comparator to sort the resulting data structure
(sorted-set-by > 3 5 8 2 1)
(sorted-map-by #(- (compare %1 %2)) :a 5 :b "hola")

;; If you use < and compare as comparators in the previous examples you get
;; the same results you get using sorted-set and sorted-map, respectively
(sorted-set-by < 3 5 8 2 1)
(sorted-set 3 5 8 2 1)

(sorted-map-by compare :a 5 :b "hola")
(sorted-map :a 5 :b "hola")

;; To learn more:
;; Clojure Programming, Practical Lisp for the Java World. Chas Emerick, Brian Carper, Christophe Grand
;; Great book!! I got most of this from it.
;;
;; Clojure data structures -> http://clojure.org/data_structures
;;
;; Clojure sequences -> http://clojure.org/sequences
;;
;; Collections and Sequences in Clojure -> http://clojure-doc.org/articles/language/collections_and_sequences.html
;;
;; Clojure Data Structures Part 1, Rich Hickey -> https://www.youtube.com/watch?v=ketJlzX-254
;;
;; Clojure Data Structures Part 2, Rich Hickey -> https://www.youtube.com/watch?v=sp2Zv7KFQQ0
