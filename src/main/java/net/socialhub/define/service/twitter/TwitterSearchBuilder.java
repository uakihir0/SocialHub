package net.socialhub.define.service.twitter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.SimpleFormatter;

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

    // 以降にしたツイート取得
    private Date since;

    // 以前にしたツイート取得
    private Date until;

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

        if (since != null) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            parts.add("AND since:" + f.format(since));
        }

        if (until != null) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            parts.add("AND until:" + f.format(since));
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

        return String.join(" ", parts);
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

    public TwitterSearchBuilder since(Date since) {
        this.since = since;
        return this;
    }

    public TwitterSearchBuilder until(Date until) {
        this.until = until;
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
