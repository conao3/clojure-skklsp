(ns skklsp.kana)

(defn compile [obj]
  ((:compiler obj) obj))

(defn table-compiler [obj]
  (->> (:y obj)
       (mapcat (fn [[key val]]
                 (map (fn [x v] [(str x key) v]) (:x obj) val)))
       (filter second)
       (into {})))

(def basic-rule
  {:compiler table-compiler
   :x ["" "k" "s" "t" "n" "h" "f" "m" "y" "r" "w"]
   :y {"a" ["あ" "か" "さ" "た" "な" "は" "ふぁ" "ま" "や" "ら" "わ"]
       "i" ["い" "き" "し" "ち" "に" "ひ" "ふぃ" "み" nil "り" nil]
       "u" ["う" "く" "す" "つ" "ぬ" "ふ" "ふ" "む" "ゆ" "る" nil]
       "e" ["え" "け" "せ" "て" "ね" "へ" "ふぇ" nil "め" "れ" nil]
       "o" ["お" "こ" "そ" "と" "の" "ほ" "ふぉ" "も" "よ" "ろ" "を"]}})

;;  	k 	s 	t 	n 	h 	f 	m 	y 	r 	w
;; z 	かん 	さん 	たん 	なん 	はん 	ふぁん 	まん 	やん 	らん 	わん
;; n 	かん 	さん 	たん 	ん 	はん 	ふぁん 	* 	やん 	らん 	わん
;; k 	きん 	しん 	ちん 	にん 	ひん 	ふぃん 	みん 		りん 	うぃん
;; j 	くん 	すん 	つん 	ぬん 	ふん 	ふん 	むん 	ゆん 	るん
;; d 	けん 	せん 	てん 	ねん 	へん 	ふぇん 	めん 		れん 	うぇん
;; l 	こん 	そん 	とん 	のん 	ほん 	ふぉん 	もん 	よん 	ろん 	うぉん

(def azik-youon-rule
  {:compiler compile-with-basic-rule
   :x ["" "k" "s" "t" "n" "h" "f" "m" "y" "r" "w"]
   :y ["z" "n" "k" "j" "d" "l"]}
)
