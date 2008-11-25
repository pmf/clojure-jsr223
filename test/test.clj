;; Basic tests for clojure-jsr223
;;
;; Make sure clojure-jsr233.jar and clojure.jar is in your classpath
(defmacro my-assert [assertion-message predicate]
  `(try
     (do
       (assert ~predicate)
       (println (str "OK    : " ~assertion-message)))
     (catch Exception e#
       (println (str "FAILED: " ~assertion-message)))))

(def sem (javax.script.ScriptEngineManager.))

(def cse (.getEngineByName sem "Clojure"))

(.put cse "foo" "bar")
(my-assert "put() did not set the root-bound var to the correct value"
        (= "bar" (.eval cse "foo")))

(def cse2 (.getEngineByName sem "Clojure"))

(my-assert "evaluating the var did not return the correct result"
        (= "bar" (.eval cse2 "foo")))

(.put cse2 "foo" "bazz")
(my-assert "put() did not set the thread-local var to the correct value"
        (= "bazz" (.eval cse2 "foo")))

(my-assert "get() did not return the correct value for the var (expected, since get() is not implemented yet)"
        (= "bazz" (.get cse2 "foo2")))

;; Make sure the changes in cse2 do not affect the binding of the var foo in cse
(my-assert "evaluating the var die not return the correct value"
        (= "bar" (.eval cse "foo")))
