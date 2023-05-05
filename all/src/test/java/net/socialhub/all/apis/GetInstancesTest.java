package net.socialhub.all.apis;

import net.socialhub.all.SocialHub;
import net.socialhub.all.TestProperty;
import net.socialhub.mastodon.define.MsInstanceOrder;
import net.socialhub.mastodon.define.MsInstanceSort;
import net.socialhub.core.model.service.Instance;
import net.socialhub.misskey.support.MiInstancesService;
import net.socialhub.mastodon.support.MsInstancesService;
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
