## 2021 年3月10日 多云 下午面了陌陌的总监和滴滴hr 大霾

### 面试公司 -- 今日头条

#### 一面：设计技术点较零碎并且不进行深度问

##### Q1: 自我介绍

A1: 巴拉巴拉

##### Q2: TIME_WAIT 和 CLOSE_WAIT

A2: https://blog.csdn.net/cpcpcp123/article/details/51260031

        TCP协议规定，对于已经建立的连接，网络双方要进行四次握手才能成功断开连接，如果缺少了其中某个步骤，将会使连接处于假死状态，连接本身占用的资源不会被释放。
        网络服务器程序要同时管理大量连接，所以很有必要保证无用连接完全断开，否则大量僵死的连接会浪费许多服务器资源。
        
        在众多TCP状态中，最值得注意的状态有两个：CLOSE_WAIT和TIME_WAIT。
               
        TIME_WAIT:
            
            通信双方建立TCP连接后，主动关闭连接的一方就会进入TIME_WAIT状态。
                    客户端主动关闭连接时，会发送最后一个ack后，然后会进入TIME_WAIT状态，再停留2个MSL时间(后有MSL的解释)，进入CLOSED状态。
            TIME_WAIT是主动关闭连接的一方保持的状态，进入TIME_WAIT的状态后会保持这个状态2MSL（max segment lifetime）时间，超过这个时间之后，彻底关闭回收资源。
            由于TIME_WAIT的时间会非常长，因此server端应尽量减少主动关闭连接
            
            为什么要这么做 两个方面的考虑：
                1.防止上一次连接中的包，迷路后重新出现，影响新连接（经过2MSL，上一次连接中所有的重复包都会消失）
                2.可靠的关闭TCP连接。
                    在主动关闭方发送的最后一个 ack(fin)，有可能丢失，这时被动方会重新发fin, 如果这时主动方处于 CLOSED 状态 ，就会响应 rst 而不是 ack。
                    所以主动方要处于 TIME_WAIT 状态，而不能是 CLOSED 。
                        另外这么设计TIME_WAIT 会定时的回收资源，并不会占用很大资源的，除非短时间内接受大量请求或者受到攻击。
            基于TCP的HTTP协议，关闭TCP连接的是Server端，这样，Server端会进入TIME_WAIT状态，对于访问量大的Web Server，会存在大量的TIME_WAIT状态。
            
            为什么需要 TIME_WAIT 状态？
                假设最终的主动关闭的最后一次验证的ACK丢失，被动关闭一次的server将重发FIN，client必须维护TCP状态信息以便可以重发最终的ACK，否则会发送RST，结果server认为发生错误。TCP
                实现必须可靠地终止连接的两个方向(全双工关闭)，client必须进入TIME_WAIT 状态，因为client可能面【重发】最终ACK的情形。
            为什么 TIME_WAIT 状态需要保持 2MSL 这么长的时间？
                虽然按道理，四个报文都发送完毕，我们可以直接进入CLOSE状态了，但是我们必须假想网络是不可靠的，有可以最后一个ACK丢失。
                所以TIME_WAIT状态就是用来重发可能丢失的ACK报文。在Client发送出最后的ACK回复，但该ACK可能丢失。
                Server如果没有收到ACK，将不断重复发送FIN片段。
                所以Client不能立即关闭，它**必须确认Server接收到了该ACK**。
                Client会在发送出ACK之后进入到TIME_WAIT状态【主动发起关闭一侧】。
                Client会设置一个计时器，等待2MSL的时间。如果在该时间内再次收到FIN，那么Client会重发ACK并再次等待2MSL。
                所谓的2MSL是两倍的MSL(Maximum Segment Lifetime)。
                MSL指一个片段在网络中最大的存活时间，2MSL就是一个发送和一个回复所需的最大时间。如果直到2MSL，Client都没有再次收到FIN，那么Client推断ACK已经被成功接收，则结束TCP连接。
    
        CLOSE_WAIT:
            CLOSE_WAIT是被动关闭连接是形成的。
            根据TCP状态机，服务器端收到客户端发送的FIN，则按照TCP实现发送ACK，因此进入CLOSE_WAIT状态。
            但如果服务器端不执行close()，就不能由CLOSE_WAIT迁移到LAST_ACK，则系统中会存在很多CLOSE_WAIT状态的连接。
            此时，可能是系统忙于处理读、写操作，而未将已收到FIN的连接，进行close。
            此时，recv/read已收到FIN的连接socket，会返回0。
            
 
        TIME_WAIT 和CLOSE_WAIT状态socket过多
        
        如果服务器出了异常，百分之八九十都是下面两种情况：   
        1.服务器保持了大量TIME_WAIT状态
        2.服务器保持了大量CLOSE_WAIT状态，简单来说CLOSE_WAIT数目过大是由于被动关闭连接处理不当导致的。
        因为linux分配给一个用户的文件句柄是有限的，而TIME_WAIT和CLOSE_WAIT两种状态如果一直被保持，那么意味着对应数目的通道就一直被占着；
        一旦达到句柄数上限，新的请求就无法被处理了，接着就是大量Too Many Open Files异常，Tomcat崩溃。

#### 偷一下别人的阿里面试题

##### Q1: aop在spring中的实现

##### A1:

            1、扫描
            2、代理生成
            3、拦截

##### Q1: 解释jdk和cglib动态代理的实现及区别

##### A1:

            1、jdk动态代理
            2、cglib动态代理
            java动态代理是利用反射机制生成一个实现代理接口的匿名类，在调用具体方法前调用InvokeHandler来处理。

而cglib动态代理是利用asm开源包，对代理对象类的class文件加载进来，通过修改其字节码生成子类来处理

1、如果目标对象实现了接口，默认情况下会采用JDK的动态代理实现AOP 2、如果目标对象实现了接口，可以强制使用CGLIB实现AOP
3、如果目标对象没有实现了接口，必须采用CGLIB库，spring会自动在JDK动态代理和CGLIB之间转换

（1）JDK动态代理只能对实现了接口的类生成代理，而不能针对类

（2）CGLIB是针对类实现代理，主要是对指定的类生成一个子类，覆盖其中的方法

 	cglib的几个重要方法：
 		生成代码继承自：MethodInterceptor

 		使用Enhancer 进行代理过程实现
 			1、setSuperclass代理的目标类
 			2、setCallback 配置拦截器实现内容
 				MethodInterceptor的intercept 方法中去实现增加或者拦截功能
         
         JDK代理是不需要以来第三方的库，只要要JDK环境就可以进行代理，它有几个要求
         
         * 实现InvocationHandler
         * 使用Proxy.newProxyInstance产生代理对象
         * 被代理的对象必须要实现接口 CGLib 必须依赖于CGLib的类库，但是它需要类来实现任何接口代理的是指定的类生成一个子类，覆盖其中的方法，是一种继承但是针对接口编程的环境下推荐使用JDK的代理
           在Hibernate中的拦截器其实现考虑到不需要其他接口的条件Hibernate中的相关代理采用的是CGLib来执行。
         
         jdk动态代理的内部实现：
         https://blog.csdn.net/yhl_jxy/article/details/80586785
         JDK动态代理基于拦截器和反射来实现。 JDK代理是不需要第三方库支持的，只需要JDK环境就可以进行代理，使用条件： 1）代理增强类，必须实现InvocationHandler接口；
         2）被代理的对象，使用Proxy.newProxyInstance产生代理对象； 3）被代理的对象必须要实现接口； 关于Proxy.newProxyInstance的实现 newProxyInstance()方法帮我们执行了
         生成代理类----获取构造器----生成代理对象 这三步； 生成代理类:Class<?> cl = getProxyClass0(loader, intfs);
                         先从缓存中获取是否有已经生成的代理类
                         ProxyClassFactory.apply()实现代理类创建。
                             Class.forName
                             byte[] proxyClassFile = ProxyGenerator.generateProxyClass(proxyName, interfaces, accessFlags);
                         defineClass0(loader, proxyName,
                                             proxyClassFile, 0, proxyClassFile.length);
                         generateProxyClass生成了字节码文件
                     获取构造器: final Constructor<?> cons = cl.getConstructor(constructorParams); 生成代理对象: cons.newInstance(new
         Object[]{h});

##### Q1: Spring的事务是如何实现的

##### A1:

            1、基于aop
            2、事务是基于连接的

            https://zhuanlan.zhihu.com/p/54067384

##### Q1: 线程池的配置参数和运行过程 如何留活 各个阻塞队列的锁及内部数据结构

##### A1:

            1、留活实现
            2、blockingqueue
                a、ArrayListBlockingQueue
                b、LinkedListBlockingQueue
                c、SynircBlockingQueue
                d、ArrayList

##### Q1: 消息队列的比较，kafka如何实现高吞吐量

##### A1:

            1、顺序读写  
                生产者生产的消息发送到kafka服务器会放到内存中 等一会儿在批量顺序写盘，顺序写 比较快
                读的时候也是一块内存一下子读
                关于内存的安全性通过多副本保证 
            2、零拷贝 
                读数据的使用通过mmf方式从内核空间放到网络传输位置，之间返回给拉区侧
            3、数据压缩 
                生产者可以压缩文件上传 减少网络io 高吞吐下cpu的耗能不急网络io 
                消费者消费的时候可以解压使用
            4、topic分区、文件分段
                kafka中的topic中的内容可以被分为多分partition存在,每个partition又分为多个段segment,所以每次操作都是针对一小部分做操作，很轻便，并且增加并行操作的能力
            5、批量发送 
                生产者可以按时或者按量 定时定量发送消息 减少阻塞
            6、kafka不保存消费状态，通过定义消费这的offset来确定消息消费情况

##### Q1: 解释explain执行结果如何解读

##### A1:

            id、select_type、table、type、possible_keys、key、key_len、ref、rows、Extra
            id: 当包含自查询的时候会存在多条记录 id越大越先执行
            select_type:
                (1) SIMPLE(简单SELECT,不使用UNION或子查询等)
                (2) PRIMARY(查询中若包含任何复杂的子部分,最外层的select被标记为PRIMARY)
                (3) UNION(UNION中的第二个或后面的SELECT语句)
                (4) DEPENDENT UNION(UNION中的第二个或后面的SELECT语句，取决于外面的查询)
                (5) UNION RESULT(UNION的结果)
                (6) SUBQUERY(子查询中的第一个SELECT)
                (7) DEPENDENT SUBQUERY(子查询中的第一个SELECT，取决于外面的查询)
                (8) DERIVED(派生表的SELECT, FROM子句的子查询)
                (9) UNCACHEABLE SUBQUERY(一个子查询的结果不能被缓存，必须重新评估外链接的第一行)
            table:
                数据是关于哪张表的，也会存在查询过程中的中间数据
            type:
                表示MySQL在表中找到所需行的方式，又称“访问类型”。
                常用的类型有： ALL, index,  range, ref, eq_ref, const, system, NULL（从左到右，性能从差到好）
                ALL：Full Table Scan， MySQL将遍历全表以找到匹配的行
                index: Full Index Scan，index与ALL区别为index类型只遍历索引树
                range:只检索给定范围的行，使用一个索引来选择行
                ref: 表示上述表的连接匹配条件，即哪些列或常量被用于查找索引列上的值
                eq_ref: 类似ref，区别就在使用的索引是唯一索引，对于每个索引键值，表中只有一条记录匹配，简单来说，就是多表连接中使用primary key或者 unique key作为关联条件
                const、system: 当MySQL对查询某部分进行优化，并转换为一个常量时，使用这些类型访问。如将主键置于where列表中，MySQL就能将该查询转换为一个常量,system是const类型的特例，当查询的表只有一行的情况下，使用system
                NULL: MySQL在优化过程中分解语句，执行时甚至不用访问表或索引，例如从一个索引列里选取最小值可以通过单独索引查找完成。
            possible_keys:
                可能使用到的索引，查询字段涉及到的索引均被列出，但不一定被使用
            key:
                mysql实际决策使用的索引
            key_len:
                索引中使用的字节数，可以通过该列计算查询中使用的索引长度 最大可能长度
            ref:
                表的连接匹配条件，即哪些列或常量被用于查找索引列上的值
            rows:
                mysql根据表统计信息及索引选用情况，估算的找到所需的记录所需要读取的行数 预估锁
            Extra:
                Using where: 列数据是从仅仅使用了索引中的信息而没有读取实际的行动的表返回的，这发生在对表的全部的请求列都是同一个索引的部分的时候，表示mysql服务器将在存储引擎检索行后再进行过滤
                Using temporary：表示MySQL需要使用临时表来存储结果集，常见于排序和分组查询
                Using filesort：MySQL中无法利用索引完成的排序操作称为“文件排序”
                Using join buffer：改值强调了在获取连接条件时没有使用索引，并且需要连接缓冲区来存储中间结果。如果出现了这个值，那应该注意，根据查询的具体情况可能需要添加索引来改进能。
                Impossible where：这个值强调了where语句会导致没有符合条件的行。               
                Select tables optimized away：这个值意味着仅通过使用索引，优化器可能仅从聚合函数结果中返回一行

##### Q1: Java中的锁有哪几种

##### A1:

           公平锁/非公平锁
           可重入锁
           独享锁/共享锁
           互斥锁/读写锁
           乐观锁/悲观锁
           分段锁
           偏向锁/轻量级锁/重量级锁
           自旋锁
           
           1、公平非公平锁
                公平锁是指多个线程按照申请锁的顺序来获取锁。
                非公平锁是指多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁。有可能，会造成优先级反转或者饥饿现象。
                对于Java ReentrantLock而言，通过构造函数指定该锁是否是公平锁，默认是非公平锁。非公平锁的优点在于吞吐量比公平锁大。
                对于Synchronized而言，也是一种非公平锁。由于其并不像ReentrantLock是通过AQS的来实现线程调度，所以并没有任何办法使其变成公平锁 
           2、可重入锁
           可重入锁又名递归锁，是指在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁。 
           对于Java ReentrantLock而言, 他的名字就可以看出是一个可重入锁，其名字是Re entrant Lock重新进入锁。
                如何实现:
                    cas获取到锁之后会将thread信息设置到所持有这信息上，标记出锁持有线程
                    通过记录加锁次数来统计锁住次数
           对于Synchronized而言,也是一个可重入锁。可重入锁的一个好处是可一定程度避免死锁。 
           锁上会标记锁持有线程和一个计数器
          3、独享锁/共享锁
          独享锁是指该锁一次只能被一个线程所持有。
          共享锁是指该锁可被多个线程所持有。
          
          对于Java ReentrantLock而言，其是独享锁。
          但是对于Lock的另一个实现类ReadWriteLock，其读锁是共享锁，其写锁是独享锁。
          读锁的共享锁可保证并发读是非常高效的，读写，写读 ，写写的过程是互斥的。
          独享锁与共享锁也是通过AQS来实现的，通过实现不同的方法，来实现独享或者共享。
          对于Synchronized而言，当然是独享锁。
          
          4、互斥锁/读写锁
          上面讲的独享锁/共享锁就是一种广义的说法，互斥锁/读写锁就是具体的实现。
          互斥锁在Java中的具体实现就是ReentrantLock
          读写锁在Java中的具体实现就是ReadWriteLock
          
          5、乐观锁/悲观锁
          乐观锁与悲观锁不是指具体的什么类型的锁，而是指看待并发同步的角度。
          悲观锁认为对于同一个数据的并发操作，一定是会发生修改的，哪怕没有修改，也会认为修改。因此对于同一个数据的并发操作，悲观锁采取加锁的形式。悲观的认为，不加锁的并发操作一定会出问题。
          乐观锁则认为对于同一个数据的并发操作，是不会发生修改的。在更新数据的时候，会采用尝试更新，不断重新的方式更新数据。乐观的认为，不加锁的并发操作是没有事情的。
          
          从上面的描述我们可以看出，悲观锁适合写操作非常多的场景，乐观锁适合读操作非常多的场景，不加锁会带来大量的性能提升。
          悲观锁在Java中的使用，就是利用各种锁。
          乐观锁在Java中的使用，是无锁编程，常常采用的是CAS算法，典型的例子就是原子类，通过CAS自旋实现原子操作的更新。
          
          6、分段锁
          分段锁其实是一种锁的设计，并不是具体的一种锁，对于ConcurrentHashMap而言，其并发的实现就是通过分段锁的形式来实现高效的并发操作。
          我们以ConcurrentHashMap来说一下分段锁的含义以及设计思想，ConcurrentHashMap中的分段锁称为Segment，它即类似于HashMap（JDK7与JDK8中HashMap的实现）的结构，
            即内部拥有一个Entry数组，数组中的每个元素又是一个链表；同时又是一个ReentrantLock（Segment继承了ReentrantLock)。
          当需要put元素的时候，并不是对整个hashmap进行加锁，而是先通过hashcode来知道他要放在那一个分段中，然后对这个分段进行加锁，所以当多线程put的时候，只要不是放在一个分段中，就实现了真正的并行的插入。
          但是，在统计size的时候，可就是获取hashmap全局信息的时候，就需要获取所有的分段锁才能统计。
          分段锁的设计目的是细化锁的粒度，当操作不需要更新整个数组的时候，就仅仅针对数组中的一项进行加锁操作。
          
          7、偏向锁/轻量级锁/重量级锁
          这三种锁是指锁的状态，并且是针对Synchronized。在Java 5通过引入锁升级的机制来实现高效Synchronized。
          这三种锁的状态是通过对象监视器在对象头中的字段来表明的。
          偏向锁是指一段同步代码一直被一个线程所访问，那么该线程会自动获取锁。降低获取锁的代价。
          轻量级锁是指当锁是偏向锁的时候，被另一个线程所访问，偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，提高性能。
          重量级锁是指当锁为轻量级锁的时候，另一个线程虽然是自旋，但自旋不会一直持续下去，当自旋一定次数的时候，还没有获取到锁，就会进入阻塞，该锁膨胀为重量级锁。重量级锁会让其他申请的线程进入阻塞，性能降低。
          
          8、自旋锁
          在Java中，自旋锁是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗CPU。
          典型的自旋锁实现的例子，可以参考自旋锁的实现 后来升级为自适应自旋 
  
   
  