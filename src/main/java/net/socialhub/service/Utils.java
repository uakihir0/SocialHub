package net.socialhub.service;

import net.socialhub.define.ServiceType;
import net.socialhub.service.mastodon.MastodonUtil;
import net.socialhub.service.misskey.MisskeyUtil;
import net.socialhub.service.slack.SlackUtil;
import net.socialhub.service.twitter.TwitterUtil;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private Map<ServiceType, ServiceUtils> utils = new HashMap<>();

    public Utils() {
        utils.put(ServiceType.Twitter, new TwitterUtil());
        utils.put(ServiceType.Mastodon, new MastodonUtil());
        utils.put(ServiceType.Misskey, new MisskeyUtil());
        utils.put(ServiceType.Slack, new SlackUtil());
    }

    /**
     * Get Comment Length Limit Level
     * Comment 投稿時の文字数制限を取得
     * Valid: 0.0 <= length <= 1.0
     * invalid: 1.0 < length
     */
    public float getCommentLengthLevel(String text, ServiceType... services) {
        if (text == null || text.isEmpty()) {
            return 0.0f;
        }

        float maxLevel = 0;
        for (ServiceType service : services) {
            if (utils.containsKey(service)) {
                float level = utils.get(service).getCommentLengthLevel(text);
                if (maxLevel < level) {
                    maxLevel = level;
                }
            }
        }

        return maxLevel;
    }

    public interface ServiceUtils {

        /**
         * Get Comment Length Limit Level
         * テキストの文字数限界レベルの取得
         */
        float getCommentLengthLevel(String text);
    }
}
