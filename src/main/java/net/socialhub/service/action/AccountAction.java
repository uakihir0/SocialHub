package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Context;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Stream;
import net.socialhub.model.service.User;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.action.callback.EventCallback;

import java.util.List;

/**
 * Account Actions
 * (全てのアクションを定義)
 */
public interface AccountAction {

    // ============================================================== //
    // Account API
    // アカウント関連 API
    // ============================================================== //

    /**
     * Get Authorized My Account
     * 認証した自身のユーザー情報を取得
     */
    default User getUserMe() {
        throw new NotImplimentedException();
    }

    /**
     * Get Specific UserInfo
     * 特定のユーザーを取得
     */
    default User getUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Follow User
     * ユーザーをフォロー
     */
    default void followUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unfollow User
     * ユーザーをフォロー解除
     */
    default void unfollowUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Mute User
     * ユーザーをミュート
     */
    default void muteUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unmute User
     * ユーザーをミュート解除
     */
    default void unmuteUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Block User
     * ユーザーをブロック
     */
    default void blockUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unblock User
     * ユーザーをブロック解除
     */
    default void unblockUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Get relationship
     * 認証アカウントとの関係を取得
     */
    default Relationship getRelationship(Identify id) {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // User API
    // ユーザー関連 API
    // ============================================================== //

    /**
     * Get Following Account
     * フォローしているユーザー情報を取得
     */
    default Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Get Follower Account
     * フォローされているユーザー情報を取得
     */
    default Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Search User Account
     * ユーザーアカウントを検索
     */
    default Pageable<User> searchUsers(String query, Paging paging) {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // TimeLine API
    // タイムライン関連 API
    // ============================================================== //

    /**
     * Get Home TimeLine
     * ホームタイムラインを取得
     */
    default Pageable<Comment> getHomeTimeLine(Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Get Mention TimeLine
     * メンションタイムラインを取得
     */
    default Pageable<Comment> getMentionTimeLine(Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Get User Comment TimeLine
     * ユーザーの投稿したコメントのタイムラインを取得
     */
    default Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Get User Like TimeLine
     * ユーザーのイイネしたコメントのタイムラインを取得
     */
    default Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Get User Media TimeLine
     * ユーザーのメディア一覧を取得
     */
    default Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Get Search TimeLine
     * 検索タイムラインを取得
     */
    default Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Comment API
    // コメント関連 API
    // ============================================================== //

    /**
     * Post Comment
     * コメントを作成
     */
    default void postComment(CommentForm req) {
        throw new NotImplimentedException();
    }

    /**
     * Get Comment
     * 単品コメントを取得
     */
    default Comment getComment(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Like Comment
     * コメントにたいしてイイねする
     * (Twitter Mastodon ではお気に入りをする)
     */
    default void likeComment(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unlike Comment
     * コメントに対してのイイねを取り消す
     * (Twitter Mastodon ではお気に入りを消す)
     */
    default void unlikeComment(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Share Comment
     * コメントをシェアする
     */
    default void shareComment(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unshare Comment
     * コメントのシェアを取り消す
     */
    default void unshareComment(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Reaction Comment
     * リアクションする
     */
    default void reactionComment(Identify id, String reaction) {
        throw new NotImplimentedException();
    }

    /**
     * UnReaction Comment
     * リアクションを取り消す
     */
    default void unreactionComment(Identify id, String reaction) {
        throw new NotImplimentedException();
    }

    /**
     * Delete Comment
     * 自分のコメントを削除
     */
    default void deleteComment(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Get Reaction Candidates
     * リアクション候補を取得
     */
    default List<ReactionCandidate> getReactionCandidates() {
        throw new NotImplimentedException();
    }

    /**
     * Get Comment Context
     * コメントについて前後の会話を取得
     */
    default Context getCommentContext(Identify id) {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Channel (List) API
    // チャンネル (リスト) 関連 API
    // ============================================================== //

    /**
     * Get Channels (or Owned Lists)
     * 自分の閲覧可能なチャンネルを取得する
     */
    default Pageable<Channel> getChannels() {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Stream
    // ============================================================== //

    /**
     * Set Home TimeLine Stream
     * ホームタイムラインのストリームイベントを設定
     */
    default Stream setHomeTimeLineStream(EventCallback callback) {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Request
    // ============================================================== //

    /**
     * Get Request Objects
     * リクエストアクションを取得
     */
    default RequestAction request() {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Alias
    // エイリアス
    // ============================================================== //

    /**
     * Like <-> Favorite
     */
    default void favoriteComment(Identify id) {
        likeComment(id);
    }

    default void unfavoriteComment(Identify id) {
        unlikeComment(id);
    }

    /**
     * Share <-> Retweet
     */
    default void retweetComment(Identify id) {
        shareComment(id);
    }

    default void unretweetComment(Identify id) {
        unshareComment(id);
    }

    /**
     * Channel <-> List
     */
    default Pageable<Channel> getLists() {
        return getChannels();
    }
}
