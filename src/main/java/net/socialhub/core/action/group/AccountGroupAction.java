package net.socialhub.core.action.group;

import net.socialhub.core.model.group.CommentGroup;
import net.socialhub.core.model.group.UserGroup;

public interface AccountGroupAction {

    /**
     * Get All User's Information
     * グループのユーザー情報を取得
     */
    UserGroup getUserMe();

    /**
     * Get Timeline Comments
     * タイムラインを取得
     */
    CommentGroup getHomeTimeLine();
}
