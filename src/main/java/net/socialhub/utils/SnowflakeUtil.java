package net.socialhub.utils;

import java.math.BigInteger;
import java.util.Date;

/**
 * Snowflake IDs
 */
public class SnowflakeUtil {

    private Long originTime;

    /**
     * FOR TWITTER
     */
    public static SnowflakeUtil ofTwitter() {
        return new SnowflakeUtil(1288834974657L);
    }

    /**
     * Snowflake need OriginTime
     */
    public SnowflakeUtil(Long originTime) {
        this.originTime = originTime;
    }

    /**
     * Get DateTime from SnowflakeID
     */
    public Date getDateTimeFromID(Long id) {
        String bit = toBinaryString(id, 64).substring(0, 42);
        Long diff = new BigInteger(bit, 2).longValue();
        return new Date(diff + originTime);
    }

    /**
     * Add MilliSeconds to Snowflake ID
     * (return not existing id (use for paging))
     */
    public Long addMilliSecondsToID(Long id, Long milliseconds) {
        String original = toBinaryString(id, 64);

        String bit = original.substring(0, 42);
        Long diff = (new BigInteger(bit, 2).longValue() + milliseconds);
        String to = toBinaryString(diff, 42);

        String appended = to + original.substring(42, 64);
        return new BigInteger(appended, 2).longValue();
    }

    /**
     * Add Seconds to Snowflake ID
     * (return not existing id (use for paging))
     */
    public Long addSecondsToID(Long id, Long seconds) {
        return addMilliSecondsToID(id, seconds * 1000L);
    }

    /**
     * Add Minutes to Snowflake ID
     * (return not existing id (use for paging))
     */
    public Long addMinutesToID(Long id, Long minutes) {
        return addSecondsToID(id, minutes * 60L);
    }

    /**
     * Add Hours to Snowflake ID
     * (return not existing id (use for paging))
     */
    public Long addHoursToID(Long id, Long hours) {
        return addMinutesToID(id, hours * 60L);
    }

    /**
     * Add Days to Snowflake ID
     * (return not existing id (use for paging))
     */
    public Long addDaysToID(Long id, Long days) {
        return addHoursToID(id, days * 24L);
    }

    /**
     * バイナリ表現文字列を作成
     */
    private String toBinaryString(Long id, Integer length) {
        String binary = Long.toBinaryString(id);
        StringBuilder builder = new StringBuilder();
        for (int i = binary.length(); i < length; i++)
            builder.append("0");
        builder.append(binary);
        return builder.toString();
    }

}
