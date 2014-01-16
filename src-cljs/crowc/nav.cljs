(ns crowc.nav
  (:require [cljs.core.async :refer [put!]]
            [domina.events :refer [listen!]]
            [crowc.picking :as picking]))


(let [movement-speed 1.0
      look-speed 1.0
      heading-prev (atom nil)
      mouse (atom nil)
      mouse-prev (atom nil)
      mouse-button-pressed (atom false)
      keys-pressed (atom #{})
      key-codes {8 "Backspace" 9 "Tab" 13 "Enter" 16 "Shift" 17 "Ctrl" 18 "Alt" 19 "Pause/Break" 20 "Caps Lock" 27 "Esc" 32 "Space" 33 "Page Up" 34 "Page Down" 35 "End" 36 "Home" 37 "Left" 38 "Up" 39 "Right" 40 "Down" 45 "Insert" 46 "Delete" 48 "0" 49 "1" 50 "2" 51 "3" 52 "4" 53 "5" 54 "6" 55 "7" 56 "8" 57 "9" 65 "A" 66 "B" 67 "C" 68 "D" 69 "E" 70 "F" 71 "G" 72 "H" 73 "I" 74 "J" 75 "K" 76 "L" 77 "M" 78 "N" 79 "O" 80 "P" 81 "Q" 82 "R" 83 "S" 84 "T" 85 "U" 86 "V" 87 "W" 88 "X" 89 "Y" 90 "Z" 91 "Windows" 93 "Right Click" 96 "Numpad 0" 97 "Numpad 1" 98 "Numpad 2" 99 "Numpad 3" 100 "Numpad 4" 101 "Numpad 5" 102 "Numpad 6" 103 "Numpad 7" 104 "Numpad 8" 105 "Numpad 9" 106 "Numpad *" 107 "Numpad +" 109 "Numpad -" 110 "Numpad ." 111 "Numpad /" 112 "F1" 113 "F2" 114 "F3" 115 "F4" 116 "F5" 117 "F6" 118 "F7" 119 "F8" 120 "F9" 121 "F10" 122 "F11" 123 "F12" 144 "Num Lock" 145 "Scroll Lock" 182 "My Computer" 183 "My Calculator" 186 ";" 187 "=" 188 " " 189 "-" 190 "." 191 "/" 192 "`" 219 "[" 220 "\\" 221 "]" 222 "'"}
      key-config {16 :slow
                  17 :fast
                  38 :look-up
                  40 :look-down
                  37 :look-left
                  39 :look-right
                  87 :forward
                  83 :reverse
                  65 :slide-left
                  68 :slide-right
                  82 :rise
                  70 :fall
                  81 :roll-left
                  69 :roll-right}
      action {:slow [:speed / 5.0]
              :fast [:speed * 5.0]
              :forward [:forward + 1.0]
              :reverse [:forward - 1.0]
              :slide-left [:right - 1.0]
              :slide-right [:right + 1.0]
              :rise [:rise + 1.0]
              :fall [:rise - 1.0]
              :look-up [:pitch + 1.0]
              :look-down [:pitch - 1.0]
              :look-left [:yaw + 1.0]
              :look-right [:yaw - 1.0]
              :roll-left [:roll + 1.0]
              :roll-right [:roll - 1.0]}
      effect {:speed 1.0
              :forward 0.0
              :right 0.0
              :rise 0.0
              :pitch 0.0
              :yaw 0.0
              :roll 0.0}]

  (defn- key-action
    [t acc k]
    (if-let [[action op modifier] (action (key-config k))]
      (update-in acc [action] op
                 (if (= action :speed)
                   modifier
                   (* t modifier)))
      acc))

  (defn- mouse-action
    [canvas acc]
    (if (and @mouse-prev @mouse-button-pressed (not= 0 @mouse-button-pressed))
      (-> acc
          (update-in [:yaw] +
                     (/ (- (:clientX @mouse-prev) (:clientX @mouse))
                        (.-offsetWidth canvas) 0.2))
          (update-in [:pitch] +
                     (/ (- (:clientY @mouse-prev) (:clientY @mouse))
                        (.-offsetWidth canvas) 0.2)))
      acc))

  (defn- input
    [canvas t]
    (mouse-action canvas
     (reduce (partial key-action t) effect @keys-pressed)))

  (defn- process-input
    "Returns the movement of obj in response to user inputs, or nil if none occurred"
    [canvas obj t]
    (let [{:keys [speed forward right rise pitch yaw roll]} (input canvas t)]
      (when (or (some (complement zero?) [speed forward right rise])
                (nil? @heading-prev)
                (not= [pitch yaw roll] @heading-prev))
        (reset! heading-prev [pitch yaw roll])
        (.multiply (.-quaternion obj)
                   (.normalize (js/THREE.Quaternion. pitch yaw roll)))
        (.translateOnAxis obj
                          (.normalize (js/THREE.Vector3. right forward rise))
                          (* t speed movement-speed))
        (.setPosition (.-matrix obj)
                      (.-position obj))
        (.makeRotationFromQuaternion (.-matrix obj)
                                     (.-quaternion obj))
        (set! (.-matrixWorldNeedsUpdate obj) true)
        [(.toArray (.-position obj)) (.toArray (.-quaternion obj))])))

  (defn- process-picking
    "Returns picking event if the mouse interacts with the scene, or nil"
    [canvas obj scene intersected]
    (when @mouse
      (reset! mouse-prev @mouse)
      (picking/pick obj scene
                    (dec (/ (:clientX @mouse) (.-clientWidth canvas) 0.5))
                    (inc (- (/ (:clientY @mouse) (.-clientHeight canvas) 0.5)))
                    intersected)))

  (defn update
    "Returns a vector of the results of movement and picking over time t
    where obj is controlled by user input (usually this will be the camera)"
    [canvas obj camera scene t intersected]
    [(process-input canvas obj t)
     (process-picking canvas camera scene intersected)])

  (defn attach
    [canvas intersected selected]
    (listen! js/document :mouseup
             (fn on-mouse-up [e]
               (reset! mouse-button-pressed false)))
    (doto canvas
      (listen! :keydown
               (fn on-key-down [e]
                 (domina.events/prevent-default e)
                 (swap! keys-pressed conj (:keyCode e))))
      (listen! :keyup
               (fn on-key-up [e]
                 (swap! keys-pressed disj (:keyCode e))))
      (listen! :mousemove
               (fn on-mouse-move [e]
                 (reset! mouse e)))
      (listen! :mousedown
               (fn on-mouse-down [e]
                 (reset! mouse-button-pressed (:button e))
                 (domina.events/prevent-default e)))
      (listen! :mouseover
               (fn on-mouse-over [e]
                 (.focus canvas)))
      (listen! :blur
               (fn on-blur [e]
                 (.log js/console "onblur")
                 (reset! keys-pressed #{})
                 (reset! mouse-button-pressed false)))
      (listen! :contextmenu
               (fn on-context-menu [e]
                 (domina.events/prevent-default e)
                 (domina.events/stop-propagation e)
                 false))
      (listen! :mousewheel
               (fn on-mouse-wheel [e]
                 (.log js/console e))))))

#_(let [c (chan)]
    (doseq [et [:mousemove :mousedown :mouseout]]
      (listen! canvas et
               (fn on-event [e]
                 (put! c e))))
    (go (while true
          (let [e (<! c)]
            (do
              (reset! x (.-pageX (.getBrowserEvent (domina.events/raw-event e))))
              (.log js/console @x)
              )))))
