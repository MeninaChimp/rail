## 动机

轻量级RPC框架，网络通信封装，作为基础中间件多节点通信组件，定制化开发，提取了Dubbo核心骨架.


## 特性

```
1.服务暴露
2.代理注入
3.过滤链包装
4.长连接.
5.共享连接和独占连接
6.连接关闭检测和重连.
7.延迟连接
8.请求分发
9.请求响应编解码
10.客户端响应和异常重建
11.全异步
12.跨语言
```

## RoadMap

```
1.数据校验
2.数据压缩
3.多序列化方式支持
4.多请求参数支持
5.异步上下文传递
6.SPI
7.优雅停机
8.One way
```

## 使用方式

> 引入依赖

```
 <dependency>
	<groupId>org.menina</groupId>
	<artifactId>rail</artifactId>
	<version>{lastest-version}</version>
</dependency>
```
> 示例

启动单元测试MockServer，执行Client，依次返回异常响应和正常响应.

## 整体流程

对目标方式通过Reference类创建接口代理，代理触发触发后会依次进入代理回调，Filter链逻辑，RpcInvoker，获取到目的节点的HeaderExchangeClient，由Client负责构造请求对象，发送请求，请求对象通过ChannelHandler处理IO事件，IO线程与Rpc线程池在AllChannelHandler做隔离，这一步完成请求分发，在HeaderExchangeHandler获取服务实例反射执行请求方法，处理返回值构造响应体返回客户端。

## 设计点

* 长连接：HeaderExchangeClient职责，负载心跳发起和重连，心跳3s触发一次，默认心跳超时时间为15s，服务端通过IdleStateHandler触发超时事件，由IdleChannelHandler捕获后关闭连接，连接关闭后，由HeaderExchangeClient负责定时检测连接状态发起重连.

* 通信协议：Header+Body的结构定义，使用固定的4字节标识Header的Length，body的Length封装在Header体中，听过固定4字节Header长度解决Tcp粘包和拆包问题.

* ChannelHandler包装器: 定义IO事件处理的Pipeline，涉及心跳，请求分发，异常处理，服务发射调用.

* 注解扫描：支持以注解方式标志Filter实例，服务实例，未解决服务状态和服务实例的单例，服务实例需要调用export方法注入，默认扫描路径为org.menina.

* Filter责任链：所有Filter通过责任链模式嵌入到代理对象，代理对象触发后依次执行代理回调，Filter实例，目前支持流控，Timteout，异常Filter.

* 全异步：IO异步+CompletableFuture，IO层面异步通过RequestId衔接请求和响应，请求发出后构造CompletableFuture返回调用方，调用方处理回调逻辑或者调用GET方法模拟同步调用，对应响应返回后，通过RequestId获得等待完成的CompletableFuture调用complete方法完成响应.

* 跨语言：RPC请求体，响应体基于Protobuf，提供跨语言支持.

## RPC实例初始化顺序

* 客户端：创建代理 -> 封装Filter链 -> RpcInvoker -> HeaderExchangeClient

* 服务端：扫描服务 -> 确认服务实例已注入 -> 构造ServerInfo -> 构造Exporter -> 构造服务实例反射逻辑 -> 封装filter链 -> 缓存ServiceKey-ServiceInfo映射
