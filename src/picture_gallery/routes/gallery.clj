(ns picture-gallery.routes.gallery
  (:require [compojure.core :refer :all]
            [hiccup.element :refer :all]
            [picture-gallery.util :refer [thumb-prefix image-uri thumb-uri]]
            [picture-gallery.models.db :as db]
            [picture-gallery.views.layout :as layout]))

(defn thumbnail-link [{:keys [user_id name]}]
  [:div.thumbnail
   [:a {:href (image-uri user_id name)}
    (image (thumb-uri user_id name))]])

(defn display-gallery [user_id]
  (or
    (not-empty (map thumbnail-link (db/images-by-user user_id)))
    [:p "当前无任何图片"]))

(defn gallery-link [{:keys [user_id name]}]
  [:div.thumbnail
                  [:a {:href (str "/gallery/" user_id)}
                   (image (thumb-uri user_id name))
                   user_id "的图片"]])

(defn show-galleries []
  (map gallery-link (db/get-gallery-previews)))

(defroutes gallery-routes
           (GET "/gallery/:user_id" [user_id] (layout/common (display-gallery user_id))))