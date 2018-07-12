package net.socialhub.model.service;

import net.socialhub.define.ActionEnum;
import net.socialhub.define.ServiceEnum;
import org.apache.commons.lang3.time.DateUtils;
import twitter4j.RateLimitStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SNS レートリミット
 * SNS RateLimit
 */
public class RateLimit {

    private Map<ActionEnum, SHSvRateLimitValue> dictionary = new HashMap<>();

    /**
     * レートリミット情報を格納
     * Set rate limit info
     * (For Twitter)
     */
    public void addInfo(ActionEnum action, twitter4j.TwitterResponse response) {
        SHSvRateLimitValue value = new SHSvRateLimitValue(response);
        dictionary.put(action, value);
    }


    /**
     * リクエスト可能かどうか？
     * Is remaining api request count?
     */
    public boolean isRemaining(ActionEnum action) {
        return dictionary.containsKey(action) && //
                dictionary.get(action).isRemaining();
    }

    private static class SHSvRateLimitValue {

        private ServiceEnum service;

        private int limit;
        private int remaining;
        private Date reset;

        /**
         * For Twitter
         */
        private SHSvRateLimitValue(twitter4j.TwitterResponse response) {
            RateLimitStatus rateLimit = response.getRateLimitStatus();
            this.service = ServiceEnum.Twitter;

            this.limit = rateLimit.getLimit();
            this.remaining = rateLimit.getRemaining();
            this.reset = DateUtils.addSeconds(new Date(), rateLimit.getSecondsUntilReset());
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
