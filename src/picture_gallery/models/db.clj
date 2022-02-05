(ns picture-gallery.models.db
  (:require [clojure.java.jdbc :as jdbc])
  (:import (com.alibaba.druid.pool DruidDataSource)))

(def db-spec
  {:datasource
   (doto (DruidDataSource.)
     (.setUrl "jdbc:mysql://localhost:3306/gallery?useSSL=false")
     (.setUsername "root")
     (.setPassword "mysqlroot")
     (.setFilters "stat")
     (.setMaxActive 20)
     (.setInitialSize 1)
     (.setMaxWait 6000)
     (.setMinIdle 1)
     (.setTimeBetweenEvictionRunsMillis 60000)
     (.setMinEvictableIdleTimeMillis 300000)
     (.setTestWhileIdle true)
     (.setTestOnBorrow false)
     (.setTestOnReturn false)
     (.setPoolPreparedStatements true)
     (.setMaxOpenPreparedStatements 20)
     (.setAsyncInit true)
     (.init))})

(defn add-image [user_id name]
   (jdbc/with-db-transaction [t-con db-spec]
                             (if (empty? (jdbc/query t-con ["SELECT * FROM images WHERE user_id = ? AND name = ?" user_id name]))
                                (jdbc/insert! t-con :images {:user_id user_id :name name})
                                (throw (Exception. "已上传过同名文件"))
                                )))
(defn images-by-user [user_id]
   (jdbc/query db-spec ["SELECT * FROM images WHERE user_id = ?" user_id]))
(defn get-gallery-previews []
   (jdbc/query db-spec ["SELECT * FROM images"]))



(defn get-user [id]
   (jdbc/query db-spec ["SELECT * FROM users WHERE id = ?" id]))

(defn create-user [user]
   (jdbc/insert! db-spec :users user))


