(ns picture-gallery.test.handler
  (:use clojure.test
        ring.mock.request
        picture-gallery.handler))

(deftest test-app
  (testing "main route"
    (let [response (app (request :get "/"))]
      (is (= (:status response) 200))
      (is (.contains (:body response) "Hello World"))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= (:status response) 404)))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "hello world"))