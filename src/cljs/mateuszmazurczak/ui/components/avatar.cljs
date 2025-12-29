(ns mateuszmazurczak.ui.components.avatar
  "https://www.radix-ui.com/primitives/docs/components/avatar"
  (:require
   ["@radix-ui/react-avatar"      :as RadixAvatar]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn avatar
  "Avatar root component. Displays an image or fallback for a user/entity.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [avatar {}
    [avatar-image {:src \"https://github.com/user.png\" :alt \"User\"}]
    [avatar-fallback {} \"UN\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         RadixAvatar/Root
         (-> props
             (assoc :data-slot "avatar"
                    :class
                    (merge-classes
                     "relative flex h-10 w-10 shrink-0 overmateuszmazurczak-hidden rounded-full"
                     class))
             (dissoc :class-name))]
        children))

(defn avatar-image
  "Avatar image component. Displays the actual image.
  
  Props:
  - `:src` - Image source URL (required)
  - `:alt` - Alt text for accessibility
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [avatar-image {:src \"https://github.com/user.png\" :alt \"User Name\"}]"
  [{:keys [class]
    :as props}]
  [:>
   RadixAvatar/Image
   (-> props
       (assoc :data-slot "avatar-image" :class (merge-classes "aspect-square h-full w-full" class))
       (dissoc :class-name))])

(defn avatar-fallback
  "Avatar fallback component. Displays when image is not available.
  Typically shows initials or an icon.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [avatar-fallback {} \"JD\"]
  [avatar-fallback {:class \"bg-blue-500 text-white\"} \"AB\"]"
  [{:keys [class]
    :as props}
   &
   children]
  (into [:>
         RadixAvatar/Fallback
         (-> props
             (assoc :data-slot "avatar-fallback"
                    :class (merge-classes
                            "flex h-full w-full items-center justify-center rounded-full bg-muted"
                            class))
             (dissoc :class-name))]
        children))
