package net.socialhub.microblog.action;

import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Identify;
import net.socialhub.core.model.service.Notification;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.Stream;
import net.socialhub.core.model.service.Trend;
import net.socialhub.core.action.callback.EventCallback;

import java.util.List;

/**
 * 分散マイクロブログアクション
 * Action for DistributedBlog
 * (Mastodon, Misskey...)
 */
public interface MicroBlogAccountAction {

    // ============================================================== //
    // Others
    // ============================================================== //

    /**
     * Get Trends
     * トレンドを取得
     */
    List<Trend> getTrends(Integer limit);

    /**
     * Get Notifications
     * 通知を取得
     */
    Pageable<Notification> getNotification(Paging paging);

    /**
     * Vote on a poll
     * 投票する
     */
    void votePoll(Identify id, List<Integer> choices);

    // ============================================================== //
    // Another TimeLines
    // ============================================================== //

    /**
     * Return Local Timeline
     * サーバーのローカルタイムライン
     */
    Pageable<Comment> getLocalTimeLine(Paging paging);

    /**
     * Return Federation TimeLine
     * 連合タイムラインを返却
     */
    Pageable<Comment> getFederationTimeLine(Paging paging);

    /**
     * Set Local Timeline Stream
     * ローカルタイムラインのイベントを取得
     */
    Stream setLocalLineStream(EventCallback callback);

    /**
     * Set Federation Timeline Stream
     * 連合タイムラインのイベントを取得
     */
    Stream setFederationLineStream(EventCallback callback);
}
