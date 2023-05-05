package net.socialhub.core.action.request;

import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.Request;
import net.socialhub.core.model.service.User;

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
