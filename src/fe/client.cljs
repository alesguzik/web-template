(ns fe.client
  (:require [reagent.core :as r]))
;; (enable-console-print!)

(defonce app-state (r/atom {:clicks 0}))

(defn main-component []
  [:div {:on-click #(swap! app-state update-in [:clicks] inc)}
   (str "Clicks: " (:clicks @app-state))])

(r/render-component [main-component]
                          (js/document.getElementById "container"))
