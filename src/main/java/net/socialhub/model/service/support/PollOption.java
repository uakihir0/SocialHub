package net.socialhub.model.service.support;

/**
 * Poll's Option
 * 投票における選択肢
 */
public class PollOption {

    private Long index;

    private String title;

    private Long count;

    /** 認証ユーザーが投票したかどうか？ */
    private boolean isVoted = false;

    // region
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public boolean isVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }
    // endregion
}
