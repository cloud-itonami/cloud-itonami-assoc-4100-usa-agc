(ns association.facts
  "Industry rule/best-practices catalog for the Associated General
  Contractors of America (AGC, Wikidata Q21015616) -- a 21st
  industry-association-level source (see cloud-itonami-assoc-6419-jpn-zenginkyo,
  -6512-jpn-sonpo, -6612-jpn-jsda, -6419-deu-bankenverband, -6612-usa-finra,
  -6512-usa-naic, -6920-jpn-jicpa, -6920-usa-aicpa, -6419-fra-fbf,
  -6511-jpn-seiho, -6910-jpn-nichibenren, -6810-jpn-recaj, -6411-jpn-boj,
  -6120-usa-ctia, -5110-usa-a4a, -3510-usa-eei, -2910-deu-vda,
  -5510-usa-ahla, -2100-usa-phrma, -4719-usa-nrf for the first twenty)
  per ADR-2607141700 (cloud-itonami-compliance-fact-federation). The
  FIRST entry aligned to ISIC 4100 (construction of buildings) -- a new
  industry code for this family. A rule not in this table has NO
  spec-basis, full stop; extend `catalog`, do not invent an id/url.

  Both AGC documents were verified directly: the 2018 Construction
  Safety Excellence Awards (CSEA) 'Safety Management Best Practices'
  PDF (jointly published with Willis Towers Watson, verified by
  directly reading its rendered cover page via the Read tool, hence a
  `:kind :best-practices-guide` rather than a binding member commitment)
  and the centennial 'Our History' profile page (directly WebFetch-verified,
  confirming AGC's 1918 founding in its own text).")

(def catalog
  "assoc-slug -> vector of self-regulatory rule entries."
  {"agc"
   [{:association-rule/id "agc.safety-management-best-practices-2018"
     :association-rule/title "2018 Construction Safety Excellence Awards (CSEA): Safety Management Best Practices, jointly with Willis Towers Watson"
     :association-rule/association "agc"
     :association-rule/isic "4100"
     :association-rule/country "USA"
     :association-rule/kind :best-practices-guide
     :association-rule/url "https://www.agc.org/sites/default/files/Files/Safety%20&%20Health/WTW-AGC-Willis%20Towers%20Watson%20CSEA%20Best%20Practices_0.pdf"
     :association-rule/url-provenance :official-association-site
     :association-rule/last-revised-date "2018"
     :association-rule/retrieved-at "2026-07-15"
     :association-rule/topic #{:worker-safety :best-practices}}
    {:association-rule/id "agc.centennial-history-profile"
     :association-rule/title "Our History (Centennial, organization profile)"
     :association-rule/association "agc"
     :association-rule/isic "4100"
     :association-rule/country "USA"
     :association-rule/kind :governance-program
     :association-rule/url "https://centennial.agc.org/our-history/"
     :association-rule/url-provenance :official-association-site
     :association-rule/established-date "1918"
     :association-rule/retrieved-at "2026-07-15"
     :association-rule/topic #{:governance}}]})

(defn spec-basis [assoc-slug] (get catalog assoc-slug))

(defn coverage
  ([] (coverage (keys catalog)))
  ([slugs]
   (let [have (filter catalog slugs)
         missing (remove catalog slugs)]
     {:requested (count slugs)
      :covered (count have)
      :covered-associations (vec (sort have))
      :missing-associations (vec (sort missing))
      :note (str "cloud-itonami-assoc-4100-usa-agc Wave 0 (ADR-2607141700): "
                 (count (get catalog "agc")) " agc entries seeded with an "
                 "official agc.org citation. Extend "
                 "`association.facts/catalog`, never fabricate a rule id/url.")})))

(defn by-topic [assoc-slug topic]
  (filterv #(contains? (:association-rule/topic %) topic) (spec-basis assoc-slug)))
