(ns de.sveri.getless.service.auth
  (:require [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [redirect]]))

(def ^:const available-roles ["admin" "none"])

(defn admin-access [req] (= "admin" (-> req :session :noir :role)))
(defn loggedin-access [req] (some? (-> req :session :noir :identity)))
(defn unauthorized-access [_] true)
(defn rest-loggedin-access [req] (authenticated? req))

(def rules [{:pattern #"^/admin.*"
             :handler admin-access}
            {:pattern #"^/user/changepassword"
             :handler loggedin-access}
            {:pattern #"^/weight.*"
             :handler loggedin-access}
            {:pattern #"^/activity.*"
             :handler loggedin-access}
            {:pattern #"^/off.*"
             :handler loggedin-access}
            {:pattern #"^/meal.*"
             :handler loggedin-access}
            {:pattern #"^/food.*"
             :handler loggedin-access}
            {:pattern #"^/rest/api/.*"
             :handler loggedin-access}
            {:pattern #"^/user.*"
             :handler unauthorized-access}
            {:pattern #"^/finance/links.*"
             :handler unauthorized-access}
            {:pattern #"^/finance.*"
             :handler loggedin-access}
            {:pattern #"^/goals.*"
             :handler loggedin-access}
            {:pattern #"^/banking.*"
             :handler loggedin-access}
            {:pattern #"^/"
             :handler unauthorized-access}])

(def rest-rules
  [{:pattern #"^/api/.*"
    :handler rest-loggedin-access}])


(defn unauthorized-handler
  [request _]
  (let [current-url (:uri request)]
    (redirect (format "/user/login?next=%s" current-url))))

(def auth-session-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))


