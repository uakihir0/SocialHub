package net.socialhub.utils;

import net.socialhub.model.common.AttributedString;
import net.socialhub.model.common.XmlConvertRule;
import net.socialhub.model.common.XmlDocument;
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
