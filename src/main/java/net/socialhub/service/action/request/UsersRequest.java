package net.socialhub.service.action.request;

import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Request;
import net.socialhub.model.service.User;

public interface UsersRequest extends Request {

    /**
     * Get Users
     * ユーザーを取得
     */
    Pageable<User> getUsers(Paging paging);

}
