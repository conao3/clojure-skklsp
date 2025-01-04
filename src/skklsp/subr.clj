(ns skklsp.subr
  (:require
   [clojure.data.json :as json])
  (:import
   [java.io InputStream OutputStream]
   [java.nio ByteBuffer]))

(defn input-stream-readline [^InputStream input-stream]
  (->> (loop [b (. input-stream read)
              accum []]
         (cond
           (< b 0) accum
           (= b (byte \return)) (do (. input-stream read) ; drop \n
                                    accum)
           :else (recur (. input-stream read) (conj accum b))))
       byte-array
       String.))

(defn byte-buffer-readline [^ByteBuffer buffer]
  (->> (loop [b (. buffer get)
              accum []]
         (cond
           (< b 0) accum
           (= b (byte \return)) (do (. buffer get) ; drop \n
                                    accum)
           :else (recur (. buffer get) (conj accum b))))
       byte-array
       String.))

(defn header-parser [^ByteBuffer buffer]
  (loop [args {}]
    (let [inpt (byte-buffer-readline buffer)
          [m k v] (re-matches #"(.*): *(.*)" inpt)]
      (cond
        (= "" inpt) args
        (nil? m) (do (println "Unkown header form" inpt)
                     args)
        :else (recur (assoc args (keyword k) v))))))

(defn input-stream-read->string [^InputStream input-stream len]
  (let [ary (byte-array len)]
    (when-not (= -1 (. input-stream read ary))
      (String. ary))))

(defn byte-buffer-read->string [^ByteBuffer buffer len]
  (let [ary (byte-array len)]
    (when-not (= -1 (. buffer get ary))
      (String. ary))))

(defn serialize-json [m]
  (let [m-str (json/write-str m)
        _ (println "Serialized:" m-str)
        m-bytes (. m-str getBytes)]
    (format "Content-Length: %d\r\n\r\n%s" (count m-bytes) m-str)))

(defn str->bytes ^bytes [^String s]
  (. s getBytes))

(defn write-json [^OutputStream output-stream m]
  (let [m-str (json/write-str m)
        _ (println "Serialized:" m-str)]
    (. output-stream write (-> m serialize-json str->bytes))
    (. output-stream flush)))

(defn json-rpc-obj [m]
  (assoc m :jsonrpc "2.0"))
