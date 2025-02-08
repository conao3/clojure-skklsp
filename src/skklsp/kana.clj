(ns skklsp.kana)

(defn compile [obj]
  ((:compiler obj) obj))

(defn asis-compiler [obj]
  obj)

(defn merge-compiler [obj]
  (->> obj
       (map compile)
       merge))

(defn table-compiler [obj]
  (->> (:y obj)
       (mapcat (fn [[key val]]
                 (map (fn [x v] [(str x key) v]) (:x obj) val)))
       (filter second)
       (into {})))

(defn compile-with-basic-rule [obj_]
  (let [obj (-> obj_
                (cond-> (string? (:z obj_))
                  (update :z #(repeat (count (:y1 obj_)) %))))
        basic-rule (skklsp.kana/compile skklsp.kana/basic-rule)]
    (->> [(:y1 obj) (:y2 obj) (:z obj)]
         (apply mapcat (fn [yi1 yi2 z]
                         (map (fn [x] [(str x yi1) (get basic-rule (str x yi2)) z])
                              (:x obj))))
         (filter second)
         (map (fn [[k v z]] [k (str v z)]))
         (into {}))))

(def basic-rule
  {:compiler merge-compiler
   :children
   [{:compiler table-compiler
     :x ["" "k" "s" "t" "n" "h" "f" "m" "y" "r" "w"]
     :y {"a" ["あ" "か" "さ" "た" "な" "は" "ふぁ" "ま" "や" "ら" "わ"]
         "i" ["い" "き" "し" "ち" "に" "ひ" "ふぃ" "み" nil "り" nil]
         "u" ["う" "く" "す" "つ" "ぬ" "ふ" "ふ" "む" "ゆ" "る" nil]
         "e" ["え" "け" "せ" "て" "ね" "へ" "ふぇ" nil "め" "れ" nil]
         "o" ["お" "こ" "そ" "と" "の" "ほ" "ふぉ" "も" "よ" "ろ" "を"]}}
    {:compiler table-compiler
     :x ["g" "z" "d" "b" "p"]
     :y {"a" ["が" "ざ" "だ" "ば" "ぱ"]
         "i" ["ぎ" "じ" "ぢ" "び" "ぴ"]
         "u" ["ぐ" "ず" "づ" "ぶ" "ぷ"]
         "e" ["げ" "ぜ" "で" "べ" "ぺ"]
         "o" ["ご" "ぞ" "ど" "ぼ" "ぽ"]}}
    {:compiler asis-compiler
     :x {"zc" "ざ"}}]})

(def azik-youon-rule
  {:compiler merge-compiler
   :children
   [{:compiler compile-with-basic-rule
     :x ["k" "s" "t" "n" "h" "f" "m" "y" "r" "w" "g" "z" "d" "b" "p"]
     :y1 ["z" "n" "k" "j" "d" "l"]
     :y2 ["a" "a" "i" "u" "e" "o"]
     :z "ん"}
    {:compiler asis-compiler
     :x {"nn" "ん"
         "wn" "わん"
         "wk" "うぃん"
         "wd" "うぇん"
         "wl" "うぉん"}}]})

(def azik-niju-boin-rule
  {:compiler merge-compiler
   :children
   [{:compiler compile-with-basic-rule
     :x ["k" "s" "t" "n" "h" "f" "m" "y" "r" "w" "g" "z" "d" "b" "p"]
     :y1 ["q" "h" "w" "p"]
     :y2 ["a" "u" "e" "o"]
     :z ["い" "う" "い" "う"]}
    {:compiler asis-compiler
     :x {"fp" "ふぉー"
         "wp" "うぉー"
         "zv" "ざい"
         "zx" "ぜい"}}]})
