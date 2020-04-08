package net.socialhub.service.support;

import msinstance4j.MastodonInstances;
import msinstance4j.entity.Instances;
import net.socialhub.define.ServiceType;
import net.socialhub.define.service.mastodon.MsInstanceOrder;
import net.socialhub.define.service.mastodon.MsInstanceSort;
import net.socialhub.model.service.Instance;
import net.socialhub.model.service.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.socialhub.utils.HandlingUtil.ignore;

/**
 * Wrapper Client for (Mastodon) Instances
 * https://instances.social/
 */
public class MsInstancesService {

    private MastodonInstances action;

    /**
     * Remove adult instances from result
     */
    private boolean isRemoveAdult = true;

    public MsInstancesService(String accessToken) {
        this.action = new MastodonInstances(accessToken);
    }

    /**
     * インスタンスリスト取得
     * Get Instances List
     */
    public List<Instance> listInstances(int count, MsInstanceSort sort, MsInstanceOrder order) {

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
    public List<Instance> searchInstances(int count, String query) {

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
        Service service = new Service(ServiceType.Mastodon, null);
        Instance model = new Instance(service);

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

        model.setUsersCount(ignore(() -> Long.valueOf(instance.getUsers())));
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
