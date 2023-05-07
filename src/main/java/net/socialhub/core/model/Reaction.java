package net.socialhub.core.model;

/**
 * Reaction Model
 * リアクションモデル
 */
public class Reaction {

    private String name;

    private String emoji;

    private String iconUrl;

    private Long count;

    /** 認証ユーザーがリアクションしたか？ */
    private boolean isReacting = false;

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

    public boolean getReacting() {
        return isReacting;
    }

    public void setReacting(boolean reacting) {
        isReacting = reacting;
    }

    //endregion
}
