package net.socialhub.utils;

public final class StringUtil {

    /**
     * XML 内のタグを除去
     */
    public static String removeXmlTags(String xml) {
        return xml.replaceAll("<.*?>", "");
    }
}
