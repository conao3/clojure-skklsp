(ns skklsp.core
  (:require
   [clojure.data.json :as json]
   [clojure.tools.logging :as log]
   [skklsp.handler :as c.handler]
   [skklsp.subr :as c.subr])
  (:import
   [java.net InetSocketAddress]
   [java.nio ByteBuffer]
   [java.nio.channels SelectionKey ServerSocketChannel SocketChannel]
   [java.nio.channels.spi AbstractSelector SelectorProvider])
  (:gen-class))

(defn input-handler [^ByteBuffer buffer]
  (let [header (c.subr/header-parser buffer)
        _ (println "Header:" header)
        body (when-let [len (:Content-Length header)]
               (c.subr/byte-buffer-read->string buffer (parse-long len)))
        _ (println "Body:" body)]
    (when-not (nil? body)         ; nil indicates EOF
      (let [parsed (json/read-str body :key-fn keyword)
            _ (println "Parsed:" parsed)
            res (when (:method parsed)
                  (c.handler/invoke parsed))
            _ (println "Response:" res)]
        res))))

(defn connection-open? [{:keys [^AbstractSelector selector
                                ^ServerSocketChannel server-socket]}]
  (and (. selector isOpen)
       (. server-socket isOpen)))

(defn connection-close [{:keys [^AbstractSelector selector
                                ^ServerSocketChannel server-socket]}]
  (when (. selector isOpen)
    (. selector close))
  (when (. server-socket isOpen)
    (. server-socket close)))

(defn accept-input-1 [{:keys [^AbstractSelector selector
                              ^ServerSocketChannel server-socket
                              ^ByteBuffer buffer]}]
  (when (pos? (. selector select))
    (let [selected-keys (. selector selectedKeys)]
      (doseq [^SelectionKey key (seq selected-keys)]
        (try
          (. selected-keys remove key)
          (cond
            (. key isAcceptable)
            (let [client-socket (. server-socket accept)]
              (println "Connection Accepted" (str (. client-socket getRemoteAddress)))
              (. client-socket configureBlocking false)
              (. client-socket register selector SelectionKey/OP_READ))

            (. key isReadable)
            (let [^SocketChannel client-socket (. key channel)
                  bytes-read (. client-socket read buffer)]
              (if (neg? bytes-read)
                (do
                  (println "Connection Closed" (str (. client-socket getRemoteAddress)))
                  (. key cancel)
                  (. client-socket close))
                (do
                  (. buffer flip)

                  (when-let [res (input-handler buffer)]
                    (. buffer clear)
                    (. buffer put (-> res c.subr/serialize-json c.subr/str->bytes))
                    (. buffer flip)
                    (. client-socket write buffer))

                  (. buffer clear)))))
          (catch Exception e
            (println "Error" e)
            (when-let [client-socket (. key channel)]
              (try
                (. client-socket close)
                (catch Exception _ nil)))))))))

(defn accept-input [args]
  (let [buffer (ByteBuffer/allocate 10240)]
    (loop []
      (when (connection-open? args)
        (accept-input-1 (assoc args :buffer buffer)))
      (recur))))

(defn start-server [port]
  (println "Listening localhost" port)
  (let [selector (. (SelectorProvider/provider) openSelector)
        server-socket (ServerSocketChannel/open)]
    (. server-socket configureBlocking false)
    (.. server-socket socket (bind (InetSocketAddress. port)))
    (. server-socket register selector SelectionKey/OP_ACCEPT)

    {:selector selector
     :server-socket server-socket}))

(defn -main
  "The entrypoint."
  [& args]
  (log/info args))
