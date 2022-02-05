(ns picture-gallery.util
  (:require [noir.session :as session]
            [hiccup.util :refer [url-encode]])
  (:import java.io.File))

(def thumb-prefix "thumb_")

(def galleries "galleries")

(defn gallery-path []
  (str galleries File/separator (session/get :user)))

(defn image-uri [user-id file-name]
  (str "/img/" user-id File/separator (url-encode file-name)))

(defn thumb-uri [user-id file-name]
  (image-uri user-id (str thumb-prefix file-name)))