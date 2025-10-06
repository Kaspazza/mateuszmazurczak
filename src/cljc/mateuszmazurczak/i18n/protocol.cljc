(ns mateuszmazurczak.i18n.protocol)

(defprotocol Translator
  "Translation abstraction for hexagonal architecture"
  (-translate [translator language id]
   "Translate a key to text in the given language.
    
    Params:
    * `language` - keyword or vector of language fallbacks
    * `id` - translation key (keyword)
    
    Returns: translated string"))
