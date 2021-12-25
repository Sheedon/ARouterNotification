# ARouterNotification
```tex
A notification routing framework based on ARouter.
```
[中文文档](https://github.com/Sheedon/ARouterNotification/blob/master/README_CN.md)

​		When we handle message notifications, we usually establish a "long link" in the message module, or use a third-party framework to listen for and accept "broadcast/notification" from the server. 

​		If in the message module, the unified implementation of **data verification**, **notice distribution** all logic, this will inevitably lead to the increase of code coupling degree, is not conducive to the subsequent maintenance;If the "target module" is processed by message consumption, **manually** adds the **strategy ** to the "message module", it may also result in **missing**, data mismatch after version update, etc.

​		The current library was developed to reduce coupling and reduce security risks caused by human factors.

![通知路由](https://github.com/Sheedon/ARouterNotification/blob/master/image/%E9%80%9A%E7%9F%A5%E8%A1%8C%E4%B8%BA%E6%B5%81%E7%A8%8B.png?raw=true)

## One, Advantages of using this library

- Automatically load the notification routing policy of the business module into the distributor.
- There is almost zero invasivity to the source code, there is no need for the 'Activity' to inherit the related class, no need to implement the interface, just use **annotations** to bind the notification routing adapter to the target class.
- Decorator classes that perform scheduling, generated at compile time, without additional developer code.
- Methods can be overridden, custom implementation, or default implementation, giving developers greater freedom of choice.



## Two, Use mode

#### Step 1: Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2: Add core dependencies

```groovy
dependencies {
  	// Instead of the latest version, note the API
    implementation 'com.github.Sheedon.ARouterNotification:an-api:x.x.x'
  	// To be compatible with compiler, use the latest version to ensure compatibility
  	annotationProcessor 'com.github.Sheedon.ARouterNotification:an-compiler:x.x.x'
}
```

#### Step 3: Initialize SKD

```java
if (isDebug()) {           // This must be written before init, otherwise these configurations will not be valid during init
		NotificationRouter.openDebug();   // Enable debug mode (must be enabled if running in InstantRun mode! The online version needs to be closed, otherwise there is a security risk).
}
NotificationRouter.init(mApplication); // Initialization in Application is recommended as early as possible
```

#### Step 4. Add annotations

```java
// Since the library is based on ARouter, you need to add the @bindrOuter annotation on top of ARouter
// @BindRouter The purpose of the annotations is to bind the "Notification routing Adapter class"
@Route(path = "/XX/XXActivity")
@BindRouter(routerClass = TestBindRouter.class)
public class YourActivity extends Activity {
   ...
}
```

#### Step 5. Add a notification routing class

```java
// Add annotations to classes notifying routing (required)
// spareRoute:Alternate route address (can be empty), used for alternate pages after the destination route cannot be satisfied
// notificationType：The notification type is determined by the service. There must be at least one notification type
// Model：real message model
@RouteStrategy(spareRoute = "/xx/xxx", notificationType = {"111", "131"})
public class TestBindRouter extends BindRouterCard<Model> {

  	// Bind parameters, as specified on the target page @Autowired
  	// This method determines the data to pass to the target @Autowired
    @BindParameter(name = "id")
    public long getId() {
        return 666L;
    }

    @BindParameter(name = "name")
    public String getName() {
        return "aaa";
    }

  	// This method is not required to be implemented
  	// If this method is not implemented, or the result is false, the schedule that automatically matches the content of the passed data is invoked by default ARouter.getInstance().build()...
  	// The method's scheduling responsibilities, implemented at compile time, currently support only 8 basic data types.
  	// If there are formats other than the 8 underlying data types, implement this method and return true
    @Override
    protected boolean startActivity(String targetRoute, String spareRoute) {
        boolean nextBoolean = new Random().nextBoolean();
        if (nextBoolean) {
            ARouter.getInstance().build(targetRoute)
                    .withLong("id", getId())
                    .withString("name", getName())
                    .navigation();
        } else {
            ARouter.getInstance().build(spareRoute).navigation();
        }

        return true;
    }

}
```

#### Step 6. The notification class performs scheduling

```java
// Gets the implementation of the "notification trigger action" by notification type
ITrigger<Model> trigger = NotificationRouter.findTrigger(notificationType);
// Attach data delivered by the server
trigger.attachData("服务器拿到的数据Model");
// startActivity
trigger.startActivity();
```



## Three,Follow-up content

- [ ] Supports Activity delivery of all supported types
- [ ] Redirect by URL
- [ ] Log Function Supplement
- [ ] To be determined.
