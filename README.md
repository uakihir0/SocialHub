# SocialHub

Cross SocialNetworkService API Library for Java and Objective-C by [J2ObjC].   
*It's WIP (work in progress) project.*

## List of SNS

* [**Twitter**](https://twitter.com/)
  * use [library](https://github.com/uakihir0/twitter4j) based on [twitter4j](https://github.com/yusuke/twitter4j)
* [**Facebook**](https://www.facebook.com/)
  * use [library](https://github.com/uakihir0/facebook4j) based on [facebook4j](https://github.com/roundrop/facebook4j)
* [**Slack**](https://slack.com/)
  * use [library](https://github.com/uakihir0/jslack) based on [jslack](https://github.com/seratch/jslack)
* [**Mastodon**](https://github.com/tootsuite/mastodon)
  * use [library](https://github.com/uakihir0/mastodon4j) based on [mastodon4j](https://github.com/hecateball/mastodon4j)


* [**Google+**]() (future work)
* [**LinkedIn**]() (future work)

* [**Discord**]() (future work)
  * [Discord API](https://discordapp.com) does not provide write messege api to channel.
* [**Instagram**]() (future work)
  * [Instagram API](https://www.instagram.com/developer/) is deplicated, and [new Graph API](https://developers.facebook.com/products/instagram/) is buissiness use.


Most of all libraries are modified (remove complex dependency and unused functions in SocialHub) for compilation to Objective-C with [J2ObjC].

## Sample
Sample of getting authorized account Information.

```java
// 1. Make Authorized Account Object

// For Twitter
SHTwitterAuth auth = SocialHub.getTwitterAuth(CONSUMER_KEY, CONSUMER_SECRET);
SHAccount account = auth.getAccountWithAccessToken(ACCESS_TOKEN, ACCESS_SECRET);

// For Mastodon
SHMastodonAuth auth = SocialHub.getMastodonAuth(HOST);
SHAccount account = auth.getAccountWithAccessToken(ACCESS_TOKEN);


// 2. Request To SNS (all sns same interfacce)
SHSvUser user = account.getAction().getUserMe();
System.out.println(user.getName());
```

if you want more samples, please watch test code.

### J2ObjC

[J2ObjC] is project aim to compile Java code to Objective-C code.

### Compile to Objective-C
SocialHub is adapted with J2ObjC compilation. So, you can use SocialHub as an Objective-C library (also use in Swift) in MacOS or iOS application development, if you want to do, do following instructions. (It's only executable on MacOS)

###### Download latest J2ObjC builds and unzip
See: https://github.com/google/j2objc/releases

###### Set J2ObjC path in ```local.properties``` file
```shell
j2objc.home=${PATH}
```

###### Build Objective-C library
build commands in ```j2objc.sh``` file. ```j2objc.gradle``` is gradle settings to make Objective-C library.

```shell
./gradlew -b j2objc.gradle clean :j2objcAssembleDebug :j2objcAssembleRelease
sh ./tool/buildscript.sh
```

###### Add dependency in your project
After J2objC comple, ```cocoapod.spec``` will be created in ```build/j2objcOutputs``` folder, so you write SocialHub dependency in your project's ```Podfile``` like

```
def j2objc_socialhub
    pod 'j2objc-socialhub-debug', :configuration => ['Debug'], :path => '../shared/build/j2objcOutputs'
    pod 'j2objc-socialhub-release', :configuration => ['Release'], :path => '../shared/build/j2objcOutputs'
end

<SOME COMPLEX RUBY>
    ...
    NOTE: this line must be added manually for the relevant targets:
    j2objc_socialhub
    ...
end
```

###### Use SocialHub in Swift
Make bridging header, and write as following.

```
#include "SocialHubHeader.h"
```

### License
[@U_Akihir0](https://twitter.com/U_AKihir0)  
MIT


  [J2ObjC]: https://developers.google.com/j2objc/
