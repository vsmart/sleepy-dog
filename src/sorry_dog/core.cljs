(ns sorry-dog.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def initial-state
  {:dog-x 0
   :dog-y 0
   :direction :right
   :is-day true
   :food-x 40
   :food-y 100
   :game-state :play
   })

(defn setup []
  (q/frame-rate 30)
  initial-state)

(def load-image  (memoize q/load-image))

(defn move-doggo  [state by-x by-y]
  (let [new-state  (update state :dog-x + by-x)
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

(defn move-after-key-pressed [state event]
  (let  [key (:key event)]
    (cond
      ; dog doesn't move at night
      (not (:is-day state)) state
      (= key :left) (move-doggo-left state)
      (= key :right) (move-doggo-right state)
      (= key :up) (move-doggo-up state)
      (= key :down) (move-doggo-down state)
      :else state)))

(defn update-day-time [state]
  (if (= (mod (q/frame-count) 30) 0)
    (assoc state :is-day (not (:is-day state)))
    state))

(defn update-dog-food [state]
  (if (and
        (= (:dog-x state) (:food-x state)
        (= (:dog-y state) (:food-y state))))
    (assoc state :game-state :win)
    state))

(defn update-state [state]
  (-> state
    (update-day-time)
    (update-dog-food)))

(defn draw-food [state]
  (q/image (load-image (str "images/food-" (if (:is-day state) "day" "night") ".png")) (:food-x state) (:food-y state)))

 (defn draw-dog [state]
  (q/image
    (load-image
      (str
        "images/"
        (if (:is-day state)
          ""
          "night-")
        (name (:direction state))
        ".png"))
  (:dog-x state) (:dog-y state)))

(defn draw-status [state]
  (q/text (str state " --- " (q/frame-count)) 20 20))

(defn draw-night [state]
  (q/background 18 15 83)
  (draw-dog state)
  (draw-food state)
  (q/fill 255)
  (draw-status state))

(defn draw-day [state]
  (q/background 255)
  (draw-dog state)
  (draw-food state)
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
