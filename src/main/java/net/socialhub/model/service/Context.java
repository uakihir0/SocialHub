package net.socialhub.model.service;

import java.util.List;

/**
 * Comment Context
 */
public class Context {

    private List<Comment> ancestors;

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
