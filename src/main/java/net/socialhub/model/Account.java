package net.socialhub.model;


import net.socialhub.model.service.Service;
import net.socialhub.service.action.AccountAction;

/**
 * Account Model
 * (Not SNS User model)
 * アカウント情報を扱うモデル
 * (各サービス毎のユーザーではない点に注意)
 */
public class Account {

    private Service service;

    private AccountAction action;

    //region // Getter&Setter
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public AccountAction getAction() {
        return action;
    }

    public void setAction(AccountAction action) {
        this.action = action;
    }
    //endregion
}

