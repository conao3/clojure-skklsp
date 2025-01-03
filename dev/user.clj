(ns user
  (:require
   [skklsp.core :as skklsp]))

(alter-var-root #'*warn-on-reflection* (constantly true))

(def server (atom nil))

(defn go []
  ;; (when @server
  ;;   (.stop ^Server @server))
  (->> (skklsp/start-server)
       (reset! server)))
