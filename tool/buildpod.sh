#!/bin/bash

# ---------------------------------------------------------------------------------- #
# DEBUG
sed -i -e '16,16d' j2objc-SocialHub-debug.podspec
sed -i -e "16s|^|    'HEADER_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/include \$(PODS_ROOT)/src/main/objc'|" j2objc-SocialHub-debug.podspec

sed -i -e '18,18d' j2objc-SocialHub-debug.podspec
sed -i -e "18s|^|    'LIBRARY_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/lib'|" j2objc-SocialHub-debug.podspec

sed -i -e '20,20d' j2objc-SocialHub-debug.podspec
sed -i -e "20s|^|    'LIBRARY_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/lib/macosx'|" j2objc-SocialHub-debug.podspec

sed -i -e '22,22d' j2objc-SocialHub-debug.podspec
sed -i -e "22s|^|    'LIBRARY_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/lib'|" j2objc-SocialHub-debug.podspec

# ---------------------------------------------------------------------------------- #
# RELEASE
sed -i -e '16,16d' j2objc-SocialHub-release.podspec
sed -i -e "16s|^|    'HEADER_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/include \$(PODS_ROOT)/src/main/objc'|" j2objc-SocialHub-release.podspec

sed -i -e '18,18d' j2objc-SocialHub-release.podspec
sed -i -e "18s|^|    'LIBRARY_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/lib'|" j2objc-SocialHub-release.podspec

sed -i -e '20,20d' j2objc-SocialHub-release.podspec
sed -i -e "20s|^|    'LIBRARY_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/lib/macosx'|" j2objc-SocialHub-release.podspec

sed -i -e '22,22d' j2objc-SocialHub-release.podspec
sed -i -e "22s|^|    'LIBRARY_SEARCH_PATHS' => '\$(PODS_ROOT)/j2objc/lib'|" j2objc-SocialHub-release.podspec

# ---------------------------------------------------------------------------------- #
# Delete Back
rm -f *.podspec-e
