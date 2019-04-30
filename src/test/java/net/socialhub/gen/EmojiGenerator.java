package net.socialhub.gen;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import net.socialhub.http.HttpClientImpl;
import net.socialhub.http.HttpRequest;
import net.socialhub.http.HttpResponse;
import net.socialhub.logger.Logger;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static net.socialhub.http.RequestMethod.GET;

/**
 * Generate Emoji List from
 * https://github.com/iamcal/emoji-data
 */
public class EmojiGenerator {

    private final static String EMOJI_JSON = "https://raw.githubusercontent.com/iamcal/emoji-data/master/emoji.json";

    @Test
    public void makeEmojiList() {
        getEmojiList().forEach((emoji) -> {
            String name = emoji.getShortName();

            StringBuilder builder = new StringBuilder();
            if (!name.matches("^[a-zA-Z].*$")) {
                builder.append("Emoji");
            }

            for (String part : name //
                    .replaceAll("^\\+", "Plus") //
                    .replaceAll("^-", "Minus") //
                    .split("[_\\-]")) {

                builder.append(caption(part));
            }

            System.out.println(builder + "(" +
                    "\"" + decodeUnicode(emoji.getUnified()) + "\"," +
                    "\"" + name + "\",\"" + emoji.getCategory() + "\"),");
        });
    }

    @Test
    public void makeEmojiCategoryList() {

        getEmojiList().stream() //
                .map(EmojiData::getCategory) //
                .distinct().forEach((category) -> {

            StringBuilder builder = new StringBuilder();
            for (String part : category.split("\\s&?\\s?")) {
                builder.append(caption(part));
            }

            System.out.println(builder //
                    + "(\"" + category + "\"),");
        });
    }

    /**
     * Get Emoji Json from GitHub
     */
    private List<EmojiData> getEmojiList() {
        Logger logger = Logger.getLoggerFactory().getLogger(null);
        logger.setLogLevel(Logger.LogLevel.WARN);

        try {
            // JSON に対してリクエストを投げて取得
            HttpRequest request = new HttpRequest(GET, EMOJI_JSON, null, null);
            HttpResponse response = new HttpClientImpl().request(request);

            // 型情報の作成
            Type type = new TypeToken<List<EmojiData>>() {
            }.getType();

            return new Gson().fromJson(response.asString(), type);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            logger.setLogLevel(Logger.LogLevel.DEBUG);
        }
    }

    /**
     * Make Caption String
     */
    private String caption(String str) {
        return str.substring(0, 1).toUpperCase() //
                + str.substring(1).toLowerCase();
    }

    /**
     * Get Original Icon
     * Unicode の表示を元のものに戻す
     */
    private String decodeUnicode(String unicode) {
        String[] codes = unicode.split("-");
        int[] points = new int[codes.length];

        for (int i = 0; i < points.length; i++) {
            points[i] = Integer.parseInt(codes[i], 16);
        }

        return new String(points, 0, points.length);
    }

    /**
     * Emoji Data Structure
     * (https://github.com/iamcal/emoji-data)
     */
    public static class EmojiData {
        @SerializedName("short_name")
        private String shortName;
        private String category;
        private String unified;

        //region // Getter&Setter
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getUnified() {
            return unified;
        }

        public void setUnified(String unified) {
            this.unified = unified;
        }
        //endregion
    }
}
