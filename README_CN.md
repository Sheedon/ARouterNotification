# ARouterNotification
```tex
一个以 ARouter 为基础的通知路由框架。
```
[English](https://github.com/Sheedon/ARouterNotification/blob/master/README.md)

​		我们在处理消息通知时，通常在消息模块中建立一个「长连接」，或者使用第三方框架，来监听并且接受服务器下发的「广播 / 通知」。若是在消息模块中，统一执行 **数据核实** 、 **通知分发** 的全部逻辑，这必然导致代码耦合度提高，不利于后续维护；如果采用由消息消费的「目标模块」处理，**手动** 将处理的 **策略** 添加到「消息模块」中，同样可能产生**遗漏**、版本更新后数据匹配有误等情况。

​		当前库就是为了降低耦合，减少人为因素造成的安全风险而开发。

![通知路由](https://github.com/Sheedon/ARouterNotification/blob/master/image/%E9%80%9A%E7%9F%A5%E8%A1%8C%E4%B8%BA%E6%B5%81%E7%A8%8B.png?raw=true)

## 一、使用该库的好处 

- 将业务模块的通知路由策略，自动加载到分发器中。
- 对于源代码入侵性几乎为零，无需让 `Activity` 继承相关类，无需实现接口，只是采用 **注解** 将通知路由适配器 绑定到 目标类上。
- 执行调度的装饰类，在编译时生成，不增加开发者额外代码编写。
- 可重载方法，自定义实现，也可按默认实现，让开发者有更高的选择自由度。



## 二、使用方式

#### 1. 将 JitPack 存储库添加到您的构建文件中

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### 2. 添加核心依赖

```groovy
dependencies {
  	// 替换成最新版本, 需要注意的是api
    implementation 'com.github.Sheedon.ARouterNotification:an-api:x.x.x'
  	// 要与compiler匹配使用，均使用最新版可以保证兼容
  	annotationProcessor 'com.github.Sheedon.ARouterNotification:an-compiler:x.x.x'
}
```

#### 3. 初始化SKD

```java
if (isDebug()) {           // 这必须写在init之前，否则这些配置在init过程中将无效
		NotificationRouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有																			//	安全风险)
}
NotificationRouter.init(mApplication); // 尽可能早，推荐在Application中初始化
```

#### 4. 添加注解

```java
// 因该库时基于ARouter，所以需要在 ARouter 基础上添加 @BindRouter 注解
// @BindRouter 注解的目的是用于绑定「通知路由适配器类」
@Route(path = "/XX/XXActivity")
@BindRouter(routerClass = TestBindRouter.class)
public class YourActivity extends Activity {
   ...
}
```

#### 5. 添加通知路由类

```java
// 通知路由的类添加注解（必填）
// spareRoute:备用路由地址（可空），用于无法满足目标路由后的备选页面
// notificationType：通知类型，由具体业务决定，至少存在一个
// Model：实际消息model
@RouteStrategy(spareRoute = "/xx/xxx", notificationType = {"111", "131"})
public class TestBindRouter extends BindRouterCard<Model> {

  	// 绑定参数，与目标页面@Autowired所填的内容一致
  	// 该方法决定传递给目标@Autowired的数据
    @BindParameter(name = "id")
    public long getId() {
        return 666L;
    }

    @BindParameter(name = "name")
    public String getName() {
        return "aaa";
    }

  	// 该方法非必需实现
  	// 若未实现该方法，或者返回结果为false，则默认调用自动匹配传递数据内容的调度 ARouter.getInstance().build()...
  	// 编译时实现的该方法调度职责，暂时只支持8种基础数据类型。
  	// 若存在8种基础数据类型之外的格式，请实现该方法，并且返回true
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

#### 6. 通知类执行调度

```java
// 通过通知类型，获取「通知触发动作」的实现
ITrigger<Model> trigger = NotificationRouter.findTrigger(notificationType);
// 附加服务器下发数据
trigger.attachData("服务器拿到的数据Model");
// 执行跳转动作
trigger.startActivity();
```



## 三、后续补充内容

- [ ] 支持Activity传递所支持的所有类型
- [ ] 通过URL跳转
- [ ] 日志功能补充
- [ ] 待定。。
