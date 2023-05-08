package net.socialhub.core.utils;

import net.socialhub.core.define.SpecialCharType;
import net.socialhub.core.model.common.xml.XmlDocument;
import net.socialhub.core.model.common.xml.XmlString;
import net.socialhub.core.model.common.xml.XmlTag;
import net.socialhub.core.model.error.SocialHubException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParseUtil {

    /**
     * Parse Simple XHTML String
     * 簡単な XHTML をパース処理
     */
    public static XmlDocument xhtml(String string) {

        // For Root Element Missing
        string = "<xml>" + string + "</xml>";

        // Delete System NewLine
        string = string.replace("\n", "");
        string = string.replace("\r", "");

        // Remove Script Async
        string = string.replace(" async", " ");
        string = string.replace(" allowfullscreen=\"\"", " ");
        string = string.replace(" allowfullscreen", " ");

        // Regex like: <(br|BR)(.*?)/?>
        String[] tags = {"br", "img", "hr"};
        for (String tag : tags) {
            string = replace(string, "<(" + tag + "|" + //
                    tag.toUpperCase() + ")(.*?)/?>", "<$1$2/>");
        }

        // Remove Target Attributes
        string = string.replace(" _blank ", " ");
        string = string.replace("target=\"_blank\"", "");

        // Some Tumblr Post NOT escaped & in href in a tag.
        string = string.replaceAll("&(?![#a-zA-Z0-9]+;)", "&amp;");

        // Replace &nbsp; to &#160; (library issue)
        for (SpecialCharType sp : SpecialCharType.values()) {
            string = string.replaceAll(sp.getEntityRepl(), sp.getNumberRepl());
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            InputStream is = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
            XMLParseHandler handler = new XMLParseHandler();
            parser.parse(is, handler);

            XmlDocument document = new XmlDocument();
            document.setRoot(handler.getRoot());
            return document;

        } catch (Exception e) {
            System.out.println(string);
            throw new SocialHubException(e);
        }
    }

    private static String replace(String from, String regex, String to) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(from);
        return m.replaceAll(to);
    }

    /**
     * XML パーサ
     */
    public static class XMLParseHandler extends DefaultHandler {

        private XmlTag root;

        private Stack<XmlTag> tags = new Stack<>();

        @Override
        public void startDocument() {
        }

        @Override
        public void characters(
                char[] ch,
                int start,
                int length) {

            XmlString string = new XmlString();
            string.setString(new String(Arrays.copyOfRange(ch, start, start + length)));
            tags.peek().getElements().add(string);
        }

        @Override
        public void startElement(
                String uri,
                String localName,
                String qName,
                Attributes attributes) {

            XmlTag tag = new XmlTag();
            tag.setName(qName);

            for (int i = 0; i < attributes.getLength(); i++) {
                tag.getAttributes().put(attributes.getQName(i), attributes.getValue(i));
            }

            // タグを登録
            if (tags.size() != 0) {
                tags.peek().getElements().add(tag);

            } else {

                // ルート要素に指定
                root = tag;
            }

            // スタックに追加
            tags.push(tag);
        }

        @Override
        public void endElement(
                String uri,
                String localName,
                String qName) {

            // スタックから削除
            tags.pop();
        }

        public XmlTag getRoot() {
            return root;
        }
    }
}
