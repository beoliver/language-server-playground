(ns language-server-playground.git
  (:import [org.eclipse.jgit.lib
            ObjectId
            ObjectInserter
            ObjectInserter$Formatter
            Constants]))

(defn hash-object [text]
  (let [formatter (new ObjectInserter$Formatter)]
    (.getName
     (.idFor formatter Constants/OBJ_BLOB (.getBytes text "UTF-8")))))
