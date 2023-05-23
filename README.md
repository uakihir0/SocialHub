# SocialHub [![](https://jitpack.io/v/uakihir0/socialhub.svg)](https://jitpack.io/#uakihir0/socialhub) [![Build Status](https://github.com/uakihir0/SocialHub/workflows/J2ObjC%20Build/badge.svg)](https://travis-ci.com/uakihir0/SocialHub)

SocialHub is a multi social network service's API library written in **Java**, and can be complied to **Objective-C**
by [J2ObjC]. and we can also use it with **Kotlin** and **Swift** too.

## List of Social Media (SNS)

### Available

* [**Twitter**](https://twitter.com/) ( use [library](https://github.com/uakihir0/twitter4j) based
  on [twitter4j](https://github.com/yusuke/twitter4j) )
* [**Slack**](https://slack.com/) ( use [library](https://github.com/uakihir0/jslack) based
  on [jslack](https://github.com/seratch/jslack) )
* [**Mastodon**](https://github.com/tootsuite/mastodon) ( use [library](https://github.com/uakihir0/mastodon4j) based
  on [mastodon4j](https://github.com/hecateball/mastodon4j) )
    * [**Pleroma**](https://pleroma.social/) (Mastodon API compatibility)
    * [**PixelFed**](https://pixelfed.org/) (Mastodon API compatibility)
* [**Tumblr**](https://www.tumblr.com/) ( use [library](https://github.com/uakihir0/jumblr) based
  on [jumblr](https://github.com/tumblr/jumblr) )
* [**Misskey**](https://join.misskey.page/ja/) ( use [library](https://github.com/uakihir0/misskey4j) )

Most of all libraries are modified (remove complex dependency and unused functions) for compilation to Objective-C
with [J2ObjC].

### Work in Progress

* [**Bluesky**](https://blueskyweb.xyz/) ( use [library](https://github.com/uakihir0/bsky4j) )

### Future Works

* [**Facebook**](https://www.facebook.com/) ( use [library](https://github.com/uakihir0/facebook4j) based
  on [facebook4j](https://github.com/roundrop/facebook4j) )
* [**LinkedIn**](https://www.linkedin.com/)
* [**Pinterest**](https://www.pinterest.com/)
* [**Snapchat**](https://www.snapchat.com/)
* [**Matrix**](https://matrix.org/)
* [**Microsoft Teams**](https://www.microsoft.com/ja-jp/microsoft-teams/group-chat-software)

### Difficult to Handle

* [**Discord**](https://discordapp.com/)
    * [Discord API](https://discordapp.com) does not provide the way to write message api as user to channel.
* [**Instagram**](https://www.instagram.com/)
    * [Instagram API](https://www.instagram.com/developer/) is duplicated,
      and [new Graph API](https://developers.facebook.com/products/instagram/) is business use.

## Detail Documents

[**See Detail Documents**](./docs/README.md)

## Code Sample

### Authorization

Code sample to authorize account and get account information.

```
/* JAVA */
// 1. Make Authorized Account Object

// For Twitter
TwitterAuth auth = SocialHub.getTwitterAuth(CONSUMER_KEY, CONSUMER_SECRET);
Account account = auth.getAccountWithAccessToken(ACCESS_TOKEN, ACCESS_SECRET);

// For Mastodon
MastodonAuth auth = SocialHub.getMastodonAuth(HOST);
Account account = auth.getAccountWithAccessToken(ACCESS_TOKEN);


// 2. Request To SNS (all sns same interface)
User user = account.action().getUserMe();
System.out.println(user.getName());
```

### Group Timeline

Code sample to get unified timeline comments from account group (two or more accounts).

```
/* JAVA */
// 1. Make Account Group
AccountGroup accounts = new AccountGroup();
accounts.addAccount((Account)twitterAccount);
accounts.addAccount((Account)mastodonAccount);

// 2. Get Home New Timeline
CommentGroup comments = accounts.action().getHomeTimeLine();

// 3. Get Past Timeline
CommentGroup pasts = comments.action().getPastComments();
```

if you want more samples, please see [detail documents](./docs/README.md) and test code.

## J2ObjC

[J2ObjC] is project aim to compile **Java** code to **Objective-C** code. SocialHub is adapted with J2ObjC compilation
So, you can use SocialHub as an Objective-C library (also use in Swift) in MacOS or iOS application development. Travis
CI compiles SocialHub and make [**SocialHub CocoaPods Repository**](https://dev.azure.com/SocialHub/_git/ObjCBinary).
It's an easy way to use this repository rather than compiling yourself.

### Compile to Objective-C

To compile SocialHub to Objective-C framework, do next instructions. **It's only executable on MacOS
environment** ([This file](./.github/workflows/build.yml) is script of followings.)

1. Download latest J2ObjC builds and unzip

   See: <https://github.com/google/j2objc/releases>

2. Set J2ObjC path in `local.properties` file

    ```shell
    j2objc.home=${PATH}
    ```

3. Build Objective-C libraries

   Build commands is written in `j2objc.sh` file. so execute it or do next instructions. ```j2objc.gradle``` is gradle
   settings to make Objective-C library.

    ```shell
    ./gradlew -b j2objc.gradle clean :j2objcAssemble -x test
    ```

4. Add dependency in your project

   After J2ObjC compile, `cocoapod.spec` will be created in `./build/j2objcOutputs` folder, so you write SocialHub
   dependency in your CocoaPods project's `Podfile` like

    ```
    def j2objc_socialhub
        pod 'j2objc-SocialHub-debug', :configuration => ['Debug'], :path => '../shared/build/j2objcOutputs'
        pod 'j2objc-SocialHub-release', :configuration => ['Release'], :path => '../shared/build/j2objcOutputs'
    end
    
        ...
        NOTE: this line must be added manually for the relevant targets:
        j2objc_socialhub
        ...
    ```

5. Use SocialHub in Swift

   Make bridging header, and write as following.

    ```
    #include "SocialHubHeader.h"
    ```

## Application

* iOS: [SocialHub](https://apps.apple.com/us/app/id1474451582)
    * This library is converted to Objective-C and used in Swift.

## Author

Twitter: [@uakihir0](https://twitter.com/uakihir0)  
GitHub: [uakihir0](https://github.com/uakihir0)

## License

**MIT**

[J2ObjC]: https://developers.google.com/j2objc/
