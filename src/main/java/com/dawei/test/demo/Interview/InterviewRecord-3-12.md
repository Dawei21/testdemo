## 2021 年3月12日 下雨 晚上8点面试快手 被一顿虐 还知道组内好几个人准备跑路

### 面试公司 -- 今日头条


#### 一面：设计技术点较零碎并且不进行深度问
   
   
   #####Q1: 自我介绍
   A1: 巴拉巴拉
   
   #####Q2: BloomFilter 错误率过高如何优化
   A2: 
         
 
   #####Q3: Dubbo的spi与jdk的spi有啥区别
   #####A3: 
            Spi是一种动态替换发现机制，使用Spi机制的优势是实现解耦，使第三方服务模块的装配控制逻辑与调用者的业务代码分离
            jdk：
                1）当服务提供者提供了一个服务（接口）的具体实现后，在classpath下的META-INF/services目录下创建一个以“接口全限定名”命名的文件，内容为实现类的全限定名。            
                2）接口实现类所在的jar包放在主程序的classpath中；
                3）主程序通过java.util.ServiceLoader动态状态实现模块（类），它通过扫描META-INF/services目录下的配置文件找到实现类的全限定名，把类加载到JVM。
                4）Spi的实现类必须携带一个无参构造
            dubbo：
                Dubbo支持更多的加载路径，
                不是通过Iterator的形式调用而是通过名称来定位具体的Provider
                按照需要进行加载，并非Jdk中的一次性全部加载，效率更高
                同时支持Provider以类似IOC的形式提供。    
                
                Dubbo自己实现Spi的目的
                1、JDK标准的SPI会一次性实例化拓展点的所有实现，如果所有的实现初始化很耗时，并没加载上也没有用，就会很浪费资源。
                2、如果有的拓展点加载失败，则所有的拓展点无法使用。
                3、提供了对拓展点包装的功能（Adaptive）,并且还支持Set的方式对其他拓展点进行注入
                
                Dubbo中实现Spi与JDK形式上的区别
                1、接口上需要添加“org.apache.dubbo.common.extension”包下的@SPI注解，在@SPI("spiService")注解中可以指定默认拓展点。
                2、在META-INF/dubbo目录下创建全限定名文件
                3、全限定名文件中的内容是KEY-VALUE形式，value是实现类的全限定类名
                4、调用者使用ExtensionLoader获取加载
   
   #####Q4: java的synchronized 锁膨胀过程及如何实现 为啥会叫重量级锁
   #####A4: 
            不同锁的实现有啥区别

   #####Q5: ReentrantLock 如何加锁 竞争锁过程中  线程状态
   #####A5:
            基于非公平锁：NonfairSync
            lock() {
             if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
             else
                acquire(1);
             }   
             acquire() {
                if (!tryAcquire(arg) &&
                    acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
                 selfInterrupt();    
            }

   #####Q6: CyclicBarrier、 CountDownLatch、Semaphore 具体是啥作用 如何实现的
   #####A6: 
            https://blog.csdn.net/weixin_43113679/article/details/95501156
            Semaphore
                FairSync 或 NonfairSync 锁的使用 
                for(;;;) {
                    unsafe.compareAndSwapInt(this, stateOffset, expect, update);
                }
                
   
   #####Q7: ThreadLocal 底层数据结构 如何保证不内存泄漏  
   #####A7: 
            https://www.jianshu.com/p/3c5d7f09dfbd
            thread 中会存储一个threadlocalMap的对象 来维护threadlocal和值的对应关系 threadlocal作为key 存储WeakReference作为entry的"map"
            
            注意如果没有手动移除 ThreadLocal，而他有一直以强引用状态存活，就会导致 value 无法回收，至最终 OOM；所以在使用 ThreadLocal 的时候，最后一定要手动移除；
            
            讲的很细：https://www.cnblogs.com/sanzao/p/10535699.html
            
            追问: ThreadLocal中的map和传统map的区别
                HashMap 使用拉链法解决哈希冲突
                ThreadLocalMap 是使用线性探测法解决哈希冲突
          
            追问: InheriableThreadLocal 是否有用过 有啥作用 TransmittableThreadLocal使用
            InheriableThreadLocal: https://www.jianshu.com/p/29f4034f4250
            
            线程是如何能获取到父线程保存到InheritableThreadLocal类型上下文中数据的呢？
            原来是在创建Thread对象时，会判断父线程中inheritableThreadLocals是否不为空，如果不为空，则会将父线程中inheritableThreadLocals中的数据复制到自己的inheritableThreadLocals中。这样就实现了父线程和子线程的上下文传递。
           
   
   #####Q8: java8的内存模型
   #####A8: 
            
            追问: 为啥java8有增加元数据区，原因是啥
            
   
   #####Q9: redis 字典的数据结构及扩容过程 及 扩容过程中如何查找和写入的
   #####A9: 
            
   
   #####Q10: 获取数组中窗口范围内最大值
   #####A10: 
            
      
   #####Q11: https
   #####A11:

      https ： https://juejin.cn/post/6850418120629485582
      
      http 通信存在以下问题：
      通信使用明文可能会被窃听
      不验证通信方的身份可能遭遇伪装
      无法证明报文的完整型，可能已遭篡改
      
      什么是 https，为什么需要 https
      https是 http + ssl 是HTTP的安全版，通过TLS/SSL加密的http协议
      什么是ssl：
      它是一种标准协议，用于加密浏览器和服务器之间的通信。
      SSL证书就是遵守SSL协议，由受信任的CA机构颁发的数字证书。
      
          SSL/TLS的工作原理:
          了解工作原理先要了解加密算法：
              对称加密：通信双方使用相同的密钥进行加密。特点是加密速度快，但是缺点是需要保护好密钥，如果密钥泄露的话，那么加密就会被别人破解。常见的对称加密有AES，DES算法。
              非对称加密：它需要生成两个密钥：公钥(Public Key)和私钥(Private Key)。
                  公钥负责加密，私钥负责解密；或者，私钥负责加密，公钥负责解密。这种加密算法安全性更高，但是计算量相比对称加密大很多，加密和解密都很慢。常见的非对称算法有RSA。
      Https的作用：
      
          内容加密 建立一个信息安全通道，来保证数据传输的安全；
          身份认证 确认网站的真实性
          数据完整性 防止内容被第三方冒充或者篡改
      
      https 的连接过程
      链接主要分两部分：
      证书验证阶段  【非对称加密】
      
                  主要分为三个过程：
                  浏览器发起请求
                  服务器接收到请求之后，会返回证书，包括公钥
                  浏览器接收到证书之后，会检验证书是否合法，不合法的话，会弹出告警提示
              数据传输阶段  【对称加密】
                  证书验证合法之后
                      浏览器会生成一个随机数，
                      使用公钥进行加密，发送给服务端
                      服务器收到浏览器发来的值，使用私钥进行解密
                      解析成功之后，使用对称加密算法进行加密，传输给客户端
      
      
      https 的加密方式是怎样的，对称加密和非对称加密，为什么要这样设计？内容传输为什么要使用对称机密
      认证过程使用非对称加密保证随机数安全，传输过程中使用对称加密保证数据传输中加解密效率；安全的随机数在一般安全的对称加密中使用也挺安全
      https 是绝对安全的吗
      可以使用中间人攻击
      中间人模拟服务端接收用户请求，模拟客户端请求目标服务器，劫持中间内容
      
          https 无法防止中间人攻击，只有做证书固定ssl-pinning 或者 apk中预置证书做自签名验证可以防中间人攻击。具体的可以看这一篇文章。
      

            
   
   
  