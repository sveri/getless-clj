(ns de.sveri.getless.layout
  (:require [selmer.parser :as parser]
            [clojure.string :as s]
            [ring.util.response :refer [content-type response]]
            [ring.middleware.anti-forgery :as af]
            [compojure.response :refer [Renderable]]
            [noir.session :as sess]))

(defn merge-flash-messages
  "Expects a map containing keys and a values which will be put into the sessions flash"
  [messages]
  (doseq [m messages]
    (sess/flash-put! (key m) (val m))))

(defn flash-result [message div-class]
  (merge-flash-messages {:flash-message message :flash-alert-type div-class}))

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (content-type
      (->> (assoc params
             (keyword (s/replace template #".html" "-selected")) "active"
             :servlet-context
             (if-let [context (:servlet-context request)]
               ;; If we're not inside a serlvet environment (for
               ;; example when using mock requests), then
               ;; .getContextPath might not exist
               (try (.getContextPath context)
                    (catch IllegalArgumentException _ context)))
             :identity (sess/get :identity)
             :role (sess/get :role)
             :af-token af/*anti-forgery-token*
             :page template
             :registration-allowed? (sess/get :registration-allowed?)
             :captcha-enabled? (sess/get :captcha-enabled?)
             :flash-message (sess/flash-get :flash-message)
             :flash-alert-type (sess/flash-get :flash-alert-type)
             :signup_message ((:localize request) [:user/signup])
             :signin_message ((:localize request) [:user/signin])
             :email_placeholder ((:localize request) [:generic/email])
             :password_placeholder ((:localize request) [:generic/password])
             :change_password_message ((:localize request) [:user/change_password])
             :logout_message ((:localize request) [:generic/logout])
             :activities_message ((:localize request) [:generic/activities])
             :diet_message ((:localize request) [:generic/diet])
             :food_message ((:localize request) [:generic/food])
             :ingredients_message ((:localize request) [:generic/ingredients])
             :weight_progress_message ((:localize request) [:generic/weight-progress])
             :localize-fn (:localize request))
           (parser/render-file (str template))
           response)
      "text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. (str "templates/" template) params))

