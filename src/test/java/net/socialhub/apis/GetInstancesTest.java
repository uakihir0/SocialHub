package net.socialhub.apis;

import net.socialhub.TestProperty;
import net.socialhub.define.service.MsInstanceOrderEnum;
import net.socialhub.define.service.MsInstanceSortEnum;
import net.socialhub.model.service.Instance;
import net.socialhub.service.mastodon.MastodonSupport;
import net.socialhub.service.mastodon.support.MastodonInstances;
import org.junit.Test;

import java.util.List;

public class GetInstancesTest {

    @Test
    public void testMastodonListInstances() {

        MastodonInstances client = MastodonSupport.getMastodonInstances( //
                TestProperty.MastodonInstancesProperty.AccessToken);

        // Get Most Users Instances
        List<Instance> instances = client.listInstances( //
                200, MsInstanceSortEnum.USERS, MsInstanceOrderEnum.DESC);

        for (Instance instance : instances) {
            System.out.println(instance.getName());
        }
    }

    @Test
    public void testMastodonSearchInstances() {

        MastodonInstances client = MastodonSupport.getMastodonInstances( //
                TestProperty.MastodonInstancesProperty.AccessToken);

        List<Instance> instances = client.searchInstances(10, "Anime");

        for (Instance instance : instances) {
            System.out.println(instance.getName());
        }
    }
}
