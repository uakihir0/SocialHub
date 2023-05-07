package net.socialhub.service.misskey.support;

import misskey4j.MisskeyFactory;
import misskey4j.search.SearchInstances;
import net.socialhub.core.define.ServiceType;
import net.socialhub.core.model.Instance;
import net.socialhub.core.model.Service;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MiInstancesService {

    private SearchInstances search;

    public MiInstancesService() {
        search = MisskeyFactory.getSearchInstances();
    }

    /**
     * インスタンスリスト取得
     * Get Instances List
     */
    public List<Instance> listInstances() {

        return search.getMisskeyInstanceList()
                .getInstances().stream()
                .filter(e -> (e.getMeta() != null))
                .filter(e -> (e.getStats() != null))
                .filter(e -> (e.getStats().getOriginalUsersCount() != null))
                .filter(e -> (e.getStats().getOriginalUsersCount() > 10))
                .map(this::mappingInstance)

                // インスタンスのユーザーが多い順序で返却
                .sorted(Comparator.comparing(Instance::getUsersCount).reversed())
                .collect(toList());
    }

    /**
     * インスタンスマッピング
     * Instance mapping
     */
    private Instance mappingInstance(misskey4j.entity.search.JoinInstance instance) {
        Service service = new Service(ServiceType.Misskey, null);
        Instance model = new Instance(service);

        model.setHost(instance.getUrl());
        model.setName(instance.getUrl());
        model.setDescription(instance.getDescription());
        model.setIconImageUrl(instance.getMeta().getBannerUrl());

        model.setStatusesCount(instance.getStats().getOriginalNotesCount());
        model.setUsersCount(instance.getStats().getOriginalUsersCount());
        model.setConnectionCount(instance.getStats().getInstances());

        return model;
    }


}
