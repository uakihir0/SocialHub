package net.socialhub.apis;

import net.socialhub.core.SocialHub;
import net.socialhub.core.model.Instance;
import net.socialhub.service.mastodon.define.MsInstanceOrder;
import net.socialhub.service.mastodon.define.MsInstanceSort;
import net.socialhub.service.mastodon.support.MsInstancesService;
import net.socialhub.service.misskey.support.MiInstancesService;
import org.junit.Test;

import java.util.List;

public class GetInstancesTest extends AbstractApiTest {

    @Test
    public void testMastodonListInstances() {

        MsInstancesService client = SocialHub.getSupportServices()
                .getMastodonInstances(maInstancesProperty.getAccessToken());

        // Get Most Users Instances
        List<Instance> instances = client.listInstances(
                200, MsInstanceSort.USERS, MsInstanceOrder.DESC);

        for (Instance instance : instances) {
            System.out.println(instance.getName());
        }
    }

    @Test
    public void testMastodonSearchInstances() {

        MsInstancesService client = SocialHub.getSupportServices()
                .getMastodonInstances(maInstancesProperty.getAccessToken());

        List<Instance> instances = client.searchInstances(10, "Anime");

        for (Instance instance : instances) {
            System.out.println(instance.getName());
        }
    }

    @Test
    public void testMisskeyListInstances() {

        MiInstancesService client = SocialHub.getSupportServices().getMisskeyInstances();

        // Get Most Users Instances
        List<Instance> instances = client.listInstances();

        for (Instance instance : instances) {
            System.out.println(instance.getName());
        }
    }
}
