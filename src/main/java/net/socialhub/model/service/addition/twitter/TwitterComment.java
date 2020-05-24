package net.socialhub.model.service.addition.twitter;

import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.MiniBlogComment;
import net.socialhub.service.action.AccountAction;
import net.socialhub.service.twitter.TwitterAction;
import net.socialhub.service.twitter.TwitterMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Twitter Comment Model
 * Twitter のコメント情報
 */
public class TwitterComment extends MiniBlogComment {

    public TwitterComment(Service service) {
        super(service);
    }

    @Override
    public String getWebUrl() {
        return "https://twitter.com/"
                + getUser().getScreenName()
                + "/status/" + getId().toString();
    }

    /**
     * {@inheritDoc}
     * Caution : Network request cause if user me not cached.
     * 注意：自分のアカウントをキャッシュしていないとネットワークリクエストが発生
     */
    @Override
    public CommentForm getReplyForm() {
        if (getDirectMessage()) {
            return super.getReplyForm();
        }

        // ツイートの返信アカウントを取得する
        List<String> accounts = TwitterMapper
                .getRepliesScreenNames(getText().getDisplayText());

        // 存在しない場合は対象垢のみ
        if (accounts.isEmpty()) {
            return super.getReplyForm();
        }

        // このコメントの対象アカウントを追加して自身を除外
        List<String> candidates = new ArrayList<>(accounts);
        candidates.add(getUser().getAccountIdentify());

        // 以下でネットワークリクエストが発生するので注意
        AccountAction account = getService().getAccount().action();
        if (account instanceof TwitterAction) {
            User me = ((TwitterAction) account).getUserMeWithCache();
            candidates.remove(me.getAccountIdentify());
        }

        String replyTo = candidates.stream().distinct()
                .collect(Collectors.joining(" "));

        CommentForm form = new CommentForm();
        form.text(replyTo + " ");
        form.targetId(getId());
        form.message(false);
        return form;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TwitterComment) {
            return getId().equals(((TwitterComment) obj).getId());
        }
        return false;
    }
}
