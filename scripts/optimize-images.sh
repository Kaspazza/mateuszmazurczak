#!/bin/bash
# Image optimization script
# Converts PNG/JPG to WebP and AVIF formats
# Requires: imagemagick (brew install imagemagick)
# Requires: libavif (brew install libavif) for AVIF support

set -e

IMG_DIR="resources/public/img"
ARTICLE_IMG_DIR="resources/public/article/img"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🖼️  Image Optimization Script"
echo "=============================="

# Check if imagemagick is installed
if ! command -v convert &> /dev/null; then
    echo -e "${RED}❌ ImageMagick not found. Install with: brew install imagemagick${NC}"
    exit 1
fi

echo -e "${GREEN}✅ ImageMagick found${NC}"

# Function to optimize image
optimize_image() {
    local input=$1
    local quality=${2:-85}
    
    # Get file info
    local dir=$(dirname "$input")
    local filename=$(basename "$input")
    local name="${filename%.*}"
    local ext="${filename##*.}"
    
    # Skip if already WebP
    if [[ "$ext" == "webp" || "$ext" == "avif" ]]; then
        echo -e "${YELLOW}⏭️  Skipping $filename (already optimized format)${NC}"
        return
    fi
    
    # Skip system files
    if [[ "$filename" == ".DS_Store" || "$filename" == ".keep" ]]; then
        return
    fi
    
    local webp_output="$dir/$name.webp"
    
    # Get original size
    local original_size=$(du -h "$input" | cut -f1)
    
    echo -e "\n📸 Processing: $filename (${original_size})"
    
    # Convert to WebP
    if [[ ! -f "$webp_output" ]]; then
        echo "   → Converting to WebP..."
        convert "$input" -quality $quality "$webp_output"
        local webp_size=$(du -h "$webp_output" | cut -f1)
        echo -e "   ${GREEN}✓ Created: $name.webp (${webp_size})${NC}"
    else
        echo -e "   ${YELLOW}⏭️  WebP already exists${NC}"
    fi
    
    echo ""
}

# Process main images
echo -e "\n${GREEN}Processing main images...${NC}"
shopt -s nullglob
for img in "$IMG_DIR"/*.png "$IMG_DIR"/*.jpg "$IMG_DIR"/*.jpeg "$IMG_DIR"/*.PNG "$IMG_DIR"/*.JPG "$IMG_DIR"/*.JPEG; do
    [ -f "$img" ] && optimize_image "$img" 85
done
shopt -u nullglob

# Process article images (higher quality)
echo -e "\n${GREEN}Processing article images...${NC}"
find "$ARTICLE_IMG_DIR" -type f \( -iname "*.png" -o -iname "*.jpg" -o -iname "*.jpeg" \) | while read img; do
    optimize_image "$img" 90
done

echo -e "\n${GREEN}✅ Optimization complete!${NC}"
echo ""
echo "📊 Image Inventory:"
echo "==================="
echo "WebP images:"
find resources/public -name "*.webp" -exec du -h {} \; | sort -h
echo ""
echo "Original images (can be removed after verification):"
find resources/public/img -type f \( -name "*.png" -o -name "*.jpg" \) -not -name "spinner.gif" -exec du -h {} \; | sort -h

echo -e "\n${YELLOW}⚠️  Next steps:${NC}"
echo "1. Test the website with new WebP images"
echo "2. If everything works, remove original PNG/JPG files"
echo "3. Consider creating multiple sizes for responsive images"
