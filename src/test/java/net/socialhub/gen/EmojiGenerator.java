package net.socialhub.gen;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import net.socialhub.http.HttpClientImpl;
import net.socialhub.http.HttpRequest;
import net.socialhub.http.HttpResponse;
import net.socialhub.logger.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.socialhub.http.RequestMethod.GET;

/**
 * Generate Emoji List from
 * https://github.com/iamcal/emoji-data
 */
public class EmojiGenerator {

    private final static String EMOJI_JSON = "https://raw.githubusercontent.com/iamcal/emoji-data/master/emoji.json";

    private final static String EMOJI_FREQUENCY_JSON = "https://home.unicode.org/emoji/emoji-frequency/";

    @Test
    @Ignore
    public void makeEmojiList() {
        List<EmojiStructure> results = new ArrayList<>();

        // 通常の処理
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

            EmojiStructure model = new EmojiStructure();
            model.setFieldName(builder.toString());
            model.setCategory(emoji.getCategory());
            model.setUnicode(emoji.getUnified());
            model.setName(name);

            results.add(model);
        });

        // スキントーンの処理
        getEmojiList().forEach((emoji) -> {

            if (emoji.getSkinVariations() != null) {
                EmojiStructure prefix = results.stream() //
                        .filter((e) -> e.getUnicode().equals(emoji.getUnified())) //
                        .findFirst().orElse(null);

                emoji.getSkinVariations().forEach((k, v) -> {
                    EmojiStructure skin = results.stream() //
                            .filter((e) -> e.getUnicode().equals(k)) //
                            .findFirst().orElse(null);

                    if (prefix != null && skin != null) {

                        EmojiStructure model = new EmojiStructure();
                        model.setFieldName(prefix.getFieldName() + skin.getFieldName());
                        model.setName(prefix.getName() + "::" + skin.getName());
                        model.setCategory(emoji.getCategory());
                        model.setUnicode(v.getUnified());

                        results.add(model);
                    }
                });
            }
        });

        List<EmojiFrequency> frequencies = getEmojiFrequency();

        for (EmojiStructure model : results) {
            String decodedEmoji = decodeUnicode(model.getUnicode());

            // スキントーンが含まれるもの以外は一致で確認
            Integer level = frequencies.stream().filter(e ->
                    (model.getName().contains("skin-tone-"))
                            ? decodedEmoji.contains(e.getEmoji())
                            : decodedEmoji.equals(e.getEmoji()))
                    .map(EmojiFrequency::getLevel)
                    .findFirst().orElse(null);

            System.out.println(model.getFieldName() + "("
                    + "\"" + decodedEmoji + "\","
                    + "\"" + model.getName() + "\","
                    + "\"" + model.getCategory() + "\","
                    + level + "),");
        }
    }

    @Test
    @Ignore
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

    @Test
    public void makeUnicodeDecode() {
        System.out.println(decodeUnicode("1F468-200D-1F3ED"));
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
     * Get Emoji Frequency from Unicode Site
     */
    private List<EmojiFrequency> getEmojiFrequency() {
        try {
            Document document = Jsoup.connect(EMOJI_FREQUENCY_JSON).get();
            List<EmojiFrequency> results = new ArrayList<>();

            Elements elements = document.select("table > tbody > tr");
            elements.forEach(element -> {
                try {
                    Elements tds = element.select("td");
                    String[] emojis = tds.get(1).html().split(" ");
                    Integer level = Integer.parseInt(tds.get(0).html());

                    for (String emoji : emojis) {
                        EmojiFrequency frequency = new EmojiFrequency();
                        frequency.setEmoji(emoji);
                        frequency.setLevel(level);
                        results.add(frequency);
                    }

                } catch (Exception ignore) {
                }
            });

            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
     * Pre Print Structure of Emoji
     */
    public static class EmojiStructure {
        private String name;
        private String fieldName;
        private String unicode;
        private String category;

        //region // Getter&Setter
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getUnicode() {
            return unicode;
        }

        public void setUnicode(String unicode) {
            this.unicode = unicode;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
        //endregion
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

        @SerializedName("skin_variations")
        private Map<String, EmojiVariationData> skinVariations;

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

        public Map<String, EmojiVariationData> getSkinVariations() {
            return skinVariations;
        }

        public void setSkinVariations(Map<String, EmojiVariationData> skinVariations) {
            this.skinVariations = skinVariations;
        }
        //endregion
    }

    /**
     * Emoji Variation Structure
     * (https://github.com/iamcal/emoji-data)
     */
    public static class EmojiVariationData {
        private String unified;

        //region // Getter&Setter
        public String getUnified() {
            return unified;
        }

        public void setUnified(String unified) {
            this.unified = unified;
        }
        //endregion
    }

    /**
     * Emoji Frequency
     */
    public static class EmojiFrequency {
        private String emoji;
        private Integer level;

        // region
        public String getEmoji() {
            return emoji;
        }

        public void setEmoji(String emoji) {
            this.emoji = emoji;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }
        // endregion
    }
}
