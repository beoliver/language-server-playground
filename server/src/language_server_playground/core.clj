(ns language-server-playground.core
  (:require [clojure.string :as str]
            [cheshire.core :as json]
            [language-server-playground.rpc :as rpc])
  (:import [java.net ServerSocket Socket]
           [java.io
            InputStream
            InputStreamReader
            BufferedReader
            OutputStream
            OutputStreamWriter
            BufferedWriter])
  (:gen-class))

(declare socket-server handle-client read-json-rpc write-json-rpc)

(defn -main
  [& args]
  (socket-server 9999 rpc/wrapped-rpc))

;;; lets begin by making a super simple socket-server
;;; this will only accept a single connection

(defn socket-server
  "reads JSON-RPC requests sent to port `port`.
   runs `rpc-handler` on the data and writes the
   respnse back to `port`"
  [port rpc-handler]
  (let [^ServerSocket sock (ServerSocket. port)]
    ;; ensure that we can re use this port
    (.setReuseAddress sock true)
    (try
      ;; we now perform blocking actions
      ;; this means that only 1 client can connect
      (let [^Socket client (.accept sock)]
        (handle-client client rpc-handler))
      (finally (.close sock)))))

(defn handle-client
  "takes in a client socket and a rpc handler.
   handles input/output streams for handler.
   A `rpc-handler` should accept a map that contains
  {:request <data> :context <context-map> }
  and return a map
  {:response <data> :context <context-map>}

  if a handler returns `any` value under the `:result` key
  then we will send this data back to the client as a jsonrpc response.
  if the handler returns `null` then we will close the client
  "
  [^Socket client rpc-handler]
  (try
    (let [in (-> client .getInputStream (InputStreamReader. "UTF-8") BufferedReader.)
          out (-> client .getOutputStream (OutputStreamWriter. "UTF-8") BufferedWriter.)]
      (loop [loop-context {}]
        (when-let [rpc-data (read-json-rpc in)]
          ;; if no data then the client has done something strange :(
          (when-let [{:keys [context response]} (rpc-handler {:request rpc-data :context loop-context})]
            (when response
              ;; only write to the clinet if there was a response
              (write-json-rpc out response))
            ;; use the new context if provided
            (recur (or context loop-context))))))
    (finally (.close client))))

(defn read-json-rpc [^BufferedReader in]
  (let [content-length-header (.readLine in)]
    (when content-length-header
      (when (str/starts-with? content-length-header "Content-Length: ")
        (let [[_ length-str] (str/split content-length-header #" ")
              content-length (Long/parseLong length-str)]
          (println (format "expecting content length of %s" content-length))
          (let [content-type-or-newline (.readLine in)]
            (when (str/starts-with? content-length-header "Content-Type: ")
              ;; ignore this line and read the final \r\n
              (.readLine in))
            (let [chars (char-array content-length)]
              (.read in chars 0 content-length)
              (json/parse-string (String. chars) keyword))))))))

(defn write-json-rpc [^BufferedWriter out-stream data]
  (let [s (json/generate-string data)]
    (let [content-length (count s)
          content-length-header (format "Content-Length: %s\r\n" content-length)]
      (doto out-stream
        (.write content-length-header 0 (count content-length-header))
        (.write "\r\n" 0 2)
        (.write s 0 content-length)
        (.flush)))))
