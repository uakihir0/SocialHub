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

    private boolean isLiked;

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

    @Override
    public void applyReaction(Reaction reaction) {
        if (reaction.getName().startsWith("like")) {
            if (reaction.getReacting() && !isLiked) {
                isLiked = true;
            }
            if (!reaction.getReacting() && isLiked) {
                isLiked = false;
            }
        }
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

    public boolean getLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    //endregion
}

