package net.socialhub.core.model;

import net.socialhub.core.define.emoji.EmojiType;
import net.socialhub.core.define.emoji.EmojiVariationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Emoji
 */
public class Emoji {

    /**
     * 絵文字本体
     * (カスタム絵文字の場合はショートコードが入る)
     */
    private String emoji;

    /**
     * 絵文字のショートコード
     * (:smile: など)
     */
    private List<String> shortCodes;

    /**
     * カスタム絵文字の場合の画像 URL
     */
    private String imageUrl;

    /**
     * 絵文字のカテゴリ
     */
    private String category;

    /**
     * 絵文字の頻度
     * (カスタム絵文字の場合は 0 固定)
     */
    private Integer frequentLevel;

    public static Emoji fromEmojiType(EmojiType e) {
        Emoji emoji = new Emoji();
        emoji.setEmoji(e.getEmoji());
        emoji.addShortCode(e.getName());
        emoji.setCategory(e.getCategory().getCode());
        emoji.setFrequentLevel(e.getLevel());
        return emoji;
    }

    public static Emoji fromEmojiVariationType(EmojiVariationType e) {
        Emoji emoji = new Emoji();
        emoji.setEmoji(e.getEmoji());
        emoji.addShortCode(e.getName());
        emoji.setCategory(e.getCategory().getCode());
        emoji.setFrequentLevel(e.getLevel());
        return emoji;
    }

    public void addShortCode(String shortCode) {
        getShortCodes().add(shortCode);
    }

    public String getShortCode() {
        return getShortCodes().get(0);
    }

    // region // Getter&Setter
    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public List<String> getShortCodes() {
        if (shortCodes == null) {
            shortCodes = new ArrayList<>();
        }
        return shortCodes;
    }

    public void setShortCodes(List<String> shortCodes) {
        this.shortCodes = shortCodes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getFrequentLevel() {
        return frequentLevel;
    }

    public void setFrequentLevel(Integer frequentLevel) {
        this.frequentLevel = frequentLevel;
    }
    // endregion
}
