(ns fe.client
  (:require
   [reagent.core :as reagent]))

;; (enable-console-print!)

(defonce app-state (reagent/atom {:clicks 0}))

(defn click-handler []
  (swap! app-state update-in [:clicks] inc))

(defn label-component []
  [:div {:on-click click-handler}
   (str "Hello world: " (:clicks @app-state))])

(defn main-component []
  [label-component])

(reagent/render-component main-component
                          (js/document.getElementById "container"))
