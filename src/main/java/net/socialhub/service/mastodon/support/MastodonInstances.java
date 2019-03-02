package net.socialhub.service.mastodon.support;

import msinstance4j.entity.Instances;
import net.socialhub.define.service.MsInstanceOrderEnum;
import net.socialhub.define.service.MsInstanceSortEnum;
import net.socialhub.model.service.Instance;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.socialhub.utils.OptionalUtil.ignore;

/**
 * Wrapper Client for Mastodon Instances
 * https://instances.social/
 */
public class MastodonInstances {

    private msinstance4j.MastodonInstances action;

    /** Remove adult instances from result */
    private boolean isRemoveAdult = true;

    public MastodonInstances(String accessToken) {
        this.action = new msinstance4j.MastodonInstances(accessToken);
    }

    /**
     * インスタンスリスト取得
     * Get Instances List
     */
    public List<Instance> listInstances(Integer count, MsInstanceSortEnum sort, MsInstanceOrderEnum order) {

        Instances instances = action.instances().listInstances(
                count, false, false, false, null,
                (sort != null) ? sort.getCode() : null,
                (order != null) ? order.getCode() : null);

        return Arrays.stream(instances.getInstances())
                .map(this::mappingInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * インスタンス検索
     * Search Instances
     */
    public List<Instance> searchInstances(Integer count, String query) {

        Instances instances = action.instances().searchInstances(count, query);

        return Arrays.stream(instances.getInstances())
                .map(this::mappingInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Instance model
     */
    private Instance mappingInstance(msinstance4j.entity.Instance instance) {
        Instance model = new Instance();

        model.setName(instance.getName());
        model.setHost(instance.getName());
        model.setIconImageUrl(instance.getThumbnail());

        // 詳細情報は存在しない場合がある
        if (instance.getInfo() != null) {
            model.setDescription(instance.getInfo().getFullDescription());

            // アダルトサーバーは除外
            if (isRemoveAdult) {
                for (String category : instance.getInfo().getCategories()) {
                    if ((category != null) && category.equals("adult")) {
                        return null;
                    }
                }
            }
        }

        model.setUserCount(ignore(() -> Long.valueOf(instance.getUsers())));
        model.setStatusesCount(ignore(() -> Long.valueOf(instance.getStatuses())));
        model.setConnectionCount(ignore(() -> Long.valueOf(instance.getConnections())));

        return model;
    }

    //region // Getter&Setter
    public boolean getRemoveAdult() {
        return isRemoveAdult;
    }

    public void setRemoveAdult(boolean removeAdult) {
        isRemoveAdult = removeAdult;
    }
    //endregion
}
