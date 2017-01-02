(ns de.sveri.getless.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-uuid-utils.core :as uuid]
            [de.sveri.getless.helper :as h]))


;;; Some non-DB state
(def state (atom {:show-younguns false}))

;;; Uber component, contains/controls stuff and younguns.
(defn uber
      []
      [:div "er"])


(defn ^:export main []
      (reagent/render-component (fn [] [uber]) (h/get-elem "app")))
