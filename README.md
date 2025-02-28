# Mateusz Mazurczak website 
This is a private project representing www.mateuszmazurczak.com (and .pl) 

## Set up
Environment variables needed to properly run the app:

GH_TOKEN - the value of your GitHub login token so the data about repos can be downloaded

STORAGE_DATOMIC_URL - a string with datomic connection URL

Updating system variables in Mac: 
https://phoenixnap.com/kb/set-environment-variable-mac


## Running the App
From this directory run:
- bb dev-launch

Tests: localhost:8081
Shadow: localhost:9630
App: localhost:3000

After that you can connect to running repl.


## General
src/bb/ <- only for bb.edn, contains all useful scripts for working with this app

Code in this repo is for my personal website, and it's built in a way to cover all the web-app usefull functionalities and code separation for quick starting web projects in clojure.

Current state of code contains:
- Scripting for easy working with the project
- Configuration
- Environments separation 
- Translation i18n (with taoensso tempura)
- Full-stack routing and setup (ring, reitit, shadow-cljs)
- Basic UI with hiccup, Tailwind, DaisyUI
- Frontend logic and data manged with reagent/re-frame
- Portfolio setup for frontend development
- User error monitoring (with sentry)

TODO:
- Logs (directly in the app like telmere, sentry, connecting to things like google logs etc.)
- describe testing FE/BE/E2E/ab testing 
- Frontend analysis tooling, heatmaps, users on the page etc. 
- Frontend a/b testing
- Database integration
- Auth
- Versioning
- Script for deployment (and deployment itself ;))
- Realtime module

License information can be found in [LICENSE file](LICENSE.md)
Copyright © 2024 Mateusz Mazurczak

