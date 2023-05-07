package net.socialhub.core.action.request;

import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Request;
import net.socialhub.core.model.Stream;

public interface CommentsRequest extends Request {

    /**
     * Get Comments
     * コメントを取得
     */
    Pageable<Comment> getComments(Paging paging);

    /**
     * Get Comment Stream
     * コメントストリームを取得
     */
    Stream setCommentsStream(EventCallback callback);

    /**
     * Get Flags of Comment Stream Support
     * コメントストリームが使用可能かを？
     */
    boolean canUseCommentsStream();

    /**
     * Make Comment Request
     * コメントリクエストの雛形作成
     */
    CommentForm getCommentFrom();

    /**
     * To Serialized String
     * シリアライズ化された文字列を取得
     */
    String toSerializedString();

    /**
     * Get Serialize Builder
     * シリアライズビルダーの取得
     */
    RequestActionImpl.SerializeBuilder getSerializeBuilder();

}
