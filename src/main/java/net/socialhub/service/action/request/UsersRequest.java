package net.socialhub.service.action.request;

import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Request;
import net.socialhub.model.service.User;

import javax.annotation.Nonnull;

public interface UsersRequest extends Request {

    /**
     * Get Users
     * ユーザーを取得
     */
    Pageable<User> getUsers(Paging paging);

    /**
     * To Serialized String
     * シリアライズ化された文字列を取得
     */
    String toSerializedString();

    /**
     * From Serialized String
     * シリアライズ文字列から生成
     */
    static CommentsRequest fromSerializedString() {
        return null;
    }
}
