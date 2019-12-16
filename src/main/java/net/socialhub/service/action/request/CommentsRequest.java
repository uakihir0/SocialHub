package net.socialhub.service.action.request;

import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.*;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.RequestActionImpl.SerializeBuilder;
import net.socialhub.service.action.callback.EventCallback;

import javax.annotation.Nonnull;

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
