package net.socialhub.service.action.request;

import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Request;
import net.socialhub.model.service.Stream;
import net.socialhub.service.action.RequestActionImpl.SerializeBuilder;
import net.socialhub.service.action.callback.EventCallback;

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
    SerializeBuilder getSerializeBuilder();

}
