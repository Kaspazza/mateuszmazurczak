(ns mateuszmazurczak.endpoint.error-page
  (:require
   [mateuszmazurczak.endpoint.handler :as mm-endpoint-handler]
   [mateuszmazurczak.ui.errors        :as mm-ui-errors]
   [mateuszmazurczak.utils.fallback   :as fallback]))

(defn not-found-page
  "Build default not found page"
  [{:keys [tr logger]
    :as request}]
  (let [title (fallback/always-return #(tr :not-found-page)
                                      "This was unexpected"
                                      logger)
        description (fallback/always-return #(tr :not-found-description)
                                            "But we are working on it!"
                                            logger)
        back-home (fallback/always-return #(tr :back-home) "Back" logger)]
    (mm-endpoint-handler/build request
                               (mm-ui-errors/not-found {:title title
                                                        :description description
                                                        :back-home-text
                                                        back-home}))))

(defn internal-error-page
  "Build default internal error page"
  [{:keys [tr logger]
    :as request}]
  (let [title (fallback/always-return #(tr :this-is-unexpected)
                                      "This was unexpected"
                                      logger)
        description (fallback/always-return #(tr :we-are-working-on-it)
                                            "But we are working on it!"
                                            logger)
        back-home (fallback/always-return #(tr :back-home) "Back" logger)]
    (mm-endpoint-handler/build request
                               (mm-ui-errors/internal-error
                                {:title title
                                 :description description
                                 :back-home-text back-home}))))
