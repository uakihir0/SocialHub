package net.socialhub.model.service;

import net.socialhub.define.ActionType;
import net.socialhub.define.ServiceType;
import org.apache.commons.lang3.time.DateUtils;
import twitter4j.RateLimitStatus;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SNS レートリミット
 * SNS RateLimit
 */
public class RateLimit implements Serializable {

    private Map<ActionType, RateLimitValue> dictionary = new HashMap<>();

    /**
     * レートリミット情報を格納
     * Set rate limit info
     * (For Twitter)
     */
    public void addInfo(ActionType action, twitter4j.TwitterResponse response) {
        RateLimitValue value = new RateLimitValue(response);
        dictionary.put(action, value);
    }

    /**
     * レートリミット情報を格納
     * Set rate limit info
     * (For Mastodon)
     */
    public void addInfo(ActionType action, mastodon4j.entity.share.Response<?> response) {
        RateLimitValue value = new RateLimitValue(response);
        dictionary.put(action, value);
    }

    /**
     * リクエスト可能かどうか？
     * Is remaining api request count?
     */
    public boolean isRemaining(ActionType action) {
        return dictionary.containsKey(action) && //
                dictionary.get(action).isRemaining();
    }

    private static class RateLimitValue {

        private ServiceType service;

        private int limit;
        private int remaining;
        private Date reset;

        /**
         * For Twitter
         */
        private RateLimitValue(twitter4j.TwitterResponse response) {
            RateLimitStatus rateLimit = response.getRateLimitStatus();
            this.service = ServiceType.Twitter;

            this.limit = rateLimit.getLimit();
            this.remaining = rateLimit.getRemaining();
            this.reset = DateUtils.addSeconds(new Date(), rateLimit.getSecondsUntilReset());
        }

        /**
         * For Mastodon
         */
        private RateLimitValue(mastodon4j.entity.share.Response<?> response) {
            mastodon4j.entity.share.RateLimit rateLimit = response.getRateLimit();
            this.service = ServiceType.Mastodon;

            this.limit = rateLimit.getLimit();
            this.remaining = rateLimit.getRemaining();
            this.reset = rateLimit.getReset();
        }

        /**
         * リクエスト可能かどうか？
         * Is remaining api request count?
         */
        private boolean isRemaining() {
            return (remaining > 0) || reset.before(new Date());
        }
    }
}
