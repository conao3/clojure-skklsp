(ns skklsp.handler
  (:require
   [clojure.core.match :as match]
   [clojure.string :as str]
   [skklsp.subr :as c.subr]
   [skklsp.kana :as c.kana]))

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

(def input-queue (atom []))

(defn input-key [key]
  (println "input-key" @input-queue key)
  (let [prev-inpt (apply str @input-queue)
        _ (swap! input-queue #(conj % key))
        inpt (apply str @input-queue)
        res (c.kana/kana-rule inpt)
        candidate-count (if res
                          1
                          (->> c.kana/kana-rule
                               (filter (fn [[key _]] (str/starts-with? key inpt)))
                               (bounded-count 2)))]
    (when (or res (= 0 candidate-count))
      (reset! input-queue []))
    (cond
      res [{:command "delete-backward" :arguments [(count prev-inpt)]}
           {:command "insert" :arguments [res]}]
      (> candidate-count 0)
      [{:command "insert" :arguments [key]}]
      :else [{:command "delete-backward" :arguments [(count prev-inpt)]}
             {:command "insert" :arguments [key]}])))

(defn workspace--executeCommand [{:keys [params]}]
  (let [{:keys [command arguments]} params]
    (case command
      "inputKey"
      (let [[key] arguments]
        {:result (input-key key)})
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
