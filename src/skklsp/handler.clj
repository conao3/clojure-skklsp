(ns skklsp.handler
  (:require
   [clojure.core.match :as match]
   [skklsp.subr :as c.subr]))

(def PARSE_ERROR -32700)
(def INVALID_REQUEST -32600)
(def METHOD_NOT_FOUND -32601)
(def INVALID_PARAMS -32602)
(def INTERNAL_ERROR -32603)

(def request-id (atom 0))

(defn initialize [_req]
  {:result
   {:capabilities
    {:executeCommandProvider
     {:commands ["inputKey"]}}}})

(defn shutdown [_req]
  {:result nil})

(defn workspace--executeCommand [{:keys [params]}]
  (let [{:keys [command arguments]} params]
    (case command
      "inputKey"
      (let [[key] arguments]
        {:result [{:command "insert" :arguments [key]}]})
      (do
        (println "executeCommand" "unknown command" command)
        nil))))

(def handlers {"initialize" initialize
               "shutdown" shutdown
               "workspace/executeCommand" workspace--executeCommand})

(defn invoke [{:keys [id method] :as req}]
  (let [fn (get handlers method)
        {:keys [result error] :as res}
        (if fn
          (fn req)
          {:error
           {:code METHOD_NOT_FOUND
            :message (str "Method not found: " method)}})]
    (when (and res id)
      (if error
        (c.subr/json-rpc-obj {:id id :error error})
        (c.subr/json-rpc-obj {:id id :result result})))))
