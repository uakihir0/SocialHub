package net.socialhub.model.service.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Reaction Candidate
 * リアクション候補モデル
 */
public class ReactionCandidate {

    private String name;

    private String emoji;

    private String iconUrl;

    private String category;

    private List<String> aliases;

    /**
     * Get All Names
     * エイリアスの返却処理
     */
    public List<String> getAllNames() {
        return (aliases != null) ? //
                aliases : Collections.singletonList(name);
    }

    /**
     * Add Reaction Alias
     * エイリアスの追加処理
     */
    public void addAlias(List<String> aliases) {
        if (this.aliases == null) {
            this.aliases = new ArrayList<>();
            this.aliases.add(name);
        }

        for (String alias : aliases) {
            if (!this.aliases.contains(alias)) {
                this.aliases.add(alias);
            }
        }
    }

    /**
     * Add Reaction Alias
     * エイリアスの追加処理
     */
    public void addAlias(String... aliases) {
        addAlias(Arrays.asList(aliases));
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    //endregion
}
