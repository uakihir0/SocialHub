package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import net.socialhub.model.service.support.ReactionCandidate;

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

    // ============================================================== //
    // Comment API
    // コメント関連 API
    // ============================================================== //

    /**
     * Like Comment
     * コメントにたいしてイイねする
     * (Twitter Mastodon ではお気に入りをする)
     */
    default void like(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unlike Comment
     * コメントに対してのイイねを取り消す
     * (Twitter Mastodon ではお気に入りを消す)
     */
    default void unlike(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Share Comment
     * コメントをシェアする
     */
    default void share(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unshare Comment
     * コメントのシェアを取り消す
     */
    default void unshare(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Reaction Comment
     * リアクションする
     */
    default void reaction(Identify id, String reaction) {
        throw new NotImplimentedException();
    }

    /**
     * UnReaction Comment
     * リアクションを取り消す
     */
    default void unreaction(Identify id, String reaction) {
        throw new NotImplimentedException();
    }

    /**
     * Get Reaction Candidates
     * リアクション候補を取得
     */
    default List<ReactionCandidate> getReactionCandidates() {
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
    // Alias
    // エイリアス
    // ============================================================== //

    /** Like <-> Favorite */
    default void favorite(Identify id) {
        like(id);
    }

    default void unfavorite(Identify id) {
        unlike(id);
    }

    /** Share <-> Retweet */
    default void retweet(Identify id) {
        share(id);
    }

    default void unretweet(Identify id) {
        unshare(id);
    }

    /** Channel <-> List */
    default Pageable<Channel> getLists() {
        return getChannels();
    }
}
