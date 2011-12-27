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
something like `yourapp.css` works well. Here's the important part, be sure to
return your css from the `-main` function.

``` clojure
(ns yourapp.css.design
  (:use cssgen))

(defn -main []
  (css
    [:body
      :color "blue"]))
```

Now you can use `load-css-ns` to register your stylesheet namespaces and return
your middleware function. `load-css-ns` takes a namespace prefix which it will
use to find all your stylsheet namespaces, it returns a Vector Atom of the
registered namespaces and the middleware handler function. Wherever you're
starting your server, you'll need to pass in the middleware function.

``` clojure
(ns yourapp.server
  (:use [ring-cssgen.core :only [load-css-ns]]))

(let [[css-ns wrap-cssgen] (load-css-ns 'yourapp.css)]
  ; start your server with wrap-cssgen
  )
```
Boom! Did you are unimpressed?


License
-------

ring-cssgen is distributed under the Eclipse Public License, the same as Clojure.

[ring]:https://github.com/mmcgrana/ring
[cssgen]:https://github.com/paraseba/cssgen/tree/0.3.0

