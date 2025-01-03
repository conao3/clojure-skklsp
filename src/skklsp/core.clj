(ns skklsp.core
  (:require
   [clojure.tools.logging :as log]
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [skklsp.subr :as c.subr]
   [skklsp.handler :as c.handler])
  (:import
   [java.net ServerSocket]
   [java.io InputStream OutputStream])
  (:gen-class))

(defn io-handler [^InputStream input-stream
                  ^OutputStream output-stream]
  (let [header (c.subr/header-parser input-stream)
        _ (println "Header:" header)
        body (when-let [len (:Content-Length header)]
               (c.subr/read->string input-stream (parse-long len)))
        _ (println "Body:" body)]
    (when-not (nil? body)         ; nil indicates EOF
      (let [parsed (json/read-str body :key-fn keyword)
            _ (println "Parsed:" parsed)
            res (when (:method parsed)
                  (c.handler/invoke (assoc parsed :output-stream output-stream)))
            _ (println "Response:" res)]
        (when res
          (c.subr/write-json output-stream res)))
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
