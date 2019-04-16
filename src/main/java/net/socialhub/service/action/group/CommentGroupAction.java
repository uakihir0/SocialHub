package net.socialhub.service.action.group;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.group.CommentGroup;

public interface CommentGroupAction {

    /**
     * Get Newer Comments
     * 最新コメントを取得
     */
    default CommentGroup getNewComments() {
        throw new NotImplimentedException();
    }

    /**
     * Get Older Comments
     * 遡ってコメントを取得
     */
    default CommentGroup getPastComments() {
        throw new NotImplimentedException();
    }
}
