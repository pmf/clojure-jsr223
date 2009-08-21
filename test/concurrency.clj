;; SCENARIO 1
;; Using 2 ClojureScriptEngine-instances from 2 separate threads must result in
;; concurrent execution.

(defn operation [n]
  "Simulate a long blocking operation."
  (do
    (println n "started")
    (doseq [i (range 10)] (do
                            (Thread/sleep 10000)
                            (println n i)))
    (println n "finished")))

(def engines (map #(doto (de.torq.clojure.jsr223.ClojureScriptEngine.)
                     (.put "engine-name" %1)
                     (.put "operation" operation))
                  ["1" "2"]))

(def latch (java.util.concurrent.CountDownLatch. 1))

(let [[thread-1 thread-2] (map #(Thread.
                                  (fn []
                                    (do
                                      (.await latch)
                                      (.eval %1 "(operation engine-name)"))))
                               engines)]
  (.start thread-1)
  (.start thread-2)
  (.countDown latch))

