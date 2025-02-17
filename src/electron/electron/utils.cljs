(ns electron.utils
  (:require [clojure.string :as string]
            ["fs" :as fs]))

(defonce mac? (= (.-platform js/process) "darwin"))
(defonce win32? (= (.-platform js/process) "win32"))
(defonce linux? (= (.-platform js/process) "linux"))

(defonce prod? (= js/process.env.NODE_ENV "production"))
(defonce dev? (not prod?))
(defonce logger (js/require "electron-log"))

(defonce open (js/require "open"))
(defonce fetch (js/require "node-fetch"))

(defn get-file-ext
  [file]
  (last (string/split file #"\.")))

(defn ignored-path?
  [dir path]
  (when (string? path)
    (or
     (some #(string/starts-with? path (str dir "/" %))
           ["." ".recycle" "assets" "node_modules"])
     (some #(string/includes? path (str "/" % "/"))
           ["." ".recycle" "assets" "node_modules"])
     ;; hidden directory or file
     (re-find #"/\.[^.]+" path)
     (re-find #"^\.[^.]+" path)
     (let [path (string/lower-case path)]
       (not
        (some #(string/ends-with? path %)
              [".md" ".markdown" ".org" ".edn" ".css"]))))))

(defn fix-win-path!
  [path]
  (when path
    (if win32?
      (string/replace path "\\" "/")
      path)))

(defn read-file
  [path]
  (when (fs/existsSync path)
    (.toString (fs/readFileSync path))))
