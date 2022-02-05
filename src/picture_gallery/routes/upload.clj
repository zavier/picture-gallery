(ns picture-gallery.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode]]
            [picture-gallery.views.layout :as layout]
            [noir.io :refer [upload-file resource-path]]
            [noir.session :as session]
            [noir.util.route :refer [restricted]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [picture-gallery.models.db :as db]
            [picture-gallery.util :refer [gallery-path thumb-uri thumb-prefix]]
            [taoensso.timbre :refer [debug info warn error]]
            )
  (:import [java.awt.image AffineTransformOp BufferedImage]
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO
           java.io.File
           )
  )

(def thumb-size 150)

(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance (double ratio) (double ratio))
        transform-op (AffineTransformOp. scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))
    ))

(defn scale-image [file]
  (let [img (ImageIO/read file)
        img-width (.getWidth img)
        img-height (.getHeight img)
        ratio (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)
    ))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
      (scale-image (io/input-stream (str path filename)))
      "jpeg"
      (File. (str path thumb-prefix filename))
      )))

(defn upload-page [info]
    (layout/common
      [:h2 "上传图片"]
      [:p info]
      (form-to {:enctype "multipart/form-data"}
               [:post "/upload"]
               (anti-forgery-field)
               (file-upload :file)
               (submit-button "上传"))))

(defn handle-upload [{:keys [filename] :as file}]
  (info file)
  (upload-page
    (if (empty? filename)
      "请选择上传文件"
      (try
        (upload-file (gallery-path) file)
        (save-thumbnail file)
        (db/add-image (session/get :user) filename)
        (image {:height "150px"}
               (thumb-uri (session/get :user) filename))
        (catch Exception e
          (error e "上传异常")
          (str "上传错误：" (.getMessage e))
          )))))

(defn serve-file [user-id file-name]
  (info (str (gallery-path) File/separator file-name))
  (file-response (str (gallery-path) File/separator file-name)))

(defroutes upload-routes
           (GET "/img/:user-id/:file-name" [user-id file-name] (serve-file user-id file-name))
           (GET "/upload" [info] (restricted (upload-page info)))
           (POST "/upload" [file] (restricted (handle-upload file))))

