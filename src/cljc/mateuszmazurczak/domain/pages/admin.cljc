(ns mateuszmazurczak.domain.pages.admin
  "Admin page domain - schemas and pure functions."
  (:require
   [malli.core                          :as m]
   [malli.error                         :as me]
   [mateuszmazurczak.domain.i18n.schema :as i18n-schema]))

;; =============================================================================
;; State Paths
;; =============================================================================

(def ^:dynamic *admin-page-path* [:pages :admin])
(def ^:dynamic *admin-login-form-path* [:pages :admin :form])
(def ^:dynamic *admin-submitting-path* [:pages :admin :submitting?])

;; =============================================================================
;; Schemas
;; =============================================================================

(def AdminPageData
  "Schema for admin page data in app-db.
   
   Admin page shows login form when not logged in, and admin dashboard when logged in.
   Login state managed in [:admin :logged-in?] (shared across app)."
  [:map {:closed true}
   [:loading? :boolean]
   [:form
    [:map {:closed true}
     [:admin-key :string]]]
   [:submitting? :boolean]
   [:text [:map-of :keyword i18n-schema/I18nMarker]]
   [:handlers {:optional true}
    [:map [:on-login fn?] [:on-logout fn?] [:on-update-form fn?] [:on-navigate-aoc fn?]]]])

(def AdminPageUIData
  "Schema for admin page UI data (denormalized for component consumption).
   
   This is the shape returned by the :pages/admin subscription after
   translating text and including logged-in state."
  [:map {:closed true}
   [:loading? :boolean]
   [:logged-in? [:maybe :boolean]]
   [:form
    [:map {:closed true}
     [:admin-key :string]]]
   [:submitting? :boolean]
   [:text [:map-of :keyword :string]]
   [:handlers {:optional true}
    [:map [:on-login fn?] [:on-logout fn?] [:on-update-form fn?] [:on-navigate-aoc fn?]]]])

(defn valid-admin-page-data?
  "Validate admin page data against schema."
  [data]
  (m/validate AdminPageData data))

(defn explain-admin-page-data
  "Explain validation errors for admin page data.
   
   Returns human-readable explanation of validation errors,
   or nil if data is valid."
  [data]
  (when-let [explanation (m/explain AdminPageData data)]
    {:explained (me/humanize explanation)
     :raw-explanation explanation}))

(defn valid-admin-page-ui-data?
  "Validate admin page UI data (denormalized) against schema."
  [data]
  (m/validate AdminPageUIData data))

(defn explain-admin-page-ui-data
  "Explain validation errors for admin page UI data.
   
   Returns human-readable explanation of validation errors,
   or nil if data is valid."
  [data]
  (when-let [explanation (m/explain AdminPageUIData data)]
    {:explained (me/humanize explanation)
     :raw-explanation explanation}))

(defn initial-admin-data
  "Returns initial admin page data structure for app-db initialization.
   
   Starts with empty login form. Loading state is true initially,
   set to false after route initialization."
  []
  {:loading? true
   :form {:admin-key ""}
   :submitting? false
   :text {:admin-panel [:i18n :admin-panel]
          :admin-dashboard [:i18n :admin-dashboard]
          :logged-in-as-admin [:i18n :logged-in-as-admin]
          :admin-login [:i18n :admin-login]
          :enter-admin-key [:i18n :enter-admin-key]
          :admin-key-placeholder [:i18n :admin-key-placeholder]
          :login [:i18n :login]
          :admin-logout [:i18n :admin-logout]
          :manage-aoc-solutions [:i18n :manage-aoc-solutions]
          :go-to-aoc-page [:i18n :go-to-aoc-page]
          :admin-description [:i18n :admin-description]}
   :handlers {:on-login [:dispatch [:admin/login]]
              :on-logout [:dispatch [:admin/logout]]
              :on-update-form [:dispatch [:admin/update-form]]
              :on-navigate-aoc
              [:dispatch
               [:nav/navigate :mateuszmazurczak.adapters.navigation.routes/aoc nil nil]]}})

;; =============================================================================
;; Admin Key Validation
;; =============================================================================

(def min-admin-key-length 32)

(defn valid-admin-key?
  "Check if admin key is valid.
   
   Admin key must be:
   - Non-nil
   - Non-empty string
   - At least 32 characters long
   
   Returns: boolean"
  [admin-key]
  (and (string? admin-key)
       (not (empty? admin-key))
       (>= (count admin-key) min-admin-key-length)))

(defn validate-admin-login
  "Validate admin login attempt.
   
   Returns map with:
   - :valid? - boolean indicating if login can proceed
   - :reason - keyword explaining why validation failed (if invalid)
   
   Possible reasons:
   - :invalid-key - Key is nil, empty, or too short"
  [admin-key]
  (if (valid-admin-key? admin-key)
    {:valid? true}
    {:valid? false
     :reason :invalid-key}))
