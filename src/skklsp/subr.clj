(ns skklsp.subr
  (:require
   [clojure.data.json :as json])
  (:import
   [java.io InputStream OutputStream]))

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

(defn header-parser [^InputStream input-stream]
  (loop [args {}]
    (let [inpt (input-stream-readline input-stream)
          [m k v] (re-matches #"(.*): *(.*)" inpt)]
      (cond
        (= "" inpt) args
        (nil? m) (do (println "Unkown header form" inpt)
                     args)
        :else (recur (assoc args (keyword k) v))))))

(defn read->string [^InputStream input-stream len]
  (let [ary (byte-array len)]
    (when-not (= -1 (. input-stream read ary))
      (String. ary))))

(defn write-json [^OutputStream output-stream m]
  (let [m-str (json/write-str m)
        _ (println "Serialized:" m-str)
        m-bytes (. m-str getBytes)]
    (. output-stream write (.. (format "Content-Length: %d\r\n" (count m-bytes))
                               getBytes))
    (. output-stream write (. "\r\n" getBytes))
    (. output-stream write m-bytes)
    (. output-stream flush)))

(defn json-rpc-obj [m]
  (assoc m :jsonrpc "2.0"))
