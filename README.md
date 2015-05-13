
# Dashes to Dashes

Simple application that offers an API to get all user contributed docsets for dash. It's used by helm-dash atm.


- https://dashes-to-dashes.herokuapp.com/docsets/contrib
- https://github.com/areina/helm-dash


# Running Locally

Make sure you have Clojure installed.  Also, install the [Heroku Toolbelt](https://toolbelt.heroku.com/).

```sh
$ git clone https://github.com/heroku/clojure-getting-started.git
$ cd clojure-getting-started
$ lein repl
user=> (require 'clojure-getting-started.web)
user=>(def server (clojure-getting-started.web/-main))
```

Your app should now be running on [localhost:5000](http://localhost:5000/).

# Deploying to Heroku

```sh
$ heroku create
$ git push heroku master
$ heroku open
```

# Documentation

This application support the [Getting Started with Clojure](https://devcenter.heroku.com/articles/getting-started-with-clojure) article - check it out.
For more information about using Clojure on Heroku, see these Dev Center articles:

- [Clojure on Heroku](https://devcenter.heroku.com/categories/clojure)
