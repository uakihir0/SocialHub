package net.socialhub.define.service.twitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Twitter Search Support Class
 */
public class TwitterSearchBuilder {

    // 検索クエリ
    private TwitterSearchQuery query;

    // 特定のユーザーを検索
    private String screenName;

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

        if (query != null) {
            parts.add("(" + query.buildQuery() + ")");
        }
        if (screenName != null) {
            parts.add("AND from:" + screenName);
        }
        if (source != null) {
            parts.add("AND source:" + source);
        }
        if (minRetweets != null) {
            parts.add("AND min_retweets:" + minRetweets);
        }
        if (minFaves != null) {
            parts.add("AND min_faves:" + minFaves);
        }
        if (minReplies != null) {
            parts.add("AND min_replies:" + minReplies);
        }

        if (includeContents != null) {
            for (TwitterFilterType type : includeContents) {
                parts.add("AND filter:" + type.getCode());
            }
        }
        if (excludeContents != null) {
            for (TwitterFilterType type : excludeContents) {
                parts.add("AND -filter:" + type.getCode());
            }
        }

        if (language != null) {
            for (TwitterLanguageType lang : language) {
                parts.add("AND lang:" + lang.getCode());
            }
        }

        String q =  String.join(" ", parts);
        System.out.println(q);
        return q;
    }

    public TwitterSearchBuilder query(TwitterSearchQuery query) {
        this.query = query;
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
