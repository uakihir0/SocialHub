#!/bin/bash
cd "$(dirname "$0")"

cd ../build/j2objcSrcGenMain/
header="SocialHubHeader.h"

echo "#ifndef _SOCIALHUB_HEADER_GLOBAL" > ${header}
echo "#define _SOCIALHUB_HEADER_GLOBAL" >> ${header}

# Add Global Header
echo "#import \"JreEmulation.h\"" >> ${header}

# Add Common Header
echo "#include \"java/lang/Integer.h\"" >> ${header}
echo "#include \"java/lang/Long.h\"" >> ${header}
echo "#include \"java/lang/Float.h\"" >> ${header}
echo "#include \"java/lang/Double.h\"" >> ${header}
echo "#include \"java/lang/Boolean.h\"" >> ${header}
echo "#include \"java/util/Date.h\"" >> ${header}

# Add SocialHub Header
for file in `\find . -name '*.h'`; do
	if [[ $file != *${header}* ]]; then
        name=$(echo $file | sed -e "s/\.\///")
        echo "#include \"${name}\"" >> ${header}
    fi
done

echo "#endif" >> ${header}

sleep 2
exit 0