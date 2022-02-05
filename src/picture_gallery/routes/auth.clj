(ns picture-gallery.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [picture-gallery.routes.home :refer :all]
            [picture-gallery.util :refer [gallery-path]]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.models.db :as db]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [noir.session :as session]
            [noir.validation :as valid]
            [noir.response :as resp]
            [noir.util.crypt :as crypt])
  (:import java.io.File))

(defn create-gallery-path []
  (let [user-path (File. (gallery-path))]
    (if-not (.exists user-path) (.mkdirs user-path))
    (str (.getAbsolutePath user-path) File/separator)))

(defn format-error [id e]
  (println (.printStackTrace e))
  (str "系统异常，请稍后重试:" (.getMessage e)))

(defn valid? [id pass pass1]
  (valid/rule (valid/has-value? id)
              [:id "名称不能为空"])
  (valid/rule (valid/min-length? pass 5)
              [:pass "密码最少5个字符"])
  (valid/rule (= pass pass1)
              [:pass "两次密码不匹配"])
  (not (valid/errors? :id :pass :pass1)))

(defn error-item [[error]]
  [:div.error error])

(defn control [id label field]
  (list
    (valid/on-error id error-item)
    label field
    [:br]))

(defn registration-page [& [id]]
  (layout/base
    (form-to [:post "/register"]
             (anti-forgery-field)
             (control :id
                      (label "user-id" "用户姓名")
                      (text-field {:tabindex 1} "id" id))
             (control :pass
                      (label "pass" "密码")
                      (password-field {:tabindex 2} "pass"))
             (control :pass1
                      (label "pass1" "重复密码")
                      (password-field {:tabindex 3} "pass1"))
             (submit-button {:tabindex 4} "创建账号"))))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (if (and user (crypt/compare pass (:pass user)))
      (println "loging success" id)
      (session/put! :user id)))
  (resp/redirect "/"))

(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (db/create-user {:id id :pass (crypt/encrypt pass)})
      (session/put! :user id)
      (create-gallery-path)
      (resp/redirect "/")
      (catch Exception e
        (valid/rule false [:id (format-error id e)])
        (registration-page)))
    (registration-page id)))

(defroutes auth-routes
           (GET "/register" []
             (registration-page))
           (POST "/register" [id pass pass1]
             (handle-registration id pass pass1))
           (POST "/login" [id pass]
             (handle-login id pass))
           (GET "/logout" []
             (handle-logout))
           )


