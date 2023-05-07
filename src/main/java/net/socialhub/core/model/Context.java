package net.socialhub.core.model;

import java.util.List;

/**
 * Comment Context
 */
public class Context {

    /**
     * Get ancestor comments.
     * 特定のコメントより前のコンテキストを取得
     */
    private List<Comment> ancestors;

    /**
     * Get descendant comments.
     * 特定のコメントより後のコンテキストを取得
     */
    private List<Comment> descendants;

    //region // Getter&Setter
    public List<Comment> getAncestors() {
        return ancestors;
    }

    public void setAncestors(List<Comment> ancestors) {
        this.ancestors = ancestors;
    }

    public List<Comment> getDescendants() {
        return descendants;
    }

    public void setDescendants(List<Comment> descendants) {
        this.descendants = descendants;
    }
    //endregion
}
