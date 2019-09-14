package net.socialhub.service.action.group;

import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.UserGroup;

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
