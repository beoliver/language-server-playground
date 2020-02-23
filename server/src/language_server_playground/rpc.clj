(ns language-server-playground.rpc
  (:require [language-server-playground.git :as git]))

(defmulti rpc (fn [{:keys [method] :as data}] method))

(defn wrapped-rpc [{:keys [context request] :as data}]
  (when-let [result-data (rpc (assoc request :context context))]
    {:context (or (:context result-data) (:context data))
     :response (when (:result result-data)
                 {:id (:id request)
                  :jsonrpc (:jsonrpc request)
                  :result (:result result-data)})}))


;;; rpc handlers

(defmethod rpc "initialize"
  [{:keys [context params]}]
  (let [result {:capabilities {:textDocumentSync {:openClose true :change 2}
                               :hoverProvider true}
                :serverInfo {:name "Bens Language Server"
                             :version "0.0.1"}}]
    {:context context :result result}))

(defmethod rpc "shutdown"
  [{:keys [context params]}])

(defmethod rpc "textDocument/didChange"
  [{:keys [context params] :as data}]
  (println "document did change...")
  (let [{:keys [textDocument contentChanges]} params
        {:keys [uri version]} textDocument
        current (get context uri)]
    (print (format "current document is %s" current))
    (print (format "updates are %s" contentChanges)))
  {:context context})

(defmethod rpc "textDocument/didOpen"
  [{:keys [context params] :as data}]
  (println "document didOpen...")
  (let [{:keys [uri languageId version text] :as document} (:textDocument params)]
    {:context (assoc context uri {:text document :hash (git/hash-object text)})}))

(defmethod rpc "textDocument/hover"
  [{:keys [context params] :as data}]
  (println "document hover...")
  {:context context :result {:contents "YOLO"}})

(defmethod rpc "$/cancelRequest"
  [{:keys [context params] :as data}]
  (println "cancel request...")
  {:context context})

(defmethod rpc :default
  [{:keys [context params method] :as data}]
  (println (format "unhandled '%s'" method))
  {:context context})
