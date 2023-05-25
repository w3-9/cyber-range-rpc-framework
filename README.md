# Cyber-Range RPC Framework 

## Demands

Cyber-Range is a platform of Cyber Attack & Defense Training platform. During the development pahse, it is found that the interaction with OpenStack is needed. Author(me) developed an OpenStack SDK which could be used for interaction with the platform. However, the expose of the SDK is a problem, Either this could an API gateway that exposes API to other services, or it could be used as RPC framework.

Finally, We choose to implement RPC framework to get service exposed for the following reasons:

- Easy to embed into existing system. (Given the interfaces), clients may try to excute functions directly without much configuration on  HTTP Request.
- Flow managing and Retry-Mechanism could be introduced to  further extend the functionality border of the SDK.
- Quick and Efficient.
- (Another reason) This could help us to get across some new skills.

Note : **This is not the production project but is just a POC. More modifications are needed for better performance**.

## Research

RPC stands for Remote Procedure Call. Due to its name, some aspects have to be taken into account during the design phase.

- Remote : how the objects are connected and tel-communicate with each other? How the service registered and discovered?
- Procedure : what really happens under-hood? How does filter work? Is is BIO, NIO or others?
- Call : How the call is guaranteed with best performance? How will load balancing and error-retrying help to improve the performance.

## Design

The customized Cyber-Range RPC framework is designed with the following components:

- Proxy Layer : use proxy service to perform request operations
- Router Layer : use router to perform server discovery
- Filter Layer : use filter to perform server selection
- Protocol Layer : perform protocol wrap-up and de-wrap-up.

The RPC base model we are using is Netty due to the following reasons:

- NIO based : High Concurrency
- High Performance : Zero-copy (data sharing between kernel and use mode)
- Good API encapsulation.

## Dev Feature

### Server / Client

Server/Client is used to perform the setup of RPC C/S.

- Server

  ```java
          /**
           * 启动服务
           */
          Server server = new Server();
  //        server.setServerConfig(config);
          /**
           * 从Property文件中加载配置信息
           */
          server.initServerConfig();
          /**
           * 将服务注册到Zookeeper上
           */
          ServerWrapper wrapper =
                  new ServerWrapper(
                      new DataServiceImpl(),
                      server.getServerConfig()
                          .getGroup()
              );
          wrapper.setToken("token1");
          wrapper.setFlowLimit(server.serverConfig.getFlowLimit());
  
  
          server.registerService(wrapper);
          server.startApplication();
  ```

- Client

```java
        Client client = new Client();
        RpcReference rpcReference = client.initClienApplication();

        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setGroup(client.getConfig().getGroup());
        rpcReferenceWrapper.setToken("token1");
        rpcReferenceWrapper.setAsync(false);

        /**
         * 绑定代理关系？
         */
//        DataService dataService = rpcReference.get(DataService.class);
        DataService dataService = rpcReference.get(rpcReferenceWrapper);
        client.subscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();
```

### Protocol

The request is composed of following:

```java
@Data
public class RpcInvocation implements Serializable {

    private static final long serialVersionUID = 1949734262531844277L;
    /**
     * 调用方法
     */
    private String targetMethod;

    /**
     * 请求的目标服务名称
     */
    private String targetServiceName;

    /**
     * 请求目标参数
     */
    private Object[] args;

    /**
     * 主要是用于匹配请求和响应的一个关键值。
     * 当请求从客户端发出的时候，会有一个uuid用于记录发出的请求
     * 待数据返回的时候通过uuid来匹配对应的请求线程
     */
    private String uuid;

    /**
     * reponse 直接放在同一个请求里
     *
     */
    private Object response;

    private Throwable error;

    private Map<String,Object> attachments = new ConcurrentHashMap<>();

    private int remainingRetryTimes;
}
```

For messaging, we use customized protocol:

```java
@Data
public class RpcProtocol implements Serializable {

    /**
     * 对于协议来说，开头为标志（MAGIC_NUMBER）_short =》 2Bytes
     * 接下来是长度 int => 4 bytes
     */

    /**
     * Alt + Enter 以生成版本序列唯一ID。
     * 与UUID有区别
     */
    private static final long serialVersionUID = 2420724557226631489L;

    /**
     * 用于安全检测，看是不是这个协议，服务端/客户端应当统一
     */
    private short magicNumber = RpcConstants.MAGIC_NUMBER;

    /**
     * 这里就是协议类，其实例就是单词报文
     * 下面是内容长度，后续可能根据长度作出相应处理
     */
    private int contentLength;

    /**
     * 请求的服务名称，请求服务的方法名称，请求参数内容，所有正式
     */
    private byte[] content;


    /**
     * 构造函数，传入报文字符数组
     * @param content RPC请求报文数组
     */
    public RpcProtocol(byte[] content) {
        this.content = content;
        this.contentLength = this.content.length;
    }
}

```

We encode using following code:

```java
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol msg, ByteBuf out) throws Exception {
        out.writeShort(msg.getMagicNumber());
        out.writeInt(msg.getContentLength());
        out.writeBytes(msg.getContent());
        out.writeBytes(RpcConstants.DELIMTER_SIGN.getBytes());
    }
```

For decoding:

```java
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        //太小说明非正常字节流
        if (in.readableBytes() < this.BASE_LENGTH){
            return;
        }

        // 太大难以解析
        if (in.readableBytes() > this.LENGTH_LIMIT){
            log.warn("ReadableBytes too long.... skip");
            // 增大读指针，也就是将这一部分跳过去了，不读了！
            in.skipBytes(in.readableBytes());
        }

        int beginReader = in.readerIndex();
        in.markReaderIndex();

        /**
         * 如果不满足开头协议魔数，直接退出
         */
        if (in.readShort() != RpcConstants.MAGIC_NUMBER){
            ctx.close();
            return;
        }

        /**
         * 如果长度不够，说明需要重置读索引？
         * 为什么会不够呢？ todo
         */
        int length = in.readInt();
        if (in.readableBytes() < length){
            in.readerIndex(beginReader);
        }

        byte[] contents = new byte[length];
        in.readBytes(contents);

        RpcProtocol protocol = new RpcProtocol(contents);
        out.add(protocol);
    }
```



### Process

We use `RpcReference` to set up a proxy for client, and `get` method will help return an imitated target instance. (with the help of `Proxy.newProxyInstance`)

```
        RpcReference rpcReference = client.initClienApplication();

        RpcReferenceWrapper<DataService> rpcReferenceWrapper = new RpcReferenceWrapper<>();
        rpcReferenceWrapper.setAimClass(DataService.class);
        rpcReferenceWrapper.setGroup(client.getConfig().getGroup());
        rpcReferenceWrapper.setToken("token1");
        rpcReferenceWrapper.setAsync(false);

        /**
         * 绑定代理关系？
         */
//        DataService dataService = rpcReference.get(DataService.class);
        DataService dataService = rpcReference.get(rpcReferenceWrapper);
```

The `getProxy` shows that `jdkClientInvocationHandle` helps to provide handler for calling.

```java
    public <T> T getProxy(RpcReferenceWrapper rpcReferenceWrapper) throws Throwable {
        return (T) Proxy.newProxyInstance(
                rpcReferenceWrapper.getAimClass().getClassLoader(),
                new Class[]{rpcReferenceWrapper.getAimClass()},
                new JdkClientInvocationHandler(rpcReferenceWrapper)
        );
    }
```



The `invoke` function here is **very important**, it is actually the real work where client generate request and deal with result :

```Java
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        /**
         * 新建请求，将对应类信息放进去
         */
        RpcInvocation invocation = new RpcInvocation();
        invocation.setArgs(args);
        invocation.setUuid(UUID.randomUUID().toString());
        invocation.setTargetMethod(method.getName());
        invocation.setTargetServiceName(clazz.getName());
        invocation.setAttachments(wrapper.getAttachments());
        invocation.setRemainingRetryTimes(CommonClientCache.CLIENT_CONFIG.getMaxRetryTimes());

        CommonClientCache.RESP_MAP.put(invocation.getUuid(),object);
        CommonClientCache.CLIENT_SEND_QUEUE.put(invocation);

        /**
         * 只是不堵塞等待结果 不代表结果不返回
         */
        if (wrapper.isAsync()){
            log.warn("Sending async requests....");
            Thread.sleep(1000);
            return null;
        }

        long beginTime = System.currentTimeMillis();

        /**
         * 这里没有调用method.invoke，是因为是远程调用，所以我们不调用
         * 放到队列，由另一个线程负责发送
         * 但是，这个也是单线程的，main，需要等待
         * 在某个函数中需要顺序调用A，B，C，D四个RPC接口，
         * 如果有多个线程来执行发送操作（只是负责发送部分逻辑），
         * 那么如何确保A，B，C，D的调用顺序是有序的呢？
         * 如果是单个线程专门负责发送调用请求，这种问题就可以避免掉了
         */

        while (System.currentTimeMillis() < beginTime + TIMEOUT_DELAY || invocation.getRemainingRetryTimes() > 0){

            long currentTime = System.currentTimeMillis();
            Object object  = CommonClientCache.RESP_MAP.get(invocation.getUuid());
            // result has been updated
            if (object instanceof RpcInvocation){
                log.info("Response ... => "+ ((RpcInvocation)object).toString());
                return ((RpcInvocation)object).getResponse();
            }

            if (currentTime > beginTime + TIMEOUT_DELAY){
                log.error("Retry for request "+invocation.getUuid()
                +"... "+(invocation.getRemainingRetryTimes()-1) +" time remaining");
                beginTime = currentTime;
                invocation.setRemainingRetryTimes(invocation.getRemainingRetryTimes()-1);
                CommonClientCache.CLIENT_SEND_QUEUE.put(invocation);
            }

        }

        CommonClientCache.RESP_MAP.remove(invocation.getUuid());
        throw new TimeoutException("Server Response Timeout....");
    }
```

After getting the `RpcProtocol` object, the method could be called through `BizHandler`which will handle the request.

```
    @Override
    public void run() {
        while (true){

            try {
                ServerChannelReadData readData
                        = this.blockingQueue.take();

                log.info("incoming new requests ...");

                this.executorService.submit(new BizHandler(readData));

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }
```

`BizHandler` Logic:

```Java
    @Override
    public void run() {



        log.warn(Thread.currentThread().getName()
        +" : performing biz logics");

        RpcProtocol protocol = readData.getProtocol();
        ChannelHandlerContext ctx = this.readData.getChannelHandlerContext();


        /**
         * 此时Object类型可以转换
         */

        /**
         * 原来FASTJSON 已被替换
         */
        // 这里content就包含调用服务的信息 =》 应该是json格式
//        String content = new String(protocol.getContent(), 0, protocol.getContentLength());

//        log.info("getting protocol msg => "+ content);

        // 将调用信息转换为RpcInvocation，里面包含RPC的调用信息

        //RpcInvocation invocation = JSON.parseObject(content, RpcInvocation.class);

        RpcInvocation invocation = CommonServiceCache.SERIALIZATION_FACTORY.deserialize(protocol.getContent(),RpcInvocation.class);

        try {
            CommonServiceCache.SERVER_FILTER_CHAIN_BEFORE.doFilter(invocation);
        }catch (Exception e){
            invocationError(invocation,e);
            wrapInvocationAndSend(invocation,ctx);
            return;
        }

//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        Object aimObject = PROVIDER_SERVICE_MAP.get(invocation.getTargetServiceName());

        /**
         * 方法不止一个
         */
        Method[] methods = aimObject.getClass().getDeclaredMethods();

        Object result = null;

        /**
         * 遍历方法，确定到底调用哪一个
         */
        for (Method method : methods){
            if (method.getName().equals(invocation.getTargetMethod())){
                // 即使返回值为空也没问题？
                // 没关系 此时result = null
                /**
                 * 此处完成RPC最终调用
                 */

                try {
                    result = method.invoke(aimObject,invocation.getArgs());
                } catch (Exception e) {
                    invocationError(invocation,e);
                }
                break;
            }
        }

        if (result != null){
            log.info("getting result =>"+ result.toString());
        }else{
            log.info("get result => null");
        }

        try {
            CommonServiceCache.SERVER_FILTER_CHAIN_AFTER.doFilter(invocation);
        }catch (Exception e){
            invocationError(invocation,e);
            wrapInvocationAndSend(invocation,ctx);
            return;
        }

        invocation.setResponse(result);

        /**
         * 重新打包，发送出去，将值基于writeAndFlush返回
         */
//        RpcProtocol resProtocol = new RpcProtocol(JSON.toJSONString(invocation).getBytes());

        wrapInvocationAndSend(invocation,ctx);

    }
```





And the client will try to deserialize and return the response

```java
@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol protocol = (RpcProtocol) msg;


        /**
         * 获取内容
         */
//        String content = new String(protocol.getContent(), 0, protocol.getContentLength());
//        log.info("getting protocol msg => "+ content);

        /**
         * 转换为请求对象及结果
         */
//        RpcInvocation invocation = JSON.parseObject(content, RpcInvocation.class);

        RpcInvocation invocation =
                CommonClientCache.SERIALIZATION_FACTORY.deserialize(
                        protocol.getContent(),
                        RpcInvocation.class
                );



        /**
         * 获得的结果匹配不上则报错！
         */
        if (!CommonClientCache.RESP_MAP.containsKey(invocation.getUuid())){
            log.error("uuid of Request out of data : "+invocation.getUuid());
            ReferenceCountUtil.release(msg);
            return;
//            throw new IllegalArgumentException("Server response error...");
        }


        /**
         * 如果Error 直接打印并结束
         */
        if (invocation.getError()!=null){
            invocation.getError().printStackTrace();
            ReferenceCountUtil.release(msg);
            return;
        }

        if ((boolean)(invocation.getAttachments().getOrDefault(RpcConstants.ASYNC_TAG,false))){
            log.warn("Async cmd -> " + invocation.getResponse());
            ReferenceCountUtil.release(msg);
            return;
        }

        /**
         * 更新结果到RESP_MAP里
         */
        CommonClientCache.RESP_MAP.put(invocation.getUuid(),invocation);
        /**
         * 需要释放msg，防止内存泄漏
         */
        ReferenceCountUtil.release(msg);
    }
```

### Service Registration

We use `ZooKeeper` as register center.

- Provide dynamic register and un-register, subscribe and un-subscribe.
- watch method is enabled for auto-detection

For server end, it should provide node info:

```java
@Data
public class ProviderNodeInfo {

    private String serviceName;

    private String address;

    private Integer weight;

    private String registerTime;

    private String group;

}
```

**curator-framework** is used for the intergration of Zookeeper API.

### Router Balance

For load balance, **IRouter** supports select `ChannelFutureWrapper` based on different strategy. The later is created via registration information.

```java
    public static ChannelFutureWrapper createChannelFutureWrapperFromAddr(String serverAddress){
        ChannelFutureWrapper wrapper =
                new ChannelFutureWrapper();
        String host = serverAddress.split(":")[0];
        int port = Integer.valueOf(serverAddress.split(":")[1]);
        wrapper.setHost(host);
        wrapper.setPort(port);
        try {
            ChannelFuture future
                    = ConnectionHandler.createChannelFuture(
                            host,
                            port
                    );
            wrapper.setChannelFuture(future);
            return wrapper;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
```

The strategy supports `weightBased` and `rotationBased` balancing.



### Serialize

We use `SerializeFactory` to support different kinds of `Serialize Methods` :

-  JDK
- FastJson
- Hessian
- Kryo



### Filter

Filters are introduced to treat request differently.

```java
@Slf4j
@SPI("before")
public class ServerLogFilterImpl implements IServerFilter {
    @Override
    public void doFilter(RpcInvocation invocation) {
        log.info(String.valueOf(invocation.getAttachments().get(RpcConstants.CLIENT_APP_NAME_TAG))
        + " invoke "+ invocation.getTargetServiceName() + " : "
        + invocation.getTargetMethod());
    }
}
```

And tags are given to load them automatically. (Customized SPI : use classloader to load classes identified under certain directory) 

```
    static List<IServerFilter> loadAndGetCorrespondFilters(String tag) throws Exception{
        load();
        List<IServerFilter> filters = new ArrayList<>();
        List<Object> objects = ExtensionLoader.getInstanceByAnnotationTag(clazz,tag);
        for (Object object : objects){
            filters.add((IServerFilter) object);
        }
        return filters;
    }
```

### Optimization

- instant-retry is enabled.
- Flow limit

### SpringBoot-Friendly

annotation `IRpcService` and `IRpcReference` is introduced.

```Java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IRpcReference {

    String url() default "";

    String group() default "default";

    String serviceToken() default "";

    int retry() default 1;

    boolean async() default false;

}
```

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface IRpcService {

    int limit() default 0;

    String group() default "default";

    String serviceToken() default "";

}
```

`IRpcService` is for Server-side and `IRpcReference` is for client side.



## Customization(In Production)

- Different Flow limit for different services
- Elastic response time for different services
- Wrap client-side into Openstack-sdk



