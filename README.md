# Mateusz Mazurczak website 
This is a private project representing www.mateuszmazurczak.com (and .pl) 

## Architecture

## Set up
Updating system variables in Mac: 
https://phoenixnap.com/kb/set-environment-variable-mac


## Running the App
From this directory run:
- bb dev-launch

Tests: localhost:8081
Shadow: localhost:9630
App: localhost:3000

After that you can connect to running repl (8000).


## General
src/bb/ <- only for bb.edn, contains all useful scripts for working with this app

Code in this repo is for my personal website, and it's built in a way to cover all the web-app usefull functionalities and code separation for quick starting web projects in clojure.

Current state of code contains:
- Scripting for simplifying work with the project
- Configuration
- Environments separation 
- Translation i18n (with taoensso tempura)
- Full-stack routing and setup (ring, reitit, shadow-cljs)
- Basic UI with integrated hiccup, Tailwind, DaisyUI
- Frontend logic and data manged with reagent/re-frame
- Portfolio setup for frontend development
- User error monitoring (with sentry)

TODO as features:
- Logs  (directly in the app like telmere, sentry, connecting to things like google logs etc.)
- Chat to speak to - so instead of saying contact me at *this-email*, just open chat option that sends email or smth
- UI theme
- Realtime module with information that the page has been updated, so user can click and hard-refresh
- describe testing FE/BE/E2E/ab
- Frontend analysis tooling, heatmaps, users on the page etc. 
- Database integration
- Auth (and feature-flags)
- Versioning (low priority)

## Adding articles
- [ ]  Write article in .md (or in Notion and convert to .md)
- [ ]  Put it in resources/public/article/content
- [ ]  If any imgs put them in resources/public/article/img
- [ ]  Add article in articles.edn
- [ ]  run bb article 
- [ ]  add :content to your article in articles inside articles.core
------- In future it will be moved to only html and static rendering, removing the last point ----

License information can be found in [LICENSE file](LICENSE.md)
Copyright © 2024 Mateusz Mazurczak

