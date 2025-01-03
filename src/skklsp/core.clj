(ns skklsp.core
  (:require
   [clojure.tools.logging :as log]
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [skklsp.handler :as c.handler])
  (:import
   [java.net ServerSocket]
   [java.io InputStream OutputStream])
  (:gen-class))

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
        (nil? m) (do (log/warn "Unkown header form" inpt)
                     args)
        :else (recur (assoc args (keyword k) v))))))

(defn read->string [^InputStream input-stream len]
  (let [ary (byte-array len)]
    (when-not (= -1 (. input-stream read ary))
      (String. ary))))

(defn io-handler [^InputStream input-stream
                  ^OutputStream output-stream]
  (let [header (header-parser input-stream)
        _ (println "Header:" header)
        body (when-let [len (:Content-Length header)]
               (read->string input-stream (parse-long len)))
        _ (println "Body:" body)]
    (when-not (nil? body)         ; nil indicates EOF
      (let [parsed (json/read-str body :key-fn keyword)
            _ (println "Parsed:" parsed)
            res (c.handler/invoke parsed)
            _ (println "Response:" res)
            res-str (json/write-str res)
            _ (println "ResponseStr:" res-str)
            res-bytes (. res-str getBytes)]
        (when res
          (. output-stream write (.. (format "Content-Length: %d\r\n" (count res-bytes))
                                     getBytes))
          (. output-stream write (. "\r\n" getBytes))
          (. output-stream write res-bytes)
          (. output-stream flush)))
      (recur input-stream output-stream))))

(defn start-server [port]
  (log/info "Listening localhost" port)
  (with-open [server-socket (ServerSocket. port)]
    (loop []
      (let [client-socket (. server-socket accept)]
        (println "Connection Accepted")
        (io-handler (io/input-stream client-socket) (io/output-stream client-socket)))
      (recur))))

(defn -main
  "The entrypoint."
  [& args]
  (log/info args))
