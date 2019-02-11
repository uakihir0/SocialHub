#!/bin/bash
cd "$(dirname "$0")"

cd ../build/j2objcSrcGenMain/
header="SocialHubHeader.h"

echo "#ifndef _SOCIALHUB_HEADER_GLOBAL" > ${header}
echo "#define _SOCIALHUB_HEADER_GLOBAL" >> ${header}

echo "#import \"JreEmulation.h\"" >> ${header}
#echo "#import \"JreEmulation.h\""

for file in `\find . -name '*.h'`; do
	if [[ $file != *${header}* ]]; then
        name=$(echo $file | sed -e "s/\.\///")
        echo "#include \"${name}\"" >> ${header}
        #echo "#include \"${name}\""
    fi
done

echo "#endif" >> ${header}

sleep 2
exit 0