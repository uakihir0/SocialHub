#!/usr/bin/env bash
cd "$(dirname "$0")"
J2OBJC="2.8"

# !! Change for Your Environment !!
J2OBJC_PATH=$HOME/Documents/library/j2objc/$J2OBJC
COCOAPODS_PATH=$HOME/Documents/library/cocoapods

# Set Output
cd ../build/j2objcOutputs/

# DEBUG
sed -i -e '16,16d' j2objc-SocialHub-debug.podspec
sed -i -e "16s|^|    'HEADER_SEARCH_PATHS' => '$J2OBJC_PATH/include $COCOAPODS_PATH/SocialHub/src/main/objc'|" j2objc-SocialHub-debug.podspec

sed -i -e '18,18d' j2objc-SocialHub-debug.podspec
sed -i -e "18s|^|    'LIBRARY_SEARCH_PATHS' => '$J2OBJC_PATH/lib'|" j2objc-SocialHub-debug.podspec

sed -i -e '20,20d' j2objc-SocialHub-debug.podspec
sed -i -e "20s|^|    'LIBRARY_SEARCH_PATHS' => '$J2OBJC_PATH/lib/macosx'|" j2objc-SocialHub-debug.podspec

sed -i -e '22,22d' j2objc-SocialHub-debug.podspec
sed -i -e "22s|^|    'LIBRARY_SEARCH_PATHS' => '$J2OBJC_PATH/lib'|" j2objc-SocialHub-debug.podspec

# RELEASE
sed -i -e '16,16d' j2objc-SocialHub-release.podspec
sed -i -e "16s|^|    'HEADER_SEARCH_PATHS' => '$J2OBJC_PATH/include $COCOAPODS_PATH/SocialHub/src/main/objc'|" j2objc-SocialHub-release.podspec

sed -i -e '18,18d' j2objc-SocialHub-release.podspec
sed -i -e "18s|^|    'LIBRARY_SEARCH_PATHS' => '$J2OBJC_PATH/lib'|" j2objc-SocialHub-release.podspec

sed -i -e '20,20d' j2objc-SocialHub-release.podspec
sed -i -e "20s|^|    'LIBRARY_SEARCH_PATHS' => '$J2OBJC_PATH/lib/macosx'|" j2objc-SocialHub-release.podspec

sed -i -e '22,22d' j2objc-SocialHub-release.podspec
sed -i -e "22s|^|    'LIBRARY_SEARCH_PATHS' => '$J2OBJC_PATH/lib'|" j2objc-SocialHub-release.podspec

# Delete Back
rm *.podspec-e

# CocoaPods Repository
rsync -av --delete ./ $COCOAPODS_PATH/SocialHub/ \
--exclude '.DS_Store'