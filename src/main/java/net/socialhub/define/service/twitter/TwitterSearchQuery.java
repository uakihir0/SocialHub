package net.socialhub.define.service.twitter;

import java.util.ArrayList;
import java.util.List;

public class TwitterSearchQuery {

    // 検索自由文字列
    private String freeword;

    // ハッシュタグ検索
    private String hashTag;

    // 除外文字列
    private String exclusive;

    // AND 条件句の追加
    private List<TwitterSearchQuery> and;

    // OR 条件句の追加
    private List<TwitterSearchQuery> or;


    public TwitterSearchQuery freeword(String freeword) {
        this.freeword = freeword;
        return this;
    }

    public TwitterSearchQuery hashTag(String hashTag) {
        this.hashTag = hashTag;
        return this;
    }

    public TwitterSearchQuery exclusive(String exclusive) {
        this.exclusive = exclusive;
        return this;
    }


    public TwitterSearchQuery and(TwitterSearchQuery and) {
        if (this.and == null) {
            this.and = new ArrayList<>();
        }
        this.and.add(and);
        return this;
    }

    public TwitterSearchQuery or(TwitterSearchQuery or) {
        if (this.or == null) {
            this.or = new ArrayList<>();
        }
        this.or.add(or);
        return this;
    }

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

        StringBuilder query = new StringBuilder();
        if (parts.size() > 1) {
            query.append("(");
            query.append(String.join(" AND ", parts));
            query.append(")");

        } else if (parts.size() == 1) {
            query.append(parts.get(0));
        }

        if (and != null) {
            for (TwitterSearchQuery q : and) {
                query.append(" AND ").append(q.buildQuery());
            }
        }

        if (or != null) {
            for (TwitterSearchQuery q : or) {
                query.append(" OR ").append(q.buildQuery());
            }
        }

        return query.toString();
    }
}
