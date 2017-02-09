;;--------------
;;
;; Sequence comprehensions
;;
;;--------------

;; Clojure generalizes the notion of list comprehension to sequence comprehension

;; Clojure comprehension uses the for macro

;; It takes a vector of binding-form - coll-expr (generator),
;; plus optional filtering expressions
;; and then yields a sequence of expressions

;; (for [binding-form coll-expr filter-expr? ...] expr)


;; 1. Generators

;; 1.1. An example using only one generator
(def three-digits (seq [1 2 3]))

(for [x1 three-digits]
  x1)

(def three-letters ["A" "B" "C"])

;; 1.2. An example using two generators
(for [x1 three-letters
      x2 three-digits]
  [x1 x2])

;;
;; 2. :when clause
;;

;; It filters the elements that are used in the expression

(for [x1 three-digits
      x2 three-digits
      :when (and (even? x1) (odd? x2))]
  [x1 x2 (* x1 x2)])

(defn neighbors [[x-cell y-cell]]
  (set (for [x (range (dec x-cell) (+ x-cell 2))
             y (range (dec y-cell) (+ y-cell 2))
             :when (not (and (= x x-cell) (= y y-cell)))]
         [x y])))

(neighbors [0 0])

;;
;; 3. :while clause
;;

;; The evaluation continues while its expression holds true

(def integers (iterate inc 0))

(for [n integers
      :while (even? n)] n)

(for [n integers
      :while (odd? n)] n)

;;
;; 4. :let allows you to make bindings with derived values
;;

(for [x1 three-digits
      x2 three-digits]
  (* x1 x2))

(for [x1 three-digits
      x2 three-digits
      :let [y (* x1 x2)]
      :when (> y 5)]
  y)

;; Example from Michiel Borkent answer to a question on Stack Overflow (see Reference 2)
;;---------------------------------
;; Although

(for [i (range 10)
      :let [x (* i 2)]]
  x)

;; is equivalent to:

(for [i (range 10)]
  (let [x (* i 2)]
  x))

;; you can see the difference when used in combination with :when (or :while):

(for [i (range 10)
          :let [x (* i 2)]
          :when (> i 5)]
      [i x])

(for [i (range 10)]
  (let [x (* i 2)]
    (when (> i 5) x)))

;;---------------------------------
;;
;; 5. List comprehensions are more general than filter and map, and can in fact emulate them
;;

;; 5.1 Map
(map #(* % %) (range 1 10))

(for [num (range 1 10)]
  (* num num))

;; 5.2 Filter (using :when)
(filter odd? (range 1 10))

(for [num (range 1 10)
      :when (odd? num)]
  num)

;; 5.3 More than map and filter
;(map #(* % %) (filter #(< % 10) integers)) ; <- can't run!

(for [num integers
      :while (< num 10)
      :when (odd? num)]
  (* num num))

;; References
;; 1. Programming Clojure, 2nd edition.  Stuart Halloway and Aaron Bedra
;; 2. Use of :let modifier in Clojure, Stack Overflow (http://bit.ly/1Iq2Hld)
;; 3. Project Euler: Problem 4 in Clojure, Leonardo Borges (http://bit.ly/1Et2rPz)
;; 4. for - clojure.core in ClojureDocs (https://clojuredocs.org/clojure.core/for)
