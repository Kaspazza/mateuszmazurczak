(ns mateuszmazurczak.endpoint.error-page
  (:require
   [mateuszmazurczak.endpoint.handler :as mm-endpoint-handler]
   [mateuszmazurczak.ui.errors        :as mm-ui-errors]
   [mateuszmazurczak.utils.fallback   :as fallback]))

(defn not-found-page
  "Build default not found page"
  [{:keys [tr]
    :or {tr str}
    :as request}]
  (let [title (fallback/always-return #(tr :not-found-page)
                                      "This was unexpected")
        description (fallback/always-return #(tr :not-found-description)
                                            "But we are working on it!")
        back-home (fallback/always-return #(tr :back-home) "Back")]
    (mm-endpoint-handler/build request
                               (mm-ui-errors/not-found {:title title
                                                        :description description
                                                        :back-home-text
                                                        back-home}))))

(defn internal-error-page
  "Build default internal error page"
  [{:keys [tr]
    :or {tr str}
    :as request}]
  (let [title (fallback/always-return #(tr request :this-is-unexpected)
                                      "This was unexpected")
        description (fallback/always-return #(tr request :we-are-working-on-it)
                                            "But we are working on it!")
        back-home (fallback/always-return #(tr request :back-home) "Back")]
    (mm-endpoint-handler/build request
                               (mm-ui-errors/internal-error
                                {:title title
                                 :description description
                                 :back-home-text back-home}))))
