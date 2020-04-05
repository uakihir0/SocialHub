package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Poll;
import net.socialhub.model.service.Service;

public class MisskeyPoll extends Poll {

    /** 親ノートの ID 情報 */
    private String noteId;

    public MisskeyPoll(Service service) {
        super(service);
    }

    // region
    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    // endregion
}
