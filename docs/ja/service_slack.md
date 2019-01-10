# Slack on SocialHub

SocialHub ライブラリを用いて Slack API へアクセスするには以下の順序でリクエストしてください。

## 認可

初めに、アプリケーション登録を [Slack API](https://api.slack.com/) から行い、ClientId 及び ClientSecret を入手します。この際に登録したリダイレクト URL も認可の過程の中で使用し、また登録した Scope についてのみ認可の際にアクセス要求が可能です。

次に認可を行う URL を生成します。SocialHub を利用するユーザーはそこで得られた URL にアクセスし、特定の workspace のアカウントに対して SocialHub を使用可能にする認可を行います。ここでの RedirectUri 及び Scope は登録したアプリケーションの内容に準じたものを使用してください。


```java
SlackAuth auth = SocialHub.getSlackAuth(CLIENT_ID, CLIENT_SECRET);

// Scope の例
List<SlackScope> scopes = new ArrayList<>();
scopes.add(new SlackScope().chat().write().user());

String scope = scopes.stream()
        .map(SlackScope::getCode)
        .collect(Collectors.joining(" "));

System.out.println(auth.getAuthorizationURL(REDIRECT_URI, scope));
```

ここで生成された URL にアクセスし、ユーザーが許可した場合、