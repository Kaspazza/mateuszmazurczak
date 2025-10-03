(ns mateuszmazurczak.utils.macro)

#?(:clj
     (defmacro keep-callsite
       "The long-standing CLJ-865 means that it's not possible for an inner
     macro to access the `&form` metadata of a wrapping outer macro. This
     means that wrapped macros lose calsite info, etc."
       [form]
       `(with-meta ~form (meta ~'&form))))
