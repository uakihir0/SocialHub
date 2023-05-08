package net.socialhub.core.model;

import net.socialhub.core.define.ServiceType;
import net.socialhub.core.define.action.ActionType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SNS レートリミット
 * SNS RateLimit
 */
public class RateLimit implements Serializable {

    private final Map<ActionType, RateLimitValue> dictionary = new HashMap<>();

    /**
     * レートリミット情報を格納
     * Set rate limit info
     */
    public void addInfo(
            ActionType action,
            ServiceType service,
            int limit,
            int remaining,
            Date reset
    ) {
        RateLimitValue value = new RateLimitValue(
                service, limit, remaining, reset);
        addInfo(action, value);
    }

    /**
     * レートリミット情報を格納
     * Set rate limit info
     */
    public void addInfo(
            ActionType action,
            RateLimitValue value
    ) {
        if (value != null) {
            dictionary.put(action, value);
        }
    }

    /**
     * リクエスト可能かどうか？
     * Is remaining api request count?
     */
    public boolean isRemaining(ActionType action) {
        return dictionary.containsKey(action) && //
                dictionary.get(action).isRemaining();
    }

    public static class RateLimitValue {

        private final ServiceType service;

        private final int limit;
        private final int remaining;
        private final Date reset;

        public RateLimitValue(
                ServiceType service,
                int limit,
                int remaining,
                Date reset
        ) {
            this.service = service;
            this.limit = limit;
            this.remaining = remaining;
            this.reset = reset;
        }

        /**
         * リクエスト可能かどうか？
         * Is remaining api request count?
         */
        public boolean isRemaining() {
            return (remaining > 0) || reset.before(new Date());
        }

        // region
        public ServiceType getService() {
            return service;
        }

        public int getLimit() {
            return limit;
        }

        public int getRemaining() {
            return remaining;
        }

        public Date getReset() {
            return reset;
        }
        // endregion
    }
}
