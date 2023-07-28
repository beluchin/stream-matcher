(ns stream-matcher.internal)

(defn- append-as-pending [event pending]
  {:side (:side event),
   :data-and-time-vec ((fnil conj [])
                       (:data-and-time-vec pending)
                       [(:data event) (:time event)])})

(defn- misses-outcome [event pending] )

(declare pending-outcome-diff-side same-side?)
(defn- pending-outcome [event pending]
  (if (or (not (seq pending)) (same-side? event pending))
    (append-as-pending event pending)
    (pending-outcome-diff-side event pending)))

(defn- pending-outcome-diff-side [event pending])

(defn- same-side? [event pending]
  (= (:side event) (:side pending)))

(defn event-outcome
  "functional API to match *every* event on the stream in order. On a
  stream with events based on independent ids, this API can be used at
  the individual id level.

  Since it matches every event, this API is not suitable for conflated
  streams"

  [{:keys [side data time] :as event}
   {:keys [side data-and-time-vec] :as pending}]

  (-> nil
      (#(if-let [p (pending-outcome event pending)]
          (assoc %1 :pending p)
          %1))
      (#(if-let [m (misses-outcome event pending)]
          (assoc %1 :misses m)
          %1))))
