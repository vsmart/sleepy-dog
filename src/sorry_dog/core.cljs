(ns sorry-dog.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def initial-state
  {:dog-x 400
   :dog-y 400
   :direction :up
   :is-day true
   :food-x (rand-int 400)
   :food-y (rand-int 400)
   :game-state :play})

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
  (if (= (mod (q/frame-count) 90) 0)
    (assoc state :is-day (not (:is-day state)))
    state))

(defn update-dog-food [state]
  (if (and
        (and (> (:dog-x state) (- (:food-x state) 20) ) (< (:dog-x state) (+ (:food-x state) 20) ))
        (and (> (:dog-y state) (- (:food-y state) 20) ) (< (:dog-y state) (+ (:food-y state) 20) )))
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
          (name (:direction state))
          "night")
        ".png"))
  (:dog-x state) (:dog-y state) 100 100))

(defn draw-status [state]
  (q/text (str state " --- " (q/frame-count)) 20 20))

(defn draw-night [state]
  (q/background 18 15 83)
  (draw-food state)
  (draw-dog state)
  (q/fill 255))

(defn draw-day [state]
  (q/background 255)
  (draw-food state)
  (draw-dog state)
  (q/fill 0))

(defn draw-play-state [state]
  (if (:is-day state)
    (draw-day state)
    (draw-night state)))

(defn draw-win-state [state]
  (q/background 206 70 145)
  (q/image (load-image (str "images/done.png")) 300 150 100 120)
  (q/text-size 30)
  (q/text "you ate the food, well done sleepy dog." 50 180 200 200))

(defn draw-state [state]
  (cond
    (= :win (:game-state state)) (draw-win-state state)
    (= :play (:game-state state)) (draw-play-state state)))

(q/defsketch sorry-dog
  :host "sorry-dog"
  :size [500 500]
  :setup setup
  :key-pressed move-after-key-pressed
  :update update-state
  :draw draw-state
  :middleware [m/fun-mode])
