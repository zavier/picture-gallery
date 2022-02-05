(defproject picture-gallery "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [compojure "1.6.2"]
                 [hiccup "1.0.5"]
                 [ring-server "0.5.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [mysql/mysql-connector-java "8.0.25"]
                 [com.alibaba/druid "1.2.8"]
                 [lib-noir "0.9.9"]
                 ]
  :plugins [[lein-ring "0.12.6"]]
  :ring {:handler picture-gallery.handler/app
         :init    picture-gallery.handler/init
         :destroy picture-gallery.handler/destroy}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring/ring-mock "0.4.0"] [ring/ring-devel "1.9.5"]]}})