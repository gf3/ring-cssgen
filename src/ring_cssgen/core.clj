(ns ring-cssgen.core
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

; TODO: Use java.io.File.separatorChar
(defn- namespace-to-file [ans]
  (str
    "/"
    (join "/"
          (take-last 2 (-> ans str (.split "\\."))))
    ".css"))

; TODO: Refactor to allow composition w/ predicate.
(defn- find-namespaces-with-prefix
  "Find all namespaces on the classpath which match a given prefix."
  [ns-prefix]
  (for [ans (find-namespaces-on-classpath)
        :let [name (str ans)
              ns-prefix (str ns-prefix)]
        :when (.startsWith name ns-prefix)]
    ans))

; TODO: Provide useful error messages when ns can't be reolved.
(defn generate-css
  "Generate a css file for a given namespace."
  [ans]
  (require ans)
  (when-let [thens (find-ns ans)]
    (when-let [func (ns-resolve thens '-main)]
      (spit (str (-> (new java.io.File ".") (.getCanonicalPath)) "/resources/public/" (namespace-to-file ans))
            (func)))))

(defn- handler
  "Ring middleware handler to compare request URI with registered namespaces for cssgen stylesheets."
  [namespaces app]
  (fn [req]
    (doall
      (for [ans namespaces
            :let [ans-filename (namespace-to-file ans)
                  uri (:uri req)]
            :when (= uri ans-filename)]
        (do
          (prn (format "Regenerate CSS: '%s -> %s" ans uri))
          (generate-css ans))))
    (app req)))

; TODO: Rename to reflect general nature of function.
; TODO: Refactor to allow composition w/ predicate.
(defn css-ns
  "Provide a list of namespaces as symbols matching a given prefix."
  [ns-prefix]
  (find-namespaces-with-prefix ns-prefix))

(defn load-css-ns
  "Register all gencss stylesheets with a ns prefix. These stylsheets will be regenerated per request"
  [ns-prefix]
  (let [a (atom (find-namespaces-with-prefix ns-prefix))] [a (partial handler @a)]))

(defn add-css-ns
  "Add a namespace to the list of cssgen namespaces to watch for."
  [namespaces ans]
  (swap! namespaces conj ans))

