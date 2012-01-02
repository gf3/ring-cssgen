(ns ring.middleware.cssgen
  (:use clojure.tools.namespace
        [clojure.string :only [join]]))

; <amalloy> anyway, a short-term solution is like...just use an atom. but
;           a long-term solution is probably...realize you don't want to solve this
;           problem
; <amalloy> (defn dyn-routes [init] (let [a (atom init)] [a (fn [req] (let [current-routes @a] (...)))]))
; <amalloy> now dyn-routes returns a pair [routes-atom, handler]
; <amalloy> install the handler wherever you want, and save the atom for later changing
; <amalloy> but *really* it would be more composable to wrap, not a list of
;           routes, but an underlying handler. maybe that handler is just some routes,
;           maybe it's something else. just hold whatever it is in an atom, and swap that
;           out with something different later

; TODO: Update URI → namespace mapping to "/name/space.method". E.g.:
; /components/login.css → 'components.login/css
;
(def ^:dynamic *sep* java.io.File/separatorChar)

(defn- ends-match?
  "Predicate that checks if the ends of two seq-able objects match."
  [a b]
  (let [amount (min (count a) (count b))]
    (= (take-last amount a)
       (take-last amount b))))

(defn- drop-extension
  [uri]
  (if-let [ext (re-find #"\.[a-zA-Z0-9]+$" uri)]
    (drop-last (count ext) uri)
    uri))

(defn- uri->namespace
  [uri]
  (symbol (apply str (map #(if (= *sep* %) \. %)
                          (drop-while #(= *sep* %) (drop-extension uri))))))

; TODO: Make this path configurable.
(defn- uri->path
  [uri]
  (apply str
    (interpose *sep* [(-> (new java.io.File ".") (.getCanonicalPath))
                      "resources"
                      "public"
                      (if (= \/ (first uri))
                        (apply str (drop 1 uri))
                        uri)])))

; TODO: Provide useful error messages when ns can't be reolved.
(defn- write-cssgen
  [ans path]
  (require ans)
  (when-let [thens (find-ns ans)]
    (when-let [func (ns-resolve thens '-main)]
      ; (println (format "[INFO] Regenerate cssgen stylesheet: %s -> %s" (str ans) path))
      (spit path
            (func)))))

(defn wrap-cssgen
  "Create a middleware function that accepts an app handler and a predicate.
  The predicate will be compared with the request URI; if true the cssgen
  stylsheet will be regenerated."
  [handler predicate]
  (fn [req]
    (when (predicate req)
      (let [uri-ns (uri->namespace (:uri req))
            pred (partial ends-match? (str uri-ns))]
        (let [found-ns (filter #(pred (str %)) (find-namespaces-on-classpath))]
          (if (< 0 (count found-ns))
            (write-cssgen (first found-ns) (uri->path (:uri req)))))))
    (handler req)))

(defn css-req?
  "A useful predicate that checks if a request starts with '/css' and ends with
  '.css'. For use with `wrap-cssgen`."
  [req]
  (and
    (= (apply str (take 4 (:uri req))) "/css")
    (= (apply str (take-last 4 (:uri req))) ".css")))

