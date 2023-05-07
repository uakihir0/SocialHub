package net.socialhub.core.action.request;

import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Request;
import net.socialhub.core.model.User;

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
     * Get Serialize Builder
     * シリアライズビルダーの取得
     */
    RequestActionImpl.SerializeBuilder getSerializeBuilder();
}
