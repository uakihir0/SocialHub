package net.socialhub.model.service.addition.mastodon;

import net.socialhub.define.service.mastodon.MastodonVisibility;
import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Service;

/**
 * Mastodon Comment Model
 * Mastodon のコメント情報
 */
public class MastodonComment extends Comment {

    /** Warning text (Mastodon only) */
    private AttributedString spoilerText;

    /** Open range */
    private MastodonVisibility visibility;

    public MastodonComment(Service service) {
        super(service);
    }

    //region // Getter&Setter
    public AttributedString getSpoilerText() {
        return spoilerText;
    }

    public void setSpoilerText(AttributedString spoilerText) {
        this.spoilerText = spoilerText;
    }

    public MastodonVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(MastodonVisibility visibility) {
        this.visibility = visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = MastodonVisibility.of(visibility);
    }
    //endregion
}
