(ns mateuszmazurczak.ui.components.avatar
  "Avatar component for displaying user/entity images with fallback support.
  
  Version: 1.0.0
  Last updated: 2025-02-05
  
  Based on Radix UI Avatar primitive.
  Documentation: https://www.radix-ui.com/primitives/docs/components/avatar"
  (:require
   ["@radix-ui/react-avatar"      :as RadixAvatar]
   [mateuszmazurczak.utils.styles :refer [merge-classes]]))

(defn avatar
  "Avatar root component. Displays an image or fallback for a user/entity.
  
  Props:
  - `:size` - Size variant (:default, :sm, :lg). Default is :default
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [avatar {}
    [avatar-image {:src \"https://github.com/user.png\" :alt \"User\"}]
    [avatar-fallback {} \"UN\"]]
  
  With badge:
  [avatar {}
    [avatar-image {:src \"https://github.com/user.png\" :alt \"User\"}]
    [avatar-fallback {} \"UN\"]
    [avatar-badge {:class \"bg-green-600\"}]]"
  [{:keys [class size]
    :or {size :default}
    :as props}
   &
   children]
  (into
   [:>
    RadixAvatar/Root
    (->
      props
      (assoc
       :data-slot "avatar"
       :data-size (name size)
       :class
       (merge-classes
        "group/avatar relative flex size-8 shrink-0 rounded-full select-none data-[size=lg]:size-10 data-[size=sm]:size-6"
        class))
      (dissoc :class-name :size))]
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
       (assoc :data-slot "avatar-image"
              :class (merge-classes "aspect-square h-full w-full overflow-hidden rounded-[inherit]"
                                    class))
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
  (into
   [:>
    RadixAvatar/Fallback
    (->
      props
      (assoc
       :data-slot "avatar-fallback"
       :class
       (merge-classes
        "bg-muted text-muted-foreground flex size-full items-center justify-center rounded-full text-sm group-data-[size=sm]/avatar:text-xs"
        class))
      (dissoc :class-name))]
   children))

(defn avatar-badge
  "Avatar badge component. Displays a badge indicator at the bottom right of the avatar.
  Can contain icons or be used as a simple status indicator.
  Automatically sizes based on the parent avatar's size variant.
  
  Props:
  - `:class` - Additional Tailwind classes to merge with defaults
  
  Example:
  [avatar-badge {:class \"bg-green-600\"}]
  [avatar-badge {} [:> PlusIcon]]"
  [{:keys [class]
    :as props}
   &
   children]
  (into
   [:span
    (->
      props
      (assoc
       :data-slot "avatar-badge"
       :class
       (merge-classes
        "bg-primary text-primary-foreground ring-background absolute right-0 bottom-0 z-10 inline-flex items-center justify-center rounded-full ring-2 select-none"
        "group-data-[size=sm]/avatar:size-2 group-data-[size=sm]/avatar:[&>svg]:hidden"
        "group-data-[size=default]/avatar:size-2.5 group-data-[size=default]/avatar:[&>svg]:size-2"
        "group-data-[size=lg]/avatar:size-3 group-data-[size=lg]/avatar:[&>svg]:size-2" class))
      (dissoc :class-name))]
   children))
