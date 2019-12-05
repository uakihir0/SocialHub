package net.socialhub.service.action;

import net.socialhub.model.service.Identify;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.UsersRequest;

public interface RequestAction {

    // ============================================================== //
    // User API
    // ユーザー関連 API
    // ============================================================== //

    /**
     * Get Following Account
     * フォローしているユーザー情報を取得
     */
    UsersRequest getFollowingUsers(Identify id);

    /**
     * Get Follower Account
     * フォローされているユーザー情報を取得
     */
    UsersRequest getFollowerUsers(Identify id);

    /**
     * Search User Account
     * ユーザーアカウントを検索
     */
    UsersRequest searchUsers(String query);

    // ============================================================== //
    // TimeLine API
    // タイムライン関連 API
    // ============================================================== //

    /**
     * Get Home TimeLine
     * ホームタイムラインを取得
     */
    CommentsRequest getHomeTimeLine();

    /**
     * Get Mention TimeLine
     * メンションタイムラインを取得
     */
    CommentsRequest getMentionTimeLine();

    /**
     * Get User Comment TimeLine
     * ユーザーの投稿したコメントのタイムラインを取得
     */
    CommentsRequest getUserCommentTimeLine(Identify id);

    /**
     * Get User Like TimeLine
     * ユーザーのイイネしたコメントのタイムラインを取得
     */
    CommentsRequest getUserLikeTimeLine(Identify id);

    /**
     * Get User Media TimeLine
     * ユーザーのメディア一覧を取得
     */
    CommentsRequest getUserMediaTimeLine(Identify id);

    /**
     * Get Search TimeLine
     * 検索タイムラインを取得
     */
    CommentsRequest getSearchTimeLine(String query);

    /**
     * Get Channel TimeLine
     * チャンネルのタイムラインを取得
     */
    CommentsRequest getChannelTimeLine(Identify id);

    /**
     * Get Message TimeLine
     * メッセージのタイムラインを取得
     */
    CommentsRequest getMessageTimeLine(Identify id);
}
