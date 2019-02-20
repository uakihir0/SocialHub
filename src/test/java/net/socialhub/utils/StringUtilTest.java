package net.socialhub.utils;

import net.socialhub.model.service.common.AttributedString;
import net.socialhub.model.service.common.AttributedString.AttributedElements;
import org.junit.Test;

public class StringUtilTest {

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

    private void printAttributedString(AttributedString string) {

        System.out.println("====================================");
        System.out.println("Whole  : " + string.getText());

        for (AttributedElements attribute : string.getAttribute()) {
            System.out.println("------------------------------------");
            System.out.println("Test  : " + attribute.getText());
            System.out.println("Type  : " + attribute.getType());
            System.out.println("Start : " + attribute.getRange().getStart());
            System.out.println("End   : " + attribute.getRange().getEnd());
        }
    }
}
