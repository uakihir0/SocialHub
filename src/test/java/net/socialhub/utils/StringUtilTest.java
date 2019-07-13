package net.socialhub.utils;

import net.socialhub.define.service.slack.SlackAttributedTypes;
import net.socialhub.model.common.AttributedElement;
import net.socialhub.model.common.AttributedString;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testDecodeUrlEncode() {
        System.out.println(StringUtil.decodeUrl("&gt;"));
    }

    @Test
    public void testAttributes() {

        AttributedString string = new AttributedString( //

                "\nLINK: " //
                        + "https://t.co/rx5cgtsmIj " //
                        + "https://www.googole.com " //
                        + "https://www.yahoo.co.jp " //
                        + "www.googole.com " //
                        + "www.yahoo.co.jp " //

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

        AttributedString string = new AttributedString( //
                "\nLINK: " //
                        + "https://t.co/rx5cgtsmIj " //
                        + "https://t.co/scawU8ske9 " //
                        + "END");

        for (AttributedElement element : string.getAttribute()) {
            element.setDisplayText("www.google.com");
        }

        printAttributedString(string);
    }

    @Test
    public void testSlackAttributeText() {

        AttributedString string = new AttributedString(
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

        System.out.println(StringUtil.getDisplayUrl( //
                "http://www.example.com/path?query=10&query=20"));

        System.out.println(StringUtil.getDisplayUrl( //
                "http://www.example.com/path?12345"));
        System.out.println(StringUtil.getDisplayUrl( //
                "http://www.example.com/path?123456"));
    }

    @Test
    public void testTrimLast() {
        System.out.print(StringUtil.trimLast("A "));
        System.out.println("B");

        System.out.print(StringUtil.trimLast("A \n"));
        System.out.println("B");
    }

    private void printAttributedString(AttributedString string) {

        System.out.println("====================================");
        System.out.println("Whole   : " + string.getText());
        System.out.println("Display : " + string.getDisplayText());

        for (AttributedElement attribute : string.getAttribute()) {
            System.out.println("------------------------------------");
            System.out.println("Type    : " + attribute.getType());
            System.out.println("Test    : " + attribute.getText());
            System.out.println("Display : " + attribute.getDisplayText());
            System.out.println("Start   : " + attribute.getRange().getStart());
            System.out.println("End     : " + attribute.getRange().getEnd());
        }
    }
}
