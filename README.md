ring-cssgen
===========

[Ring][ring] middleware to automatically compile and regenerate your
[cssgen][cssgen] stylesheets. Works well with Compojure and Noir. Very much
sttill an alpha.


Installation
------------

**Soon** you'll be able to add the following to your `project.clj`:

``` clojure
:dependencies [[ring-cssgen "0.0.1"]]
```


Usage
-----

First off, create your cssgen stylesheets and throw them in a common namespace,
something like `yourapp.css.whatever` works well. Here's the important part, be sure to
return your css from the `-main` function.

``` clojure
(ns yourapp.css.design
  (:use cssgen))

(defn -main []
  (css
    [:body
      :color "blue"]))
```

Now you can use the `wrap-cssgen` middleware with your ring application.
`wrap-cssgen` takes one additional argument, a predicate which receives the
request and determines whether it is a legal CSS resource. As a conveniece
a default predicate is included which may be used, `css-req?`. It simply checks
if the URI begins with `/css` and ends with `.css`.

``` clojure
(ns yourapp.server
  (:require [ring.middleware.cssgen :as [cssgen]]))

(def app
  (-> your-handler
      (cssgen/wrap-cssgen cssgen/css-req?)))
```
Now when `/css/design.css` is requested from your server, it will be
automatically generated and written to disk.

Boom! Did you are unimpressed?


Caveats
-------

Requesting a CSS resource runs code, potentially unsafe. Be sure you provide
a restrictive predicate.


Tests/Specs
-----------

Run the specs with leiningen: `lein spec`


License
-------

ring-cssgen is distributed under the Eclipse Public License, the same as Clojure.

[ring]:https://github.com/mmcgrana/ring
[cssgen]:https://github.com/paraseba/cssgen/tree/0.3.0

