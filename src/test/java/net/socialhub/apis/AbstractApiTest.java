package net.socialhub.apis;

import com.google.gson.Gson;
import net.socialhub.TestProperty;
import net.socialhub.TestProperty.ServiceProperty;
import net.socialhub.core.SocialHub;
import net.socialhub.core.model.Account;
import net.socialhub.j2objc.security.HmacProvider;
import net.socialhub.service.mastodon.action.MastodonAuth;
import net.socialhub.service.misskey.action.MisskeyAuth;
import net.socialhub.service.slack.action.SlackAuth;
import net.socialhub.service.tumblr.action.TumblrAuth;
import net.socialhub.service.twitter.action.TwitterAuth;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.security.Security;

public class AbstractApiTest {

    protected ServiceProperty twitterProperty;
    protected ServiceProperty mastodonProperty;
    protected ServiceProperty misskeyProperty;
    protected ServiceProperty tumblrProperty;
    protected ServiceProperty slackProperty;
    protected ServiceProperty pleromaProperty;
    protected ServiceProperty pixelfedProperty;
    protected ServiceProperty mastodonInstancesProperty;

    @Before
    public void before() {
        Security.addProvider(new HmacProvider());

        try {
            String json = readFile("./secrets.json");
            TestProperty props = new Gson().fromJson(json, TestProperty.class);

            twitterProperty = getServiceProperty(props, "Twitter");
            mastodonProperty = getServiceProperty(props, "Mastodon");
            misskeyProperty = getServiceProperty(props, "Misskey");
            tumblrProperty = getServiceProperty(props, "Tumblr");
            slackProperty = getServiceProperty(props, "Slack");
            pleromaProperty = getServiceProperty(props, "Pleroma");
            pixelfedProperty = getServiceProperty(props, "Pixelfed");
            mastodonInstancesProperty = getServiceProperty(props, "MastodonInstances");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServiceProperty getServiceProperty(
            TestProperty props,
            String name
    ) {
        return props.getServices().stream()
                .filter(s -> s.getName().equals(name))
                .findFirst().orElse(null);
    }

    /**
     * File to ImageBytes
     */
    public static byte[] convertFile(InputStream stream) {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = stream.read(buffer)) != -1) {
                bout.write(buffer, 0, len);
            }
            return bout.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read File
     */
    private String readFile(String fileName) {
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder b = new StringBuilder();

            String line;
            String ls = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                b.append(line);
                b.append(ls);
            }

            b.deleteCharAt(b.length() - 1);
            br.close();

            return b.toString();

        } catch (Exception e) {
            return null;
        }
    }


    public Account getTwitterAccount() {
        TwitterAuth auth = SocialHub.getTwitterAuth(
                twitterProperty.getClientId(),
                twitterProperty.getClientSecret());
        return auth.getAccountWithAccessToken(
                twitterProperty.getAccessToken(),
                twitterProperty.getAccessSecret());
    }

    public Account getMastodonAccount() {
        MastodonAuth auth = SocialHub.getMastodonAuth(
                mastodonProperty.getHost());
        return auth.getAccountWithAccessToken(
                mastodonProperty.getAccessToken(),
                null,
                null
        );
    }

    public Account getPleromaAccount() {
        MastodonAuth auth = SocialHub.getPleromaAuth(
                pleromaProperty.getHost());
        return auth.getAccountWithAccessToken(
                pleromaProperty.getAccessToken(),
                null,
                null
        );
    }

    public Account getPixelFedAccount() {
        MastodonAuth auth = SocialHub.getPixelFedAuth(
                pixelfedProperty.getHost());
        return auth.getAccountWithAccessToken(
                pixelfedProperty.getAccessToken(),
                null,
                null
        );
    }

    public Account getMisskeyAccount() {
        MisskeyAuth auth = SocialHub.getMisskeyAuth(
                misskeyProperty.getHost());
        auth.setClientInfo(
                misskeyProperty.getClientId(),
                misskeyProperty.getClientSecret());
        return auth.getAccountWithAccessToken(
                misskeyProperty.getAccessToken());
    }

    public Account getTumblrAccount() {
        TumblrAuth auth = SocialHub.getTumblrAuth(
                tumblrProperty.getClientId(),
                tumblrProperty.getClientSecret());
        return auth.getAccountWithAccessToken(
                tumblrProperty.getAccessToken(),
                tumblrProperty.getAccessSecret());
    }

    public Account getSlackAccount() {
        SlackAuth auth = SocialHub.getSlackAuth(
                slackProperty.getClientId(),
                slackProperty.getClientSecret());
        return auth.getAccountWithToken(
                slackProperty.getAccessToken());
    }
}
