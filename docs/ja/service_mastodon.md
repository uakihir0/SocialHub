# Mastodon on SocialHub

SocialHub ライブラリを用いて Mastodon API へアクセスするには以下の順序でリクエストしてください。

## インスタンス検索

Mastodon は分散型のソーシャルネットワークサービスであるため、多くのインスタンスが存在します。別々のインスタンスにあるアカウント同士リモートフォローが可能であり、基本的にどのインスタンスでも困ることは無いように設計されていますが、場合によっては特定のインスタンスの方が興味の分野に合致し、良い体験が得られる場合があります。まずは [Mastodon Instances] などから、インスタンスを探してみてください。また、SocialHub では [Mastodon Instances] の API 以下のように呼び出すこともできます。


```java
MastodonInstances client = MastodonSupport.getMastodonInstances(
        TestProperty.MastodonInstancesProperty.AccessToken);

// ユーザーが多い順にインスタンスリストを取得
List<Instance> instances = client.listInstances(
        200, MsInstanceSortEnum.USERS, MsInstanceOrderEnum.DESC);
```


## 認可

特定のホスティングされた Mastodon インスタンスへの認可リクエストは以下のように行います。



### アプリケーション登録

最初に認証アプリケーションを登録してクライアント情報 ClientId 及び ClientSecret を入手します。この手順はすでに登録してある場合は繰り返し行う必要はありません。


```java
MastodonAuth auth = SocialHub.getMastodonAuth(MastodonInstance.MSTDN_JP);

auth.requestClientApplication("SocialHub", null,
        MastodonAuth.REDIRECT_NONE,
        MastodonScope.FULL_ACCESS);

System.out.println(auth.getClientId());
System.out.println(auth.getClientSecret());
```

  [Mastodon Instances]: https://instances.social/