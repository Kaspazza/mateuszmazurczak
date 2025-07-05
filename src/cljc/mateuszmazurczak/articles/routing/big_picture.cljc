(ns mateuszmazurczak.articles.routing.big-picture
  (:require
   [mateuszmazurczak.navigation.routes :as-alias mm-routes]))

(def article-content
  [:div {:class "page-body mt-6"}
   [:p {:id "1bb0109e-1131-807b-9d2d-da9e20c30472"
        :class ""}]
   [:p {:id "1aa0109e-1131-80ca-9401-f40476ea69ad"
        :class ""}
    "Routing is complicated. Despite many years of experience in web development, setting up complete routing and troubleshooting is still challenging."
    [:br]
    [:br]
    "This article aims to provide a general, shallow understanding of how client requests reach your application and what occurs during that process."
    [:br]]
   [:p {:id "1bb0109e-1131-80dd-a465-f23de1fc07b6"
        :class ""}]
   [:p {:id "1bb0109e-1131-80f5-a14c-e5e9675d48da"
        :class ""}
    [:br]
    "Here is a diagram representing a big picture of what happens between our client opening our page in a browser and him getting the response (website content)."
    [:br]
    [:br]
    "In this article, we will go briefly over all those elements so you can understand step by step what’s happening, between when someone enters the website and receives the content."
    [:br]
    [:br]
    "This big-picture overview is essential for understanding where problems with routing can happen."
    [:br]]
   [:p {:id "1bb0109e-1131-8083-b14e-ddf13e67a3e8"
        :class ""}]
   [:figure {:id "1bb0109e-1131-8034-8a29-fbd1996e910e"
             :class "image"}
    [:img {:style {"width" "709.982666015625px"}
           :src "routing/image.png"}]]
   [:p {:id "1b80109e-1131-8032-8376-e71806b889b2"
        :class ""}
    [:br]
    "The first step begins when someone opens our website in their browser"
    [:br]]
   [:p {:id "1c60109e-1131-800b-869f-c1751e26200a"
        :class ""}]
   [:h2 {:id "1aa0109e-1131-8024-898a-d4ab02491db6"
         :class ""}
    [:a {:href "#1aa0109e-1131-8024-898a-d4ab02491db6"
         :class "no-underline"}
     [:span
      {:class
       "heading-anchorlink-icon bg-base-content/5 hover:bg-primary/10 size-[1em] text-base-content/30 hover:text-primary/50 rounded-field border border-base-content/5 hover:border-primary/20 inline-grid place-content-center hover:shadow-sm hover:shadow-base-200 align-text-bottom me-3 lg:absolute lg:ms-[-1.5em] lg:mt-1 transition-all group"}
      [:svg {:class "group-hover:scale-100 scale-90 transition-transform"
             :fill "currentColor"
             :width ".5em"
             :height ".5em"
             :viewBox "0 0 256 256"
             :id "Flat"
             :xmlns "http://www.w3.org/2000/svg"}
       [:path
        {:d
         "M216,148H172V108h44a12,12,0,0,0,0-24H172V40a12,12,0,0,0-24,0V84H108V40a12,12,0,0,0-24,0V84H40a12,12,0,0,0,0,24H84v40H40a12,12,0,0,0,0,24H84v44a12,12,0,0,0,24,0V172h40v44a12,12,0,0,0,24,0V172h44a12,12,0,0,0,0-24Zm-108,0V108h40v40Z"}]]]]
    "1. User opens our website"]
   [:p {:id "1bb0109e-1131-80b9-885f-c70dba20821d"
        :class ""}
    "When the user opens the page, what happens under the hood is that the browser is sending a message to GitHub and expects the message back."
    [:br]
    "The message is sent over"
    [:a {:href "https://developer.mozilla.org/en-US/docs/Web/HTTP"}
     "HTTP protocol"]
    "and sending it from the client to a server is called Request."
    [:br]
    [:br]
    "One example of sending a request is opening a website, e.g. entering “"
    [:a {:href "http://github.com/trending"}
     "github.com/trending"]
    "”." [:br]
    "Which looks something like this:" [:br]]
   [:p {:id "1bb0109e-1131-8080-b1d2-f60632fa5d80"
        :class ""}]
   [:script
    {:src "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js"
     :integrity
     "sha512-7Z9J3l1+EYfeaPKcGXu3MS/7T+w19WtKQY/n+xzmw4hZhJ9tyYmcUS+4QqAlzhicE5LAfMQSF3iFTK9bQdTxXg=="
     :crossOrigin "anonymous"
     :referrerPolicy "no-referrer"}]
   [:link
    {:rel "stylesheet"
     :href
     "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.min.css"
     :integrity
     "sha512-tN7Ec6zAFaVSG3TpNAKtk4DOHNpSwKHxxrsiw4GHKESGPs5njn/0sMCUMl2svV4wo4BK/rCP7juYz+zx+l6oeQ=="
     :crossOrigin "anonymous"
     :referrerPolicy "no-referrer"}]
   [:pre {:id "1bb0109e-1131-80b4-b415-fd716d0d78ca"
          :class "code"}
    [:code {:class "language-Visual Basic"}
     "GET /trending HTTP/1.1\nHost: www.github.com"]]
   [:p {:id "1b10109e-1131-802b-93c1-de51e39c88f9"
        :class ""}
    [:br]
    [:br]
    "This kind of request is called GET request (as it expects to retrieve data)."
    [:br]
    [:br]]
   [:h2 {:id "DNS"}
    [:a {:href "#DNS"
         :class "no-underline"}
     [:span
      {:class
       "heading-anchorlink-icon bg-base-content/5 hover:bg-primary/10 size-[1em] text-base-content/30 hover:text-primary/50 rounded-field border border-base-content/5 hover:border-primary/20 inline-grid place-content-center hover:shadow-sm hover:shadow-base-200 align-text-bottom me-3 lg:absolute lg:ms-[-1.5em] lg:mt-1 transition-all group"}
      [:svg {:class "group-hover:scale-100 scale-90 transition-transform"
             :fill "currentColor"
             :width ".5em"
             :height ".5em"
             :viewBox "0 0 256 256"
             :id "Flat"
             :xmlns "http://www.w3.org/2000/svg"}
       [:path
        {:d
         "M216,148H172V108h44a12,12,0,0,0,0-24H172V40a12,12,0,0,0-24,0V84H108V40a12,12,0,0,0-24,0V84H40a12,12,0,0,0,0,24H84v40H40a12,12,0,0,0,0,24H84v44a12,12,0,0,0,24,0V172h40v44a12,12,0,0,0,24,0V172h44a12,12,0,0,0,0-24Zm-108,0V108h40v40Z"}]]]]
    "2. DNS"]
   [:p {:id "1b80109e-1131-80fc-b676-dde65037b2c3"
        :class ""}
    "But for your browser to send the request to host"
    [:a {:href "http://github.com"}
     "github.com"]
    "it’s not enough information. It needs to know exactly which computer to send it to."
    [:br]
    [:a
     {:href
      "https://developer.mozilla.org/en-US/docs/Learn/Common_questions/Web_mechanics/How_does_the_Internet_work"}
     "Because internet/web is just a ton of computers connected together"]
    "and each device has unique IP address when it connects to the web, which is a numerical label like 8.8.8.8 (IPv4 and IPv6 are just different ways to write them)."
    [:br]
    [:br]
    "Theorethically we could skip domain names and only use the addresses, but because address like  74.125.24.102 is not easily memorable and words like github.com are, there is Domain Name System (DNS) that resolves name to IP address."
    [:br]
    [:br]
    [:br]
    "So to know which IP address is behind github.com"
    [:br]
    "First your browser checks if you don’t have this IP address already saved locally."
    [:br]
    [:br]
    "If you don’t have it, it will resolve it via your internet service provider or public service (e.g. Google DNS, Cloudflare). Where they have acess to the registry of providers (e.g. OVH) that will tell them if this is something they recognize."
    [:br]]
   [:p {:id "1c60109e-1131-8039-a67e-d277d8c193f6"
        :class ""}]
   [:p {:id "1bb0109e-1131-801c-ad84-e94523f3b441"
        :class ""}
    [:br]
    "And if they do recognize it they will respond to them with IP address connected to that name."
    [:br]
    [:br]
    [:br]
    "So if you want to be found on the web via a website name you need to buy a domain and configure which IP should it be forwarded to."
    [:br]
    [:br]]
   [:p {:id "1bb0109e-1131-801b-aff8-c0859de7eec0"
        :class ""}
    "So when you buy a domain from service providers like OVH, they have registered their nameservers in those services, so when the DNS resolver asks, OVH it will answer - oh yeah I know that domain name! This is the IP for it."
    [:br]
    [:br]
    "And that’s the first part of the diagram"
    [:br]
    [:br]]
   [:figure {:id "1bb0109e-1131-8069-ac1b-c7759109ceb7"
             :class "image"}
    [:img {:style {"width" "709.982666015625px"}
           :src "routing/image%201.png"}]]
   [:p {:id "1b80109e-1131-803f-a7af-fef5328890ff"
        :class ""}
    [:br]
    [:br]
    [:br]
    [:br]
    [:br]]
   [:h2 {:id "1b80109e-1131-80b5-894c-c25b16ec85ab"
         :class ""}
    [:a {:href "#1b80109e-1131-80b5-894c-c25b16ec85ab"
         :class "no-underline"}
     [:span
      {:class
       "heading-anchorlink-icon bg-base-content/5 hover:bg-primary/10 size-[1em] text-base-content/30 hover:text-primary/50 rounded-field border border-base-content/5 hover:border-primary/20 inline-grid place-content-center hover:shadow-sm hover:shadow-base-200 align-text-bottom me-3 lg:absolute lg:ms-[-1.5em] lg:mt-1 transition-all group"}
      [:svg {:class "group-hover:scale-100 scale-90 transition-transform"
             :fill "currentColor"
             :width ".5em"
             :height ".5em"
             :viewBox "0 0 256 256"
             :id "Flat"
             :xmlns "http://www.w3.org/2000/svg"}
       [:path
        {:d
         "M216,148H172V108h44a12,12,0,0,0,0-24H172V40a12,12,0,0,0-24,0V84H108V40a12,12,0,0,0-24,0V84H40a12,12,0,0,0,0,24H84v40H40a12,12,0,0,0,0,24H84v44a12,12,0,0,0,24,0V172h40v44a12,12,0,0,0,24,0V172h44a12,12,0,0,0,0-24Zm-108,0V108h40v40Z"}]]]]
    "3. Server"]
   [:p {:id "1bb0109e-1131-80cf-af10-d26e068481ea"
        :class ""}
    "So now that we know the IP of the computer under that domain, the request is sent there."
    [:br]
    [:br]]
   [:figure {:id "1bb0109e-1131-8047-aa59-c76e1af14dae"
             :class "image"}
    [:img {:style {"width" "709.984375px"}
           :src "routing/e8cc37ca-878d-410c-a8f3-b0d6080778d6.png"}]]
   [:p {:id "1bb0109e-1131-809c-b37f-dd5b8ccf29c9"
        :class ""}]
   [:p {:id "1bb0109e-1131-8015-ac7e-dddbd577311c"
        :class ""}
    [:br]
    "And once it reaches your IP address (which is either it’s your local machine that has ports open or most likely remote machine (web-cloud e.g. AWS, gcloud, Hertz, digitalocean) where your app is deployed."
    [:br]
    [:br]
    [:br]
    [:br]
    "The Server itself just needs to forward that request to your running application backend."
    [:br]]
   [:p {:id "1bb0109e-1131-8042-abec-fde0852d0cc6"
        :class ""}]
   [:figure {:id "1bb0109e-1131-80b5-88f4-fb712580a818"
             :class "image"}
    [:img {:style {"width" "687.0317002881845px"}
           :src "routing/b928a8e1-8db7-465e-b57e-73e0d5ce986c.png"}]]
   [:p {:id "1bb0109e-1131-80a8-8149-e76be0fa18b7"
        :class ""}]
   [:p {:id "1bb0109e-1131-8020-a4ee-d9a11adddf75"
        :class ""}
    [:br]
    [:br]
    "So most likely you will have on the server reverse proxy setup (e.g. nginx) that forwards that request to your app running e.g. on localhost:3000."
    [:br]
    "This reverse proxy setup is also helpful when scaling, as you could forward it to different instances or with more resources of your app running, but also for security, as here often request is forwarded from HTTP to HTTPS (adding SSL/TLS certificate)."
    [:br]
    [:br]]
   [:h2 {:id "1b80109e-1131-80bf-bfd5-fe46d6f67891"
         :class ""}
    [:a {:href "#1b80109e-1131-80bf-bfd5-fe46d6f67891"
         :class "no-underline"}
     [:span
      {:class
       "heading-anchorlink-icon bg-base-content/5 hover:bg-primary/10 size-[1em] text-base-content/30 hover:text-primary/50 rounded-field border border-base-content/5 hover:border-primary/20 inline-grid place-content-center hover:shadow-sm hover:shadow-base-200 align-text-bottom me-3 lg:absolute lg:ms-[-1.5em] lg:mt-1 transition-all group"}
      [:svg {:class "group-hover:scale-100 scale-90 transition-transform"
             :fill "currentColor"
             :width ".5em"
             :height ".5em"
             :viewBox "0 0 256 256"
             :id "Flat"
             :xmlns "http://www.w3.org/2000/svg"}
       [:path
        {:d
         "M216,148H172V108h44a12,12,0,0,0,0-24H172V40a12,12,0,0,0-24,0V84H108V40a12,12,0,0,0-24,0V84H40a12,12,0,0,0,0,24H84v40H40a12,12,0,0,0,0,24H84v44a12,12,0,0,0,24,0V172h40v44a12,12,0,0,0,24,0V172h44a12,12,0,0,0,0-24Zm-108,0V108h40v40Z"}]]]]
    "4. Your app"]
   [:p {:id "1b80109e-1131-80dc-aa40-c3117fae2b8c"
        :class ""}
    "Finally! The request reaches your app! If all the previous steps went succesfully, your app will receive that request and decide what to do next and what"
    [:a {:href
         "https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status"}
     "response"]
    "to give back!"
    [:br]
    [:br]]
   [:figure {:id "1bb0109e-1131-8056-a1e9-dd477e32dbbf"
             :class "image"}
    [:img {:style {"width" "709.982666015625px"}
           :src "routing/image%202.png"}]]
   [:p {:id "1bb0109e-1131-800e-acb1-cc0de071d580"
        :class ""}
    [:br]
    [:br]
    "The response will look like this:"
    [:br]]
   [:script
    {:src "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js"
     :integrity
     "sha512-7Z9J3l1+EYfeaPKcGXu3MS/7T+w19WtKQY/n+xzmw4hZhJ9tyYmcUS+4QqAlzhicE5LAfMQSF3iFTK9bQdTxXg=="
     :crossOrigin "anonymous"
     :referrerPolicy "no-referrer"}]
   [:link
    {:rel "stylesheet"
     :href
     "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.min.css"
     :integrity
     "sha512-tN7Ec6zAFaVSG3TpNAKtk4DOHNpSwKHxxrsiw4GHKESGPs5njn/0sMCUMl2svV4wo4BK/rCP7juYz+zx+l6oeQ=="
     :crossOrigin "anonymous"
     :referrerPolicy "no-referrer"}]
   [:pre {:id "1bb0109e-1131-80de-a1ad-cf477fe4bb5b"
          :class "code"}
    [:code {:class "language-Visual Basic"}
     "HTTP/1.1 200 OK\nContent-Type: text/html\n\n<!doctype html>… (here come the 29769 bytes of the requested web page)"]]
   [:p {:id "1bb0109e-1131-80ef-be64-e90854af7e42"
        :class ""}
    [:br]
    "In the case of modern web applications (SPA), most likely our backend for GET requests to display a page, will send as a response our frontend."
    [:br]
    "Which is almost empty HTML, needed CSS and JS that will execute and build the frontend (e.g. react) and from now on it will handle further communication."
    [:br]
    [:br]
    "So after that is done, when user clicks to go to next page (e.g. About page), we won’t need to send request through the whole process we just described. It will be our frontend routing (JS) capturing that and just rendering new content."
    [:br]
    [:br]
    "Same with other kind of requests that are further sent, like clicking “create” button on the page, it will be frontend (js, react)  going to new frontend route, generating new html and if needed sending request to backend to save/get some information for display."
    [:br]
    [:br]
    "Basically from the point of receiving the response, JS hijacks the communication and all of that is happening on the client side (inside the browser)."
    [:br]
    [:br]
    "And what needs to be handled on our server/backend is sent on the client side by that frontend as a request and managed."
    [:br]
    [:br]
    "Alternative to that is SSR (Server Side Rendering), which is about creating the full HTML that user sees, on the backend and sending it as it is."
    [:br]
    [:br]
    "Often there is a mix between SSR and SPA. For many businesses a landing page needs to be super efficient for web-crawlers and optimized under page loading time, while the main app is under different request as a SPA that does not need that performance optimalization, and can bundle big JS files with react and all goodies."
    [:br]
    [:br]
    [:br]
    "Now that we have general understanding, let’s go deeper and jump into implementation of that for clojure application."
    [:br]
    [:br]
    "Some topics may not interest you (e.g. setting up DNS or server) so if you don’t need to deal with it go straight to backend/frontend routing."
    [:br]]
   [:p {:id "1bb0109e-1131-80b4-91b6-c3326bcf738e"
        :class ""}]])
