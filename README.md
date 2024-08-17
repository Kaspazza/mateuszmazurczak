# Mateusz Mazurczak website 
This is a private project representing www.mateuszmazurczak.com (and .pl) 

## Set up
Environment variables needed to properly run the app:
in dev mode for admin page:
GH_TOKEN - the value of your GitHub login token so the data about repos can be downloaded

All envs:
STORAGE_DATOMIC_URL - a string with datomic connection URL

Updating system variables in Mac: 
https://phoenixnap.com/kb/set-environment-variable-mac


## Running the App
From this directory run:
- wf-2

After that connect to repl and evaluate -main function in mateusz.core

License information can be found in [LICENSE file](LICENSE.md)
Copyright © 2024 Mateusz Mazurczak
