(ns untitled1.core)
(defn card-baralho []
  (inc (rand-int 13)))

(defn A-11 [card-baralho]
  (if (= card-baralho 1) 11 card-baralho))

(defn JQK-10 [card-baralho]
  (if (> card-baralho 10) 10 card-baralho))

(defn points-cards [cards]
  (let [cards-out-jqk (map JQK-10 cards)
        cards-A (map A-11 cards-out-jqk)
        points-A-1 (reduce + cards-out-jqk)
        points-A-11 (reduce + cards-A)]
    (if (> points-A-11 21) points-A-1 points-A-11)))

(defn player [player-name]
  (let [card1 (card-baralho)
        card2 (card-baralho)
        cards [card1 card2]
        points (points-cards cards)]
    {:player player-name :cards cards :points points }))

(defn more-cards [player]
  (let [card (card-baralho)
        cards (conj (:cards player) card)
        player-novo (assoc player :cards cards)
        points (points-cards cards)]
    (assoc player-novo :points points)))

(defn player-decision [player]
  (= (read-line) "sim"))

(defn dealer-decision [player-points dealer]
  (let [dealer-points (:points dealer)]
    (if (and (<= dealer-points 21) (<= dealer-points player-points))true)))
(defn read-score []
  (try
    (read-string (slurp "score.txt"))
    (catch Exception _
      0)))

(defn write-score [score]
  (spit "score.txt" (pr-str score)))

(defn update-score [score]
  (let [current-score (read-score)]
    (write-score (+ current-score score))))
(defn down-score [score]
  (let [current-score (read-score)]
    (write-score (- current-score score))))



(defn game [player fn-continue]
  (println "♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤")
  (println (:player player) ":mais cartas ?")
  (if (fn-continue player)
    (let [player-more-cards (more-cards player)]
      (println player-more-cards)
      (recur player-more-cards fn-continue))
    player))

(defn end-game [player dealer]
  (let [player-points (:points player)
        dealer-points (:points dealer)
        player-name (:player player)
        dealer-name (:player dealer)
        message (cond
                  (and (> player-points 21) (> dealer-points 21)) "Os dois passaram de 21"
                  (= player-points dealer-points) "Empate"
                  (> player-points 21) (do (down-score 1) (str dealer-name " ganhou , você perdeu 1 ponto"))
                  (> dealer-points 21) (do (update-score 1) (str player-name " ganhou"))
                  (> player-points dealer-points) (do (update-score 1) (str player-name " ganhou"))
                  (> dealer-points player-points) (do (down-score 1) (str dealer-name " ganhou , você perdeu 1 ponto")))]
    (println player)
    (println dealer)
    (println message)
    (when (> (read-score) 0)
      (println  "Pontuação :" (read-score)))))

(def player-1 (player "Player 1"))
(print player-1)


(println "♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤")
(def dealer (player "Dealer"))
(print dealer)
(println "♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤♢♥♧♤")
(def player-after-game (game player-1 player-decision))
(def dealer-after-game (game dealer (partial dealer-decision (:points player-after-game))))
(end-game player-after-game dealer-after-game)