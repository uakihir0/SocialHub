#!/bin/bash
cd "$(dirname "$0")"
J2OBJC="2.7"

# !! Change for Your Environment !!
J2OBJC_PATH=$HOME/Documents/j2objc/$J2OBJC
COCOAPODS_PATH=$HOME/Documents/Repository/cocoapod/

# ---------------------------------------------------------------------------------- #
# Make WorkSpace
mkdir work
cd ./work

# ---------------------------------------------------------------------------------- #
# Make GitRepo
mkdir repository
mkdir binary

# ---------------------------------------------------------------------------------- #
# Clone
cd repository

# Debug Binary
git clone -b Debug https://SocialHub@dev.azure.com/SocialHub/ObjCBinary/_git/ObjCBinary
rsync -av ./ObjCBinary/ ../binary/ \
--exclude '/.git/' \
--exclude '.DS_Store'

# Release Binary
cd ./ObjCBinary
git checkout Release
cd ../

rsync -av ./ObjCBinary/ ../binary/ \
--exclude '/.git/' \
--exclude '.DS_Store'

cd ../

# ---------------------------------------------------------------------------------- #
# Modify Podspec
cd binary

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
cd ../

# ---------------------------------------------------------------------------------- #
# Make CocoaPods Repository
cd binary

rsync -av --delete ./ $COCOAPODS_PATH/SocialHub/ \
--exclude '/.git/' \
--exclude '.DS_Store'

cd ../

# ---------------------------------------------------------------------------------- #
# Delete
cd ../
rm -rf ./work
