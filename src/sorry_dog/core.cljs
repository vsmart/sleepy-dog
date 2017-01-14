(ns sorry-dog.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  {:dog-x (/ (q/width) 2)
   :dog-y (/ (q/height) 2)})

(def load-image  (memoize q/load-image))

(defn move-doggo  [by-x by-y state]
  (let  [new-state  (update state :dog-x + by-x)
         newer-state  (update new-state :dog-y + by-y)]
    newer-state))

(def one-move 20)
(def one-neg-move  (* one-move -1))

(defn move-doggo-left  [state]
  (move-doggo one-neg-move 0 state))

(defn move-doggo-right  [state]
  (move-doggo one-move 0 state))

(defn move-doggo-up  [state]
  (move-doggo 0 one-neg-move state))

(defn move-doggo-down  [state]
  (move-doggo 0 one-move state))

(defn move-after-key-pressed  [state event]
  (let  [key  (:key event)]
    (cond
      (= key :left) (move-doggo-left state)
      (= key :right) (move-doggo-right state)
      (= key :up) (move-doggo-up state)
      (= key :down) (move-doggo-down state)
      :else state)))

(defn update-state [state]
  state )

(defn draw-state [state]
  (q/background 255)
  (q/image (load-image "images/dog2.png") (:dog-x state) (:dog-y state))
  (q/fill 0)
  (q/text (str state) 200 20))

(q/defsketch sorry-dog
  :host "sorry-dog"
  :size [500 500]
  :setup setup
  :key-pressed move-after-key-pressed
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])
