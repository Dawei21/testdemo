package com.dawei.test.demo.bloomfilter;

import com.dawei.test.demo.bloomfilter.MyBloomFilter.MisjudgmentRate;
import java.util.UUID;

/**
 * @author by Dawei on 2019/11/23.
 */
public class BloomFilterDemo {

    public void checkFailed() throws Exception {

        MyBloomFilter myBloomFilter = new MyBloomFilter(10000, MisjudgmentRate.MIDDLE, 0.8);

        //容量一万 存下会有多少个重复的
        int i = 9000;
        for (;i-- > 0;) {
            String data = UUID.randomUUID().toString();
            myBloomFilter.add(data);
        }

        int j = 100000;
        int failedCount = 0;
        int dataCount = 0;
        for (;j-- > 0;) {

            String dataA = UUID.randomUUID().toString();
            boolean checkExist = myBloomFilter.checkExist(dataA);
            dataCount++;
            if (checkExist ){
                System.out.println(String.format("Count : %s filedNum = %s ", dataCount , ++failedCount));
            }
        }
        System.out.println(myBloomFilter.getNoteBook());
    }


    /**
     * 第一个异常数据
     * @throws Throwable
     */
    private void firstFaild() throws Throwable{
        MyBloomFilter myBloomFilter = new MyBloomFilter(10000, MisjudgmentRate.MIDDLE, null);

        int failedCount = 0;
        int dateCount = 0;
        while (true) {
            String data = UUID.randomUUID().toString();
            dateCount++;
            if(myBloomFilter.checkExist(data)) {
                System.out.println(String.format("Count : %s filedNum = %s failedData= %s", dateCount , ++failedCount, data));
                break;
            }
            myBloomFilter.add(data);
        }
    }


    public static void main(String[] args) throws Exception {
        BloomFilterDemo bloomFilterDemo = new BloomFilterDemo();
        bloomFilterDemo.checkFailed();
    }
}
