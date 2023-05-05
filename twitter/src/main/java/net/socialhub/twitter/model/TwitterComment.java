package net.socialhub.twitter.model;

import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.service.Service;
import net.socialhub.core.model.service.User;
import net.socialhub.microblog.model.MiniBlogComment;
import net.socialhub.core.action.AccountAction;
import net.socialhub.twitter.action.TwitterAction;
import net.socialhub.twitter.action.TwitterMapper;

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
        candidates.add(0, getUser().getAccountIdentify());

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
        form.replyId(getId());
        form.message(false);
        return form;
    }


    @Override
    public CommentForm getQuoteForm() {
        CommentForm form = new CommentForm();
        form.quoteId(getWebUrl());
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
