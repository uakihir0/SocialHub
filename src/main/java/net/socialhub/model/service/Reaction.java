package net.socialhub.model.service;

/**
 * Reaction Model
 * リアクションモデル
 */
public class Reaction {

    private String name;

    private String emoji;

    private String iconUrl;

    private Long count;

    //region // Getter&Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    //endregion
}
