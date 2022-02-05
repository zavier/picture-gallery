(ns picture-gallery.models.schema
  (:require [picture-gallery.models.db :refer :all]
            [clojure.java.jdbc :as jdbc]))

(def users-table-ddl
  (jdbc/create-table-ddl :users
                         [[:id "varchar(32) PRIMARY KEY"]
                          [:pass "varchar(100) NOT NULL COMMENT '密码'"]]))

(def images-table-ddl
  (jdbc/create-table-ddl :images
                         [[:user_id "varchar(32)"]
                         [:name "varchar(100)"]]))

(defn create-users-table []
  (jdbc/db-do-commands db-spec [users-table-ddl]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (jdbc/db-do-commands db-spec [images-table-ddl])))