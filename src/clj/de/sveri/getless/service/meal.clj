(ns de.sveri.getless.service.meal)

(def breakfast "BREAKFAST")
(def )

(defn save-new-meal [session]
  (assoc-in session [:meal :type] breakfast))
