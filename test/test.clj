;; Basic tests for clojure-jsr223
;;
;; Make sure clojure-jsr233.jar and clojure.jar is in your classpath
(use 'clojure.test)

(def sem (javax.script.ScriptEngineManager.))

(def cse (.getEngineByName sem "Clojure"))

(.put cse "foo" "bar")
(is (= "bar" (.eval cse "foo"))
    "put() must set the root-bound var to the correct value")

(def cse2 (.getEngineByName sem "Clojure"))

(is (= "bar" (.eval cse2 "foo"))
    "evaluating the var must return the correct result")

(.put cse2 "foo" "bazz")
(is (= "bazz" (.eval cse2 "foo"))
    "put() must set the thread-local var to the correct value")

(is (= "bazz" (.get cse2 "foo2"))
    "get() must return the correct value for the var (expected, since get() is not implemented yet)")

;; Make sure the changes in cse2 do not affect the binding of the var foo in cse
(is (= "bar" (.eval cse "foo"))
    "evaluating the var must return the correct value")
