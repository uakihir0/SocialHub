package net.socialhub.utils;

import net.socialhub.core.model.common.AttributedString;
import net.socialhub.core.model.common.xml.XmlConvertRule;
import net.socialhub.core.model.common.xml.XmlDocument;
import net.socialhub.core.utils.XmlParseUtil;
import org.junit.Test;

public class XmlParseUtilTest {

    @Test
    public void testParseXml() {

        XmlDocument document = XmlParseUtil
                .xhtml("プログラミングで<br/>\n\r"
                        + "わからないときは<img src=\"image.png\"><br>"
                        + "まず<a href=\"http://google.com\">ググる</a>！");

        AttributedString string = document.toAttributedString(new XmlConvertRule());
        System.out.println(string.getDisplayText());
    }
}
