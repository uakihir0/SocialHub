package net.socialhub.service.action.group;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.group.CommentGroup;

public interface RequestGroupAction {

    /**
     * Get Comments
     * コメントを取得
     */
    CommentGroup getComments();
}
