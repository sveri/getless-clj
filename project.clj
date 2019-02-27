(defproject getless "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/spec.alpha "0.2.176"]

                 [org.clojure/core.cache "0.7.2"]
                 [org.clojure/core.async "0.4.490"]

                 [ring "1.7.1"]
                 [lib-noir "0.9.9"]
                 [ring/ring-anti-forgery "1.3.0"]
                 [compojure "1.6.1"]
                 [reagent "0.8.1"]
                 [http-kit "2.3.0"]
                 [selmer "1.12.8"]
                 [prone "1.6.1"]
                 [im.chit/hara.io.scheduler "2.5.10"]
                 [noir-exception "0.2.5"]

                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]

                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]

                 [fipp "0.6.17"]

                 [com.draines/postal "2.0.3"]

                 [jarohen/nomad "0.7.3"]

                 [de.sveri/clojure-commons "0.2.2"]

                 [clojure-miniprofiler "0.5.0"]

                 [reloaded.repl "0.2.4"]
                 [org.danielsz/system "0.4.2"]

                 [cljs-ajax "0.8.0"]
                 [ring-transit "0.1.6"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]

                 [net.tanesha.recaptcha4j/recaptcha4j "0.0.8"]

                 [com.taoensso/tempura "1.2.1"]

                 [org.clojure/core.typed "0.6.0"]
                 [prismatic/plumbing "0.5.5"]
                 [prismatic/schema "1.1.10"]

                 [com.rpl/specter "1.1.2"]

                 [de.sveri/closp-crud "0.3.0"]
                 [org.clojure/test.check "0.9.0"]
                 [clj-time "0.15.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [kerodon "0.9.0"]]

  :plugins [[lein-cljsbuild "1.1.1"]]

  :min-lein-version "2.5.0"

  :jvm-opts ["-Duser.timezone=UTC"]

  ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
  ;:hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild
  {:builds {:dev {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                  :figwheel {:css-dirs ["resources/public/css"]             ;; watch and update CSS
                             :on-jsload "getless.dev/main"}
                  :compiler     {:main           "getless.dev"
                                 :asset-path     "/js/compiled/out"
                                 :output-to      "resources/public/js/compiled/app.js"
                                 :output-dir     "resources/public/js/compiled/out"}}
            :adv {:source-paths ["src/cljs" "src/cljc"]
                  :compiler     {:output-to     "resources/public/js/compiled/app.js"
                                 ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
                                 ;:jar           true
                                 :optimizations :advanced
                                 :pretty-print  false}}}}

  :profiles {:dev     {:repl-options {:init-ns          de.sveri.getless.user}

                       :plugins      [[lein-ring "0.9.0"]
                                      [lein-figwheel "0.5.0-2"]
                                      [test2junit "1.1.1"]]

                       :dependencies [
                                      ;[org.bouncycastle/bcprov-jdk15on "1.52"]
                                      ;[ring/ring-mock "0.3.0"]
                                      [org.apache.httpcomponents/httpclient "4.5.7"]
                                      ;[org.seleniumhq.selenium/htmlunit-driver "2.53.1"]
                                      [org.seleniumhq.selenium/selenium-htmlunit-driver "2.52.0"]
                                      [org.seleniumhq.selenium/selenium-firefox-driver "3.141.59"]
                                      [org.seleniumhq.selenium/selenium-java "3.141.59"]
                                      ;[org.seleniumhq.selenium/selenium-server "2.52.0"]
                                      [clj-webdriver "0.7.2"]

                                      ;[ring-mock "0.3.0"]
                                      [ring/ring-devel "1.7.1"]
                                      [pjstadig/humane-test-output "0.9.0"]]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]}
                       ;:ring {:stacktrace-middleware prone.middleware/wrap-exceptions}}

             :uberjar {:auto-clean false                    ; not sure about this one
                       :omit-source true
                       :aot         :all}}

  :test-paths ["test/clj" "integtest/clj"]

  :test-selectors {:unit        (fn [m] (not (or (:selenium m) (:integration m) (:rest m))))
                   :off-integration :off-integration
                   :integration :integration
                   :selenium    :selenium
                   :rest        :rest
                   :cur         :cur                                ; one more selector for, give it freely to run only
                   ; the ones you need currently
                   :all         (constantly true)}

  :test2junit-output-dir "test-results"

  :main de.sveri.getless.core

  :uberjar-name "getless.jar"

  :aliases {"rel-jar" ["do" "clean," "cljsbuild" "once" "adv," "uberjar"]
            "unit" ["do" "test" ":unit"]
            "integ" ["do" "test" ":integration"]})

            ; migration utilities
            ;"migrate" ["run" "-m" "joplin.alias/migrate" "joplin.edn" "sqlite-dev-env" "sqlite-dev"]
            ;"rollback" ["run" "-m" "joplin.alias/rollback" "joplin.edn" "sqlite-dev-env" "sqlite-dev"]
            ;"reset" ["run" "-m" "joplin.alias/reset" "joplin.edn" "sqlite-dev-env" "sqlite-dev"]})
