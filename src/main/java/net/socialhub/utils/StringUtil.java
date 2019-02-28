package net.socialhub.utils;

import java.net.URL;

public final class StringUtil {

    private static final int MAX_DISPLAY_LENGTH = 26;

    /**
     * XML 内のタグを除去
     */
    public static String removeXmlTags(String xml) {
        return xml.replaceAll("<.*?>", "");
    }

    /**
     * 表示向け URL 文字列の作成
     */
    public static String getDisplayUrl(String str) {
        if (str == null) {
            return null;
        }

        // プロトコルがある場合
        String url = removeProtocolUrl(str);
        if (url.length() <= MAX_DISPLAY_LENGTH) {
            return url;
        }

        return url.substring(0, MAX_DISPLAY_LENGTH) + "...";
    }

    /**
     * Http のプロトコル部分を除去
     */
    public static String removeProtocolUrl(String str) {
        if (str == null) {
            return null;
        }

        // プロトコルがある場合
        if (str.startsWith("http")) {
            try {
                URL url = new URL(str);
                return url.getAuthority() + url.getFile();
            } catch (Exception ignore) {
            }
        }
        return str;
    }
}
