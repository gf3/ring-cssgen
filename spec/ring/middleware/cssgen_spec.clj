(ns ring.middleware.cssgen-spec
  (:use clojure.test
        clojure.contrib.mock.test-adapter
        speclj.core)
  (:require [ring.middleware.cssgen :as cssgen]))

(defn handler [r]
  nil)
(declare *css-ns*)
(declare *req*) 
(declare *bad-req*) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Public
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(describe "wrap-cssgen"
  (it "should accept two arguments"
    (should= 2 (count (first (:arglists (meta #'cssgen/wrap-cssgen))))))

  (it "should accept an app handler and a predicate and return the middleware when first invoked"
    (should (function? (cssgen/wrap-cssgen handler cssgen/css-req?)))))

(describe "wrap-cssgen middleware"
  (before-all
    (create-ns 'spec.css.style)
    (intern (find-ns 'spec.css.style) '-main (fn [] true)))

  (after-all
    (remove-ns 'spec.css.style))

  (around [it]
    (binding [*req* {:uri "/css/style.css"}
              *css-ns* 'spec.css.style]
      (it)))

  (it "should match a request with the given predicate")
  (it "should not generate the CSS file of the predicate fails")
  (it "should generate the CSS file if the predicated succeeds"
    ; (should
      ; (expect [ring-cssgen.core/write-cssgen (has-args [*css-ns* (:uri *req*)] (times once (returns nil)))]
        ; ((#'cssgen/wrap-cssgen handler cssgen/css-req?) *req*))))
    )
  )

(describe "css-req?"
  (around [it]
    (binding [*req* {:uri "/css/style.css"}
              *bad-req* {:uri "/code/something.exe"}]
      (it)))

  (it "should match a URI beginning with '/css' and ending with '.css'"
    (should (cssgen/css-req? *req*)))

  (it "should not match a URI that doesn't begin with '/css' or end with '.css'"
    (should-not (cssgen/css-req? *bad-req*))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Private
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(describe "ends-match?"
  (it "should return true when the ends of two sequences match"
    (should (#'cssgen/ends-match? [3 4] [1 2 3 4])))

  (it "should return false when the end of two sequences don't match"
    (should-not (#'cssgen/ends-match? [3 4] [1 2])))
  
  (it "should work on strings"
    (should (#'cssgen/ends-match? "lolwat" "wat"))))

(describe "drop-extension"
  (it "drop the extension from a URI"
    (should= "lol" (#'cssgen/drop-extension "lol"))))

(describe "uri->namespace"
  (it "should accurately transform a URI to a namespace symbol"
    (should= 'css.style (#'cssgen/uri->namespace "/css/style.css"))))

(describe "uri->path"
  (it "should accurately transform a URI to a canonical path"
    (should= (str (-> (new java.io.File ".") (.getCanonicalPath)) "/resources/public/css/style.css")
             (#'cssgen/uri->path "/css/style.css"))))

(describe "write-cssgen"
  (before-all
    (create-ns 'spec.css.style)
    (intern (find-ns 'spec.css.style) '-main (fn [] true)))

  (after-all
    (remove-ns 'spec.css.style))

  (around [it]
    (binding [*css-ns* (find-ns 'spec.css.style)]
      (it)))

  (it "should write the CSS to a specific path if the namespace and function resolve"
    (should
      (expect [require (has-args ['spec.css.style] (times once (returns nil)))
               spit (has-args ["wat" true] (times once (returns nil)))]
        (#'cssgen/write-cssgen 'spec.css.style "wat")))))

(run-specs)

