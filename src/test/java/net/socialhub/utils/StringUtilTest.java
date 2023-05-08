package net.socialhub.utils;

import net.socialhub.core.utils.StringUtil;
import net.socialhub.service.slack.define.SlackAttributedTypes;
import net.socialhub.core.model.common.AttributedElement;
import net.socialhub.core.model.common.AttributedItem;
import net.socialhub.core.model.common.AttributedKind;
import net.socialhub.core.model.common.AttributedString;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testDecodeUrlEncode() {
        System.out.println(net.socialhub.core.utils.StringUtil.decodeUrl("&gt;"));
    }

    @Test
    public void testAttributes() {

        AttributedString string = AttributedString.plain( //
                "\nLINK: " //
                        + "https://t.co/rx5cgtsmIj " //
                        + "https://www.googole.com " //
                        + "https://www.yahoo.co.jp " //

                        + "\nEMAIL: " //
                        + "sample@example.com " //
                        + "ex+sample@example.com " //

                        + "\nPHONE: " //
                        + "08012345678 " //
                        + "(03)1234-5678 " //
                        + "+81-1234-5678 " //

                        + "\nHASH_TAG: " //
                        + "#TheWorld " //
                        + "#超会議 " //
                        + "＃闘会議 " //

                        + "\nMASTODON ACCOUNT: " //
                        + "@U_Akihir0@mstdn.jp " //
                        + "@TheWorld_JP@pawoo.net " //

                        + "\nTWITTER ACCOUNT: " //
                        + "@u_akihir0 " //
                        + "@TheWorld_JP " //
        );

        printAttributedString(string);
    }

    @Test
    public void testDisplayText() {

        AttributedString string = AttributedString.plain( //
                "\nLINK: " //
                        + "https://t.co/rx5cgtsmIj " //
                        + "https://t.co/scawU8ske9 " //
                        + "END");

        for (AttributedElement element : string.getElements()) {
            AttributedItem item = ((AttributedItem) element);
            if (item.getKind() == AttributedKind.LINK) {
                item.setDisplayText("www.google.com");
            }
        }

        printAttributedString(string);
    }

    @Test
    public void testSlackAttributeText() {

        AttributedString string = AttributedString.plain(
                "SLACK: " //
                        + "<https://www.googole.com> " //
                        + "<mailto:sample@example.com|sample@example.com> " //
                        + "<@ABCDEFG12> " //
                        + "END",
                SlackAttributedTypes.simple());

        printAttributedString(string);
    }

    @Test
    public void testGetDisplayUrl() {

        System.out.println(net.socialhub.core.utils.StringUtil.getDisplayUrl( //
                "http://www.example.com/path?query=10&query=20"));

        System.out.println(net.socialhub.core.utils.StringUtil.getDisplayUrl( //
                "http://www.example.com/path?12345"));
        System.out.println(net.socialhub.core.utils.StringUtil.getDisplayUrl( //
                "http://www.example.com/path?123456"));
    }

    @Test
    public void testTrimLast() {
        System.out.print(net.socialhub.core.utils.StringUtil.trimLast("A "));
        System.out.println("B");

        System.out.print(StringUtil.trimLast("A \n"));
        System.out.println("B");
    }

    private void printAttributedString(AttributedString string) {

        System.out.println("====================================");
        System.out.println("Text   : " + string.getDisplayText());

        for (AttributedElement attribute : string.getElements()) {
            if (attribute.getKind() != AttributedKind.PLAIN) {

                System.out.println("------------------------------------");
                System.out.println("Type    : " + attribute.getKind());
                System.out.println("Display : " + attribute.getDisplayText());
                System.out.println("Expand  : " + attribute.getExpandedText());
            }
        }
    }
}
