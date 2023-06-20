package net.socialhub.core.model.support;

import net.socialhub.core.model.Emoji;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reaction Candidate
 * リアクション候補モデル
 */
public class ReactionCandidate {

    /**
     * リアクション名
     */
    private String name;

    /**
     * リアクション名のエイリアス
     */
    private List<String> nameAliases;

    /**
     * When Emoji Reaction
     * 絵文字リアクションの場合
     */
    private Emoji emoji;

    /**
     * Get All Names
     * エイリアスの返却処理
     */
    public List<String> getAllNames() {
        if (getEmoji() != null) {
            return new ArrayList<>(getEmoji().getShortCodes());
        }

        List<String> results = new ArrayList<>(getNameAliases());
        results.add(getName());
        return results;
    }

    /**
     * Add Reaction Alias
     * エイリアスの追加処理
     */
    public void addAlias(List<String> aliases) {
        if (emoji != null) {
            emoji.getShortCodes().addAll(aliases);

            for (String alias : aliases) {
                if (!emoji.getShortCodes().contains(alias)) {
                    emoji.getShortCodes().add(alias);
                }
            }

        } else {
            if (nameAliases == null) {
                nameAliases = new ArrayList<>();
            }

            for (String alias : aliases) {
                if (!nameAliases.contains(alias)) {
                    nameAliases.add(alias);
                }
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
        if (emoji != null) {
            return emoji.getShortCodes().get(0);
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNameAliases() {
        if (nameAliases == null) {
            return new ArrayList<>();
        }
        return nameAliases;
    }

    public void setNameAliases(List<String> nameAliases) {
        this.nameAliases = nameAliases;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }
    //endregion
}
