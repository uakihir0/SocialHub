package net.socialhub.service.action.group;

import net.socialhub.model.group.CommentGroup;

public interface CommentsRequestGroupAction {

    /**
     * Get Comments.
     * default count is 200.
     */
    CommentGroup getComments();

    /**
     * Get Comments with count.
     */
    CommentGroup getComments(int count);
}
