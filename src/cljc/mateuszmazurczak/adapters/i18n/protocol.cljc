(ns mateuszmazurczak.adapters.i18n.protocol)

(defprotocol Translator
  "Translation abstraction for hexagonal architecture"
  (-translate [translator language id]
              [translator language id params]
   "Translate a key to text in the given language, optionally with interpolation params.
    
    Params:
    * `language` - keyword or vector of language fallbacks
    * `id` - translation key (keyword)
    * `params` - optional map of parameters for interpolation
    
    Returns: translated string"))
