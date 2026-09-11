(ns association-facts-test
  (:require [clojure.java.io :as io] [clojure.java.shell :as shell]
            [clojure.test :refer [deftest is testing]]
            [kotoba.compiler.core :as compiler] [kotoba.kir :as ir]))
(def source (slurp "src/association_facts.kotoba"))
(defn call [kir function & args] (ir/execute kir function (vec args)))
(defn present [option] (when (second option) (nth option 2)))
(def fields ["id" "title" "association" "isic" "country" "kind" "url"
             "url-provenance" "established-date" "last-revised-date" "retrieved-at"])
(def expected
  [{"id" "agc.safety-management-best-practices-2018"
    "title" "2018 Construction Safety Excellence Awards (CSEA): Safety Management Best Practices, jointly with Willis Towers Watson"
    "association" "agc" "isic" "4100" "country" "USA" "kind" "best-practices-guide"
    "url" "https://www.agc.org/sites/default/files/Files/Safety%20&%20Health/WTW-AGC-Willis%20Towers%20Watson%20CSEA%20Best%20Practices_0.pdf"
    "url-provenance" "official-association-site" "established-date" nil
    "last-revised-date" "2018" "retrieved-at" "2026-07-15"}
   {"id" "agc.centennial-history-profile"
    "title" "Our History (Centennial, organization profile)"
    "association" "agc" "isic" "4100" "country" "USA" "kind" "governance-program"
    "url" "https://centennial.agc.org/our-history/"
    "url-provenance" "official-association-site" "established-date" "1918"
    "last-revised-date" nil "retrieved-at" "2026-07-15"}])

(deftest reference-preserves-fields-date-precision-and-topics
  (let [kir (:kir (compiler/compile-source source :js-kotoba-v1))
        observed (mapv (fn [i] (into {} (map (fn [f] [f (present (call kir 'entry-field "agc" i f))]) fields))) [0 1])]
    (is (= expected observed))
    (is (= [nil "1918"] (mapv #(present (call kir 'entry-field "agc" % "established-date")) [0 1])))
    (is (= "2018" (present (call kir 'entry-field "agc" 0 "last-revised-date"))))
    (is (= [2 1] (mapv #(call kir 'topic-count "agc" %) [0 1])))
    (is (= ["worker-safety" "best-practices"] (mapv #(present (call kir 'topic "agc" 0 %)) [0 1])))
    (is (= "agc.centennial-history-profile" (present (call kir 'by-topic-id "agc" "governance" 0))))
    (is (= #{} (set (:effects kir))))
    (testing "unknown values and invalid indexes fail closed"
      (is (zero? (call kir 'entry-count "abc")))
      (is (nil? (present (call kir 'entry-field "agc" -1 "id"))))
      (is (nil? (present (call kir 'entry-field "agc" 2 "id"))))
      (is (nil? (present (call kir 'entry-field "agc" 1 "last-revised-date"))))
      (is (nil? (present (call kir 'topic "agc" 1 1))))
      (is (zero? (call kir 'by-topic-count "agc" "labor")))
      (is (nil? (present (call kir 'by-topic-id "agc" "governance" 1)))))))

(defn compiler-root []
  (nth (iterate #(.getParent ^java.nio.file.Path %)
                (java.nio.file.Path/of (.toURI (io/resource "kotoba/compiler/core.clj")))) 4))
(defn base64 [value] (.encodeToString (java.util.Base64/getEncoder) value))
(deftest restricted-javascript-and-typed-wasm-conform-semantically
  (let [javascript (compiler/compile-source source :js-kotoba-v1)
        wasm (compiler/compile-source source :wasm32-browser-kotoba-v1)
        js64 (base64 (.getBytes ^String (:source javascript) "UTF-8")) wasm64 (base64 ^bytes (:bytes wasm))
        probe (shell/sh "node" "--input-type=module" "-e"
                (str "import(process.argv[1]).then(async host=>{const j=await import('data:text/javascript;base64," js64 "');"
                     "const w=await host.instantiateKotoba(Buffer.from(process.argv[2],'base64'));const run=x=>{"
                     "if(x['entry-count']('agc')!==2n||x['entry-field']('agc',0n,'established-date')[1]!==false||x['entry-field']('agc',0n,'last-revised-date')[2]!=='2018'||x['entry-field']('agc',1n,'established-date')[2]!=='1918')throw Error('dates');"
                     "if(x['topic-count']('agc',0n)!==2n||x['topic']('agc',0n,1n)[2]!=='best-practices'||x['topic-count']('agc',1n)!==1n)throw Error('topics');"
                     "if(x['by-topic-id']('agc','governance',0n)[2]!=='agc.centennial-history-profile'||x['topic']('agc',1n,1n)[1]!==false)throw Error('query');};"
                     "run(j.instantiateKotoba({}));run(w.instance.exports);}).catch(e=>{console.error(e);process.exit(99)})")
                (.toString (.toUri (.resolve (compiler-root) "runtime/browser-host.mjs"))) wasm64)]
    (is (zero? (:exit probe)) (str (:out probe) (:err probe)))))
(deftest production-source-authority
  (is (= ["src/association_facts.kotoba"]
         (->> (file-seq (io/file "src")) (filter #(.isFile %)) (map str) sort vec))))
