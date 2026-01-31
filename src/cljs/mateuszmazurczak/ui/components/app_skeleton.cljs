(ns mateuszmazurczak.ui.components.app-skeleton
  "App initialization loading screen component."
  (:require
   [mateuszmazurczak.ui.components.skeleton :as skeleton]))

(defn navigation-skeleton
  []
  [:div {:class "space-y-4"}
   [:div {:class "space-y-2"}
    [skeleton/skeleton {:class "h-4 w-20"}] ; Group label
    [:div {:class "space-y-1"}
     [skeleton/skeleton {:class "h-9 w-full"}]
     [skeleton/skeleton {:class "h-9 w-full"}]
     [skeleton/skeleton {:class "h-9 w-full"}]
     [skeleton/skeleton {:class "h-9 w-full"}]]]])

(defn spacer
  []
  [:div {:class "flex-1"}])

(defn sidebar-user-bottom-profile
  []
  [:div {:class "mt-auto"}
   [:div {:class "flex items-center gap-2"}
    [skeleton/skeleton {:class "h-8 w-8 rounded-lg"}]
    [:div {:class "flex-1 space-y-1"}
     [skeleton/skeleton {:class "h-4 w-24"}]
     [skeleton/skeleton {:class "h-3 w-32"}]]]])

(defn sidebar-header
  []
  [:div {:class "mb-6"}
   [skeleton/skeleton {:class "h-10 w-full"}]])

(defn sidebar-skeleton
  []
  [:div {:class "flex w-64 flex-col border-r border-border bg-sidebar p-4"}
   [sidebar-header]
   [navigation-skeleton]
   [spacer]
   [sidebar-user-bottom-profile]])

(defn header
  []
  [:header {:class "flex h-16 items-center gap-2 border-b border-border px-4"}
   [skeleton/skeleton {:class "h-6 w-6"}]    ; Sidebar trigger
   [:div {:class "mx-2 h-4 w-px bg-border"}] ; Separator
   [:div {:class "flex items-center gap-2"}
    [skeleton/skeleton {:class "h-4 w-24"}] ; Breadcrumb
    [:span {:class "text-muted-foreground"}
     "/"]
    [skeleton/skeleton {:class "h-4 w-32"}]]])

(defn page-content
  []
  [:div {:class "flex-1 p-4"}
   [:div {:class "space-y-4"}
    [skeleton/skeleton {:class "h-8 w-64"}] ; Page title
    [skeleton/skeleton {:class "h-4 w-96"}] ; Description
    [:div {:class "mt-8 space-y-3"}
     [skeleton/skeleton {:class "h-[125px] w-full rounded-xl"}]
     [skeleton/skeleton {:class "h-[125px] w-full rounded-xl"}]
     [skeleton/skeleton {:class "h-[125px] w-full rounded-xl"}]]]])

(defn main-content-skeleton
  []
  [:div {:class "flex flex-1 flex-col"}
   [header]
   [page-content]])

(defn app-loading
  "Display a loading screen during app initialization.
   
   Shows a skeleton matching the main app layout (sidebar + header + content)
   while the initial configuration is being loaded. This prevents race conditions
   and premature page rendering."
  []
  [:div {:class "flex h-screen w-screen bg-background"}
   [sidebar-skeleton]
   [main-content-skeleton]])
