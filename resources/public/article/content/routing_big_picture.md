Routing is complicated. Despite many years of experience in web development, setting up complete routing and troubleshooting is still challenging.

This article aims to provide a general, shallow understanding of how client requests reach your application and what occurs during that process.

Here is a diagram representing a big picture of what happens between our client opening our page in a browser and him getting the response (website content).

In this article, we will go briefly over all those elements so you can understand step by step what’s happening, between when someone enters the website and receives the content.

This big-picture overview is essential for understanding where problems with routing can happen.

![image.png](Big%20picture%201bb0109e113180bf89cfdd873a04069f/image.png)

The first step begins when someone opens our website in their browser 

## 1. User opens our website

When the user opens the page, what happens under the hood is that the browser is sending a message to GitHub and expects the message back.
The message is sent over [HTTP protocol](https://developer.mozilla.org/en-US/docs/Web/HTTP) and sending it from the client to a server is called Request.

One example of sending a request is opening a website, e.g. entering “[github.com](http://github.com)/trending”.
Which looks something like this:

```visual-basic
GET /trending HTTP/1.1
Host: [www.github.com](http://www.github.com)
```

This kind of request is called GET request (as it expects to retrieve data).

## 2. DNS

But for your browser to send the request to host [github.com](http://github.com) it’s not enough information. It needs to know exactly which computer to send it to.

[Because internet/web is just a ton of computers connected together](https://developer.mozilla.org/en-US/docs/Learn/Common_questions/Web_mechanics/How_does_the_Internet_work) and each device has unique IP address when it connects to the web, which is a numerical label like 8.8.8.8 (IPv4 and IPv6 are just different ways to write them). 

Theorethically we could skip domain names and only use the addresses, but because address like  74.125.24.102 is not easily memorable and words like github.com are, there is Domain Name System (DNS) that resolves name to IP address.

So to know which IP address is behind github.com
First your browser checks if you don’t have this IP address already saved locally.

If you don’t have it, it will resolve it via your internet service provider or public service (e.g. Google DNS, Cloudflare). Where they have acess to the registry of providers (e.g. OVH) that will tell them if this is something they recognize.

And if they do recognize it they will respond to them with IP address connected to that name.

So if you want to be found on the web via a website name you need to buy a domain and configure which IP should it be forwarded to.

So when you buy a domain from service providers like OVH, they have registered their nameservers in those services, so when the DNS resolver asks, OVH it will answer - oh yeah I know that domain name! This is the IP for it.

And that’s the first part of the diagram

![image.png](Big%20picture%201bb0109e113180bf89cfdd873a04069f/image%201.png)

## 3. Server

So now that we know the IP of the computer under that domain, the request is sent there.

![image.png](Big%20picture%201bb0109e113180bf89cfdd873a04069f/e8cc37ca-878d-410c-a8f3-b0d6080778d6.png)

And once it reaches your IP address (which is either it’s your local machine that has ports open or most likely remote machine (web-cloud e.g. AWS, gcloud, Hertz, digitalocean) where your app is deployed.

The Server itself just needs to forward that request to your running application backend. 

![image.png](Big%20picture%201bb0109e113180bf89cfdd873a04069f/b928a8e1-8db7-465e-b57e-73e0d5ce986c.png)

So most likely you will have on the server reverse proxy setup (e.g. nginx) that forwards that request to your app running e.g. on localhost:3000. 
This reverse proxy setup is also helpful when scaling, as you could forward it to different instances or with more resources of your app running, but also for security, as here often request is forwarded from HTTP to HTTPS (adding SSL/TLS certificate).

## 4. Your app

Finally! The request reaches your app! If all the previous steps went succesfully, your app will receive that request and decide what to do next and what [response](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status) to give back!

![image.png](Big%20picture%201bb0109e113180bf89cfdd873a04069f/image%202.png)

The response will look like this:

```visual-basic
HTTP/1.1 200 OK
Content-Type: text/html

<!doctype html>… (here come the 29769 bytes of the requested web page)

```

In the case of modern web applications (SPA), most likely our backend for GET requests to display a page, will send as a response our frontend. 
Which is almost empty HTML, needed CSS and JS that will execute and build the frontend (e.g. react) and from now on it will handle further communication.

So after that is done, when user clicks to go to next page (e.g. About page), we won’t need to send request through the whole process we just described. It will be our frontend routing (JS) capturing that and just rendering new content.

Same with other kind of requests that are further sent, like clicking “create” button on the page, it will be frontend (js, react)  going to new frontend route, generating new html and if needed sending request to backend to save/get some information for display.

Basically from the point of receiving the response, JS hijacks the communication and all of that is happening on the client side (inside the browser).

And what needs to be handled on our server/backend is sent on the client side by that frontend as a request and managed.

Alternative to that is SSR (Server Side Rendering), which is about creating the full HTML that user sees, on the backend and sending it as it is. 

Often there is a mix between SSR and SPA. For many businesses a landing page needs to be super efficient for web-crawlers and optimized under page loading time, while the main app is under different request as a SPA that does not need that performance optimalization, and can bundle big JS files with react and all goodies.

Now that we have general understanding, let’s go deeper and jump into implementation of that for clojure application.

Some topics may not interest you (e.g. setting up DNS or server) so if you don’t need to deal with it go straight to backend/frontend routing.
