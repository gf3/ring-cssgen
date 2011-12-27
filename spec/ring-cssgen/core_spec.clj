(ns ring-cssgen.core-spec
  (:use speclj.core
        ring-cssgen.core))

(describe "css-ns"
  (it "should find all matches"
      (should= 2 (count (css-ns 'ring-cssgen)))))

(describe "load-css-ns")

(describe "add-css-ns")

(describe "generate-css")

(run-specs)

