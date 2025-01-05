(ns skklsp.handler
  (:require
   [clojure.string :as str]
   [skklsp.kana :as c.kana]
   [skklsp.subr :as c.subr]))

(def PARSE_ERROR -32700)
(def INVALID_REQUEST -32600)
(def METHOD_NOT_FOUND -32601)
(def INVALID_PARAMS -32602)
(def INTERNAL_ERROR -32603)

;; (def request-id (atom 0))
;; (def input-queue (atom []))

(def state
  "{:session-key {:request-id 0
                  :input-queue []}}"
  (atom {}))

(defn initialize [{:keys [session-key]}]
  (swap! state assoc session-key {:request-id 0
                                  :input-queue []})
  {:result
   {:capabilities
    {:executeCommandProvider
     {:commands ["inputKey"]}}}})

(defn shutdown [_req]
  {:result nil})

(defn input-key [session-key key]
  (let [prev-queue (get-in @state [session-key :input-queue])
        prev-inpt (apply str prev-queue)
        _ (println "input-key" prev-queue key)
        _ (swap! state update-in [session-key :input-queue] conj key)
        inpt-queue (get-in @state [session-key :input-queue])
        inpt (apply str inpt-queue)
        res (c.kana/kana-rule inpt)
        candidate-count (if res
                          1
                          (->> c.kana/kana-rule
                               (filter (fn [[key _]] (str/starts-with? key inpt)))
                               (bounded-count 2)))]
    (cond
      (> candidate-count 1)
      [{:command "insert" :arguments [key]}]

      (> candidate-count 0)
      (do
        (swap! state assoc-in [session-key :input-queue] [])
        [{:command "delete-backward" :arguments [(count prev-inpt)]}
         {:command "insert" :arguments [res]}])

      :else
      (do
        (swap! state assoc-in [session-key :input-queue] [key])
        [{:command "delete-backward" :arguments [(count prev-inpt)]}
         {:command "insert" :arguments [key]}]))))

(defn workspace--executeCommand [{:keys [params session-key]}]
  (let [{:keys [command arguments]} params]
    (case command
      "inputKey"
      (let [[key] arguments]
        {:result (input-key session-key key)})
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
