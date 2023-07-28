(ns stream-matcher.internal-test
  (:require [stream-matcher.internal :as sut]
            [clojure.test :as t]))

(t/deftest event-outcome-test
  (t/testing "an event with nothing pending"
    (t/is (= {:pending {:side :left
                        :data-and-time-vec [[:d :t]]}}

             (sut/event-outcome
               ;; the event
               {:side :left
                :data :d
                :time :t}

               ;; nothing pending to be matched
               {}))))

  (t/testing "match without misses"
    (t/testing "a single pending event is matched"
      (t/is (nil?
              (seq (sut/event-outcome
                     ;; the event
                     {:side :right
                      :data :d
                      :time :t}

                     ;; what's pending
                     {:side :left
                      :data-and-time-vec [:d :t]})))))

    (t/testing "multiple events pending"
      (t/is (= {:pending {:side :right
                          :data-and-time-vec [[:d2 :t2]]}}

               (sut/event-outcome
                 ;; the event
                 {:side :left
                  :data :d1
                  :time :t1}

                 ;; what's pending to be matched
                 {:side :right
                  :data-and-time-vec [[:d1 :t1] [:d2 :t2]]})))))

  (t/testing "misses"
    
    ;; misses are always on the opposite side of the event
    
    (t/testing "with match - anything prior to the match is a miss"
      (t/is (= {:pending {:side :left
                          :data-and-time-vec [[:d3 :t3]]}
                :missed {:data-and-time-vec [[:d1 :t1]]}}

               (sut/event-outcome
                 {:side :right
                  :data :d2
                  :time :t2}

                 {:side :left
                  :data-and-time-vec [[:d1 :t1] [:d2 :t2] [:d3 :t3]]}))))

    (t/testing "without match"
      (t/is (= {:pending {:side :right
                          :data-and-time-vec [[:d3 :t3]]}
                :missed {:data-and-time-vec [[:d1 :t1] [:d2 :t2]]}}

               (sut/event-outcome
                 {:side :right
                  :data :d3
                  :time :t3}

                 {:side :left
                  :data-and-time-vec [[:d1 :t1] [:d2 :t2]]})))))

  (t/testing "record the event as pending"
    (t/is (= {:side :left
              :data-and-time-vec [[:d1 :t1] [:d2 :t2]]}

             (sut/event-outcome
               {:side :left
                :data :d2
                :time :t2}

               {:side :left
                :data-and-time-vec [[:d1 :t1]]})))))

(t/deftest timeout-test)
