(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :refer :all]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [noir.session :as session]))

(defn base [& body]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body body]))

(defn make-menu [& items]
  [:div (for [item items] [:div.menuitem item])])

(defn guest-menu []
  (make-menu
   (link-to "/" "主页")
   (link-to "/register" "注册")
   (form-to [:post "/login"]
            (anti-forgery-field)
            (text-field {:placeholder "用户名"} "id")
            (password-field {:placeholder "密码"} "pass")
            (submit-button "login"))))

(defn user-menu [user]
  (make-menu
    (link-to "/" "主页")
    (link-to "/upload" "上传图片")
    (link-to "/logout" (str "退出 " user))))


(defn common [& body]
  (base
    (if-let [user (session/get :user)]
      (user-menu user)
      (guest-menu))
    body))
