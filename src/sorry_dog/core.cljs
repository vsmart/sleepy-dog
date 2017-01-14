(ns sorry-dog.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def initial-state
  {:dog-x 250
   :dog-y 250
   :direction :right
   :is-day true })

(defn setup []
  (q/frame-rate 30)
  initial-state)


(def load-image  (memoize q/load-image))

(defn move-doggo  [state by-x by-y]
  (let  [new-state  (update state :dog-x + by-x)
         newer-state  (update new-state :dog-y + by-y)]
    newer-state))

(def one-move 20)
(def one-neg-move  (* one-move -1))

(defn move-doggo-left  [state]
  (-> state
    (move-doggo one-neg-move 0)
    (assoc :direction :left)))

(defn move-doggo-right  [state]
  (-> state
    (move-doggo one-move 0)
    (assoc :direction :right)))

(defn move-doggo-up  [state]
  (-> state
    (move-doggo 0 one-neg-move)
    (assoc :direction :up)))

(defn move-doggo-down  [state]
  (-> state
    (move-doggo 0 one-move)
    (assoc :direction :down)))

(defn move-after-key-pressed  [state event]
  (let  [key  (:key event)]
    (cond
      (= key :left) (move-doggo-left state)
      (= key :right) (move-doggo-right state)
      (= key :up) (move-doggo-up state)
      (= key :down) (move-doggo-down state)
      :else state)))

(defn update-day [state]
  (if (= (mod (q/frame-count) 60) 0)
    (assoc state :is-day (not (:is-day state)))
    state))

(defn update-state [state]
  (update-day state))

(defn draw-dog [state]
  (q/image (load-image (str "images/" (name (:direction state)) ".png")) (:dog-x state) (:dog-y state)))

(defn draw-status [state]
  (q/text (str state " --- " (q/frame-count)) 20 20))

(defn draw-night [state]
  (q/background 0)
  (draw-dog state)
  (q/fill 255)
  (draw-status state))

(defn draw-day [state]
  (q/background 255)
  (draw-dog state)
  (q/fill 0)
  (draw-status state))

(defn draw-state [state]
  (if (:is-day state)
    (draw-day state)
    (draw-night state)))

(q/defsketch sorry-dog
  :host "sorry-dog"
  :size [500 500]
  :setup setup
  :key-pressed move-after-key-pressed
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])
