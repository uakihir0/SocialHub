#!/bin/bash

cd build/j2objcOutputs/src/main/objc/
header="net/socialhub/SocialHubHeader.h"
echo "#import \"JreEmulation.h\"" > ${header}

for file in `\find . -name '*.h'`; do
    name=$(echo $file | sed -e "s/\.\///")
    echo "#include \"${name}\"" >> ${header}
done