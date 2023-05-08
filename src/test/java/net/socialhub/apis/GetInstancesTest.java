package net.socialhub.apis;

import net.socialhub.core.SocialHub;
import net.socialhub.TestProperty;
import net.socialhub.service.mastodon.define.MsInstanceOrder;
import net.socialhub.service.mastodon.define.MsInstanceSort;
import net.socialhub.core.model.Instance;
import net.socialhub.service.misskey.support.MiInstancesService;
import net.socialhub.service.mastodon.support.MsInstancesService;
import org.junit.Test;

import java.util.List;

public class GetInstancesTest {

    @Test
    public void testMastodonListInstances() {

        MsInstancesService client = SocialHub.getSupportServices().getMastodonInstances( //
                TestProperty.MastodonInstancesProperty.AccessToken);

        // Get Most Users Instances
        List<Instance> instances = client.listInstances( //
                200, MsInstanceSort.USERS, MsInstanceOrder.DESC);

        for (Instance instance : instances) {
            System.out.println(instance.getName());
        }
    }

    @Test
    public void testMastodonSearchInstances() {

        MsInstancesService client = SocialHub.getSupportServices().getMastodonInstances( //
                TestProperty.MastodonInstancesProperty.AccessToken);

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
