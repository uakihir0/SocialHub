package net.socialhub.core;

import net.socialhub.core.define.ServiceType;
import net.socialhub.core.utils.ServiceUtil;
import net.socialhub.service.bluesky.action.BlueskyUtil;
import net.socialhub.service.mastodon.action.MastodonUtil;
import net.socialhub.service.misskey.action.MisskeyUtil;
import net.socialhub.service.slack.action.SlackUtil;
import net.socialhub.service.twitter.action.TwitterUtil;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private final Map<ServiceType, ServiceUtil> utils = new HashMap<>();

    public Utils() {
        utils.put(ServiceType.Twitter, new TwitterUtil());
        utils.put(ServiceType.Mastodon, new MastodonUtil());
        utils.put(ServiceType.Misskey, new MisskeyUtil());
        utils.put(ServiceType.Slack, new SlackUtil());
        utils.put(ServiceType.Bluesky, new BlueskyUtil());
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
}
