(ns mateuszmazurczak.articles.routing-big-picture)

(def article-content
  [:div
   [:p
    "Routing is complicated. Despite many years of experience in web development, setting up complete routing and troubleshooting is still challenging."]
   [:p
    "This article aims to provide a general, shallow understanding of how client requests reach your application and what occurs during that process."]
   [:p
    "Here is a diagram representing a big picture of what happens between our client opening our page in a browser and him getting the response (website content)."]
   [:p
    "In this article, we will go briefly over all those elements so you can understand step by step what’s happening, between when someone enters the website and receives the content."]
   [:p
    "This big-picture overview is essential for understanding where problems with routing can happen."]
   [:p
    [:img {:src "img/Big%20picture%201bb0109e113180bf89cfdd873a04069f/image.png"
           :title nil
           :alt "image.png"}]]
   [:p "The first step begins when someone opens our website in their browser"]
   [:h2 {:id "1.-user-opens-our-website"}
    [:a {:href "#1.-user-opens-our-website"
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
   [:p
    "When the user opens the page, what happens under the hood is that the browser is sending a message to GitHub and expects the message back."
    " "
    "The message is sent over "
    [:a {:href "https://developer.mozilla.org/en-US/docs/Web/HTTP"}
     "HTTP protocol"]
    " and sending it from the client to a server is called Request."]
   [:p
    "One example of sending a request is opening a website, e.g. entering “"
    [:a {:href "http://github.com"}
     "github.com"]
    "/trending”."
    " "
    "Which looks something like this:"]
   [:pre.code
    [:code.language-visual-basic
     "GET /trending HTTP/1.1\nHost: [www.github.com](http://www.github.com)\n"]]
   [:p
    "This kind of request is called GET request (as it expects to retrieve data)."]
   [:h2 {:id "2.-dns"}
    [:a {:href "#2.-dns"
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
   [:p
    "But for your browser to send the request to host "
    [:a {:href "http://github.com"}
     "github.com"]
    " it’s not enough information. It needs to know exactly which computer to send it to."]
   [:p
    [:a
     {:href
      "https://developer.mozilla.org/en-US/docs/Learn/Common_questions/Web_mechanics/How_does_the_Internet_work"}
     "Because internet/web is just a ton of computers connected together"]
    " and each device has unique IP address when it connects to the web, which is a numerical label like 8.8.8.8 (IPv4 and IPv6 are just different ways to write them)."]
   [:p
    "Theorethically we could skip domain names and only use the addresses, but because address like  74.125.24.102 is not easily memorable and words like github.com are, there is Domain Name System (DNS) that resolves name to IP address."]
   [:p
    "So to know which IP address is behind github.com"
    " "
    "First your browser checks if you don’t have this IP address already saved locally."]
   [:p
    "If you don’t have it, it will resolve it via your internet service provider or public service (e.g. Google DNS, Cloudflare). Where they have acess to the registry of providers (e.g. OVH) that will tell them if this is something they recognize."]
   [:p
    "And if they do recognize it they will respond to them with IP address connected to that name."]
   [:p
    "So if you want to be found on the web via a website name you need to buy a domain and configure which IP should it be forwarded to."]
   [:p
    "So when you buy a domain from service providers like OVH, they have registered their nameservers in those services, so when the DNS resolver asks, OVH it will answer - oh yeah I know that domain name! This is the IP for it."]
   [:p "And that’s the first part of the diagram"]
   [:p
    [:img {:src
           "img/Big%20picture%201bb0109e113180bf89cfdd873a04069f/image%201.png"
           :title nil
           :alt "image.png"}]]
   [:h2 {:id "3.-server"}
    [:a {:href "#3.-server"
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
   [:p
    "So now that we know the IP of the computer under that domain, the request is sent there."]
   [:p
    [:img
     {:src
      "img/Big%20picture%201bb0109e113180bf89cfdd873a04069f/e8cc37ca-878d-410c-a8f3-b0d6080778d6.png"
      :title nil
      :alt "image.png"}]]
   [:p
    "And once it reaches your IP address (which is either it’s your local machine that has ports open or most likely remote machine (web-cloud e.g. AWS, gcloud, Hertz, digitalocean) where your app is deployed."]
   [:p
    "The Server itself just needs to forward that request to your running application backend."]
   [:p
    [:img
     {:src
      "img/Big%20picture%201bb0109e113180bf89cfdd873a04069f/b928a8e1-8db7-465e-b57e-73e0d5ce986c.png"
      :title nil
      :alt "image.png"}]]
   [:p
    "So most likely you will have on the server reverse proxy setup (e.g. nginx) that forwards that request to your app running e.g. on localhost:3000."
    " "
    "This reverse proxy setup is also helpful when scaling, as you could forward it to different instances or with more resources of your app running, but also for security, as here often request is forwarded from HTTP to HTTPS (adding SSL/TLS certificate)."]
   [:h2 {:id "4.-your-app"}
    [:a {:href "#4.-your-app"
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
   [:p
    "Finally! The request reaches your app! If all the previous steps went succesfully, your app will receive that request and decide what to do next and what "
    [:a {:href
         "https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status"}
     "response"]
    " to give back!"]
   [:p
    [:img {:src
           "img/Big%20picture%201bb0109e113180bf89cfdd873a04069f/image%202.png"
           :title nil
           :alt "image.png"}]]
   [:p "The response will look like this:"]
   [:pre.code
    [:code.language-visual-basic
     "HTTP/1.1 200 OK\nContent-Type: text/html\n\n<!doctype html>… (here come the 29769 bytes of the requested web page)\n\n"]]
   [:p
    "In the case of modern web applications (SPA), most likely our backend for GET requests to display a page, will send as a response our frontend."
    " "
    "Which is almost empty HTML, needed CSS and JS that will execute and build the frontend (e.g. react) and from now on it will handle further communication."]
   [:p
    "So after that is done, when user clicks to go to next page (e.g. About page), we won’t need to send request through the whole process we just described. It will be our frontend routing (JS) capturing that and just rendering new content."]
   [:p
    "Same with other kind of requests that are further sent, like clicking “create” button on the page, it will be frontend (js, react)  going to new frontend route, generating new html and if needed sending request to backend to save/get some information for display."]
   [:p
    "Basically from the point of receiving the response, JS hijacks the communication and all of that is happening on the client side (inside the browser)."]
   [:p
    "And what needs to be handled on our server/backend is sent on the client side by that frontend as a request and managed."]
   [:p
    "Alternative to that is SSR (Server Side Rendering), which is about creating the full HTML that user sees, on the backend and sending it as it is."]
   [:p
    "Often there is a mix between SSR and SPA. For many businesses a landing page needs to be super efficient for web-crawlers and optimized under page loading time, while the main app is under different request as a SPA that does not need that performance optimalization, and can bundle big JS files with react and all goodies."]
   [:p
    "Now that we have general understanding, let’s go deeper and jump into implementation of that for clojure application."]
   [:p
    "Some topics may not interest you (e.g. setting up DNS or server) so if you don’t need to deal with it go straight to backend/frontend routing."]])
