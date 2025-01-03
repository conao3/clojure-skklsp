(ns skklsp.handler)

(def PARSE_ERROR -32700)
(def INVALID_REQUEST -32600)
(def METHOD_NOT_FOUND -32601)
(def INVALID_PARAMS -32602)
(def INTERNAL_ERROR -32603)

(defn initialize [_req]
  {:result
   {:capabilities {}}})

(def handlers {"initialize" initialize})

(defn invoke [{:keys [id method] :as req}]
  (let [fn (get handlers method)
        {:keys [result error] :as res}
        (if fn
          (fn req)
          (do
            (println "Method not found:" method)
            {:error
             {:code METHOD_NOT_FOUND
              :message (str "Method not found: " method)}}))]
    (when (and res id)
      (if error
        {:jsonrpc "2.0" :id id :error error}
        {:jsonrpc "2.0" :id id :result result}))))
