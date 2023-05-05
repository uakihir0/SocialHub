package net.socialhub.core.model.service.support;

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

    /**
     * Custom Reaction or Unicode Frequent level under 10.
     * https://home.unicode.org/the-most-frequent-emoji/
     */
    private boolean isFrequentlyUsed;

    /** Use for search */
    private String searchWord;

    private List<String> aliases;

    /**
     * Get All Names
     * エイリアスの返却処理
     */
    public List<String> getAllNames() {
        if (aliases != null) {
            List<String> results = new ArrayList<>(aliases);
            results.add(name);
            return results;
        }

        return Collections.singletonList(name);
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

    public boolean isFrequentlyUsed() {
        return isFrequentlyUsed;
    }

    public void setFrequentlyUsed(boolean frequentlyUsed) {
        isFrequentlyUsed = frequentlyUsed;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }
    //endregion
}
