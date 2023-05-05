package net.socialhub.core.model.service;

import net.socialhub.core.model.service.support.PollOption;

import java.util.Date;
import java.util.List;

/**
 * Poll
 * 投票情報
 */
public class Poll extends Identify {

    public Poll(Service service) {
        super(service);
    }

    /** 有効期限 (時刻) */
    private Date expireAt;

    /** 有効期限切れしたかどうか？ */
    private boolean isExpired = false;

    /** 複数投票が可能か？ */
    private boolean multiple = false;

    /** 投票数 */
    private Long votesCount;

    /** 投票人数 */
    private Long votersCount;

    /** 認証ユーザーが投票したかどうか？ */
    private boolean isVoted = false;

    /** 投票選択肢 */
    private List<PollOption> options;

    /**
     * 投票の反映
     */
    public void applyVote(List<Integer> choices) {
        if (choices == null || choices.size() == 0) {
            return;
        }

        isVoted = true;
        if (votersCount != null) {
            votersCount++;
        }
        if (votesCount != null) {
            votesCount += choices.size();
        }
        choices.forEach((i) -> {
            if (options.size() > i) {
                options.get(i).applyVote();
            }
        });
    }

    // region
    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public Long getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(Long votesCount) {
        this.votesCount = votesCount;
    }

    public Long getVotersCount() {
        return votersCount;
    }

    public void setVotersCount(Long votersCount) {
        this.votersCount = votersCount;
    }

    public boolean isVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
    // endregion
}
