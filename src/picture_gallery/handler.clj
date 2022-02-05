(ns picture-gallery.handler
  (:require [compojure.core :refer [defroutes routes]]
            [compojure.route :as route]
            [noir.util.middleware :as noir-middleware]
            [noir.session :as session]
            [picture-gallery.routes.home :refer [home-routes]]
            [picture-gallery.routes.auth :refer [auth-routes]]
            [picture-gallery.routes.upload :refer [upload-routes]]
            [picture-gallery.routes.gallery :refer [gallery-routes]]
            [taoensso.timbre :refer [debug info warn error]]))

(defn user-page [_]
  (session/get :user))

(defn init []
  (info "picture-gallery is starting"))

(defn destroy []
  (info "picture-gallery is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (noir-middleware/app-handler
    [auth-routes
     home-routes
     upload-routes
     gallery-routes
     app-routes]
    :access-rules [user-page]))
