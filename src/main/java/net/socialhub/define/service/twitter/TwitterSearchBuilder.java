package net.socialhub.define.service.twitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Twitter Search Support Class
 */
public class TwitterSearchBuilder {

    // 検索自由文字列
    private String freeword;

    // ハッシュタグ検索
    private String hashTag;

    // 特定のユーザーを検索
    private String screenName;

    // 除外文字列
    private String exclusive;

    // 検索投稿ソース
    private String source;

    // 最低リツイート数
    private Integer minRetweets;

    // 最低お気に入り数
    private Integer minFaves;

    // 最低返信数
    private Integer minReplies;

    // フィルタリングタイプ
    private List<TwitterFilterType> includeContents;
    private List<TwitterFilterType> excludeContents;

    // 対象言語の絞り込み
    private List<TwitterLanguageType> language;


    /**
     * クエリ作成
     */
    public String buildQuery() {
        List<String> parts = new ArrayList<>();

        if (freeword != null) {
            parts.add(freeword);
        }
        if (exclusive != null) {
            parts.add("-" + exclusive);
        }
        if (hashTag != null) {
            parts.add("#" + hashTag);
        }
        if (screenName != null) {
            parts.add("from:" + screenName);
        }
        if (source != null) {
            parts.add("source:" + source);
        }
        if (minRetweets != null) {
            parts.add("min_retweets:" + minRetweets);
        }
        if (minFaves != null) {
            parts.add("min_faves:" + minFaves);
        }
        if (minReplies != null) {
            parts.add("min_replies:" + minReplies);
        }

        if (includeContents != null) {
            for (TwitterFilterType type : includeContents) {
                parts.add("filter:" + type.getCode());
            }
        }
        if (excludeContents != null) {
            for (TwitterFilterType type : excludeContents) {
                parts.add("-filter:" + type.getCode());
            }
        }

        if (language != null) {
            for (TwitterLanguageType lang : language) {
                parts.add("lang:" + lang.getCode());
            }
        }

        return String.join(" ", parts);
    }


    public TwitterSearchBuilder freeword(String freeword) {
        this.freeword = freeword;
        return this;
    }

    public TwitterSearchBuilder hashTag(String hashTag) {
        this.hashTag = hashTag;
        return this;
    }

    public TwitterSearchBuilder exclusive(String exclusive) {
        this.exclusive = exclusive;
        return this;
    }

    public TwitterSearchBuilder screenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public TwitterSearchBuilder source(String source) {
        this.source = source;
        return this;
    }

    public TwitterSearchBuilder minRetweets(Integer minRetweets) {
        this.minRetweets = minRetweets;
        return this;
    }

    public TwitterSearchBuilder minFaves(Integer minFaves) {
        this.minFaves = minFaves;
        return this;
    }

    public TwitterSearchBuilder minReplies(Integer minReplies) {
        this.minReplies = minReplies;
        return this;
    }

    public TwitterSearchBuilder excludeRetweets() {
        return excludeContentType(TwitterFilterType.Retweets);
    }


    public TwitterSearchBuilder includeContentType(TwitterFilterType contentType) {
        if (this.includeContents == null) {
            this.includeContents = new ArrayList<>();
        }
        this.includeContents.add(contentType);
        return this;
    }

    public TwitterSearchBuilder excludeContentType(TwitterFilterType contentType) {
        if (this.excludeContents == null) {
            this.excludeContents = new ArrayList<>();
        }
        this.excludeContents.add(contentType);
        return this;
    }

    public TwitterSearchBuilder addLanguage(TwitterLanguageType language) {
        if (this.language == null) {
            this.language = new ArrayList<>();
        }
        this.language.add(language);
        return this;
    }

}
