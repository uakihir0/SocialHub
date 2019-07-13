package net.socialhub.model.service.addition.tumblr;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;

import java.util.List;

/**
 * Tumblr Comment Model
 * Tumblr のコメント情報
 */
public class TumblrComment extends Comment {

    private Long noteCount;

    private Boolean isLiked;

    public TumblrComment(Service service) {
        super(service);
    }

    // Reblog Key
    private String reblogKey;

    @Override
    public Comment getDisplayComment() {
        return (getSharedComment() != null) //
                ? this.getSharedComment() : this;
    }

    @Override
    public List<Reaction> getReactions() {
        List<Reaction> reactions = super.getReactions();

        Reaction note = new Reaction();
        note.setCount(noteCount);
        note.setName("note");
        reactions.add(note);

        return reactions;
    }

    //region // Getter&Setter
    public String getReblogKey() {
        return reblogKey;
    }

    public void setReblogKey(String reblogKey) {
        this.reblogKey = reblogKey;
    }

    public Long getNoteCount() {
        return noteCount;
    }

    public void setNoteCount(Long noteCount) {
        this.noteCount = noteCount;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }
    //endregion
}

