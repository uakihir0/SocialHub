package net.socialhub.model.service.addition.mastodon;

import net.socialhub.model.common.AttributedElement;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.common.AttributedKind;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Emoji;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.addition.MiniBlogUser;

import java.net.URL;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Mastodon User Model
 * Mastodon のユーザー情報
 */
public class MastodonUser extends MiniBlogUser {

    /** attributed name (custom emoji included) */
    private AttributedString attributedName;

    /** attributed filed that user input */
    private List<AttributedFiled> fields;

    /** emojis which contains in name */
    private List<Emoji> emojis;

    public MastodonUser(Service service) {
        super(service);
    }

    /**
     * Get Attributed Name
     * 絵文字付き属性文字列を取得
     */
    public AttributedString getAttributedName() {
        if (attributedName == null) {
            attributedName = AttributedString.plain(getName(), emptyList());
            attributedName.addEmojiElement(emojis);
        }
        return attributedName;
    }

    /**
     * Get is custom emoji included user.
     * 絵文字付きのユーザー情報かを取得
     */
    public boolean isEmojiIncluded() {
        return emojis != null && !emojis.isEmpty();
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (name == null || name.isEmpty()) {
            return getScreenName().split("@")[0];
        }
        return name;
    }

    @Override
    public String getAccountIdentify() {
        try {
            // 外のホストの場合は既に URL が付与済
            if (getScreenName().contains("@")) {
                return "@" + getScreenName();
            }

            // URL と結合して Mastodon アカウント対応
            AttributedElement link = getProfileUrl().getElements().stream()
                    .filter(e -> e.getKind() == AttributedKind.LINK)
                    .findFirst().orElse(null);

            if (link != null) {
                URL url = new URL(link.getExpandedText());
                return "@" + getScreenName() + "@" + url.getHost();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getScreenName();
    }

    @Override
    public String getWebUrl() {
        String host = getAccountIdentify().split("@")[2];
        String identify = getAccountIdentify().split("@")[1];

        if (getService().isPleroma()) {
            return "https://" + host + "/" + identify;
        }
        return "https://" + host + "/@" + identify;
    }

    @Override
    public List<AttributedFiled> getAdditionalFields() {
        return getFields();
    }

    /**
     * Direct Message Form
     * メッセージフォームは Twitter と Mastodon で扱いが異なる
     * Mastodon の DM はユーザーの AccountIdentify が必要
     */
    @Override
    public CommentForm getMessageForm() {
        CommentForm form = new CommentForm();
        form.text(getAccountIdentify() + " ");
        form.message(true);
        return form;
    }

    // region // Getter&Setter
    public List<AttributedFiled> getFields() {
        return fields;
    }

    public void setFields(List<AttributedFiled> fields) {
        this.fields = fields;
    }

    public List<Emoji> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<Emoji> emojis) {
        this.emojis = emojis;
    }
    // endregion

}

