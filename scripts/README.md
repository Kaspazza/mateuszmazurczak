# Image Optimization

## Quick Start

```bash
# Convert all PNG/JPG to WebP
bb optimize-images

# Or run directly
./scripts/optimize-images.sh
```

## Results

- **mateusz_mazurczak.png**: 312KB → 28KB WebP (91% reduction) ✨
- **not_found.jpg**: 884KB → 160KB WebP (82% reduction) 🎉
- **Article images**: 96-160KB → 20-68KB WebP (~60-80% reduction)

## Usage in Code

```clojure
(require '[mateuszmazurczak.ui.components.image :as ui-img])

;; Hero/above-fold (loads immediately)
[ui-img/optimized-img {:src "/img/hero.webp"
                       :alt "Hero image"
                       :width 800
                       :height 600
                       :loading "eager"
                       :fetchpriority "high"}]

;; Below-fold (lazy loads)
[ui-img/optimized-img {:src "/img/article.webp"
                       :alt "Article thumbnail"
                       :width 400
                       :height 300
                       :loading "lazy"}]
```

## After Testing

Once you verify WebP images work, remove originals:

```bash
rm resources/public/img/*.png
rm resources/public/img/*.jpg
```
