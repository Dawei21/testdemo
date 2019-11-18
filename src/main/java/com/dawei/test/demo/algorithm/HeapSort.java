package com.dawei.test.demo.algorithm;

import com.sun.istack.internal.NotNull;

import java.util.Arrays;

/**
 * @author by Dawei on 2019/2/15.
 * 堆排序
 * 堆的方式分为最小堆和最大堆
 * 堆顶最小或堆顶最大
 * 堆排序实现：
 * 1、根据初始数组去构造初始堆（构建一个完全二叉树，保证所有的父结点都比它的孩子结点数值大）。
 * 2、每次交换第一个和最后一个元素，输出最后一个元素（最大值），然后把剩下元素重新调整为大根堆。
 */
public class HeapSort {


    /**
     * 将数据转为堆排列
     * 因为堆形成是个递归过程 过程中父节点[堆顶再循环变化]
     *
     * @param array  整理的数组
     * @param parent 父节点
     * @param length 需要整理的数组长度
     */
    private static void createHeap(int parent, @NotNull int[] array, int length) {

        //父节点的值
        int temp = array[parent];
        //左孩子的位置
        int child = parent * 2 + 1;
        while (child < length) {
            //1、判断父节点有右孩子且 且右孩子大于左孩子
            if (child + 1 < length && array[child] < array[child + 1]) {
                child++;
            }

            //2、如孩子节点比父节点大 则将孩子节点值给父节点 否则父节点值大返回
            if (array[child] > temp) {
                array[parent] = array[child];
                //孩子节点成为父节点
                parent = child;
                //孩子的左孩子成为孩子的左孩子
                child = 2 * child + 1;
                //3、上文若子节点比父节点大 此时parent == child  即将父节点的值给子节点 否则只是将值还原回去
                array[parent] = temp;
            } else {
                break;
            }
        }
    }

    private static void heapSort(int[] array) {

        //循环初始化堆
        for (int i = array.length / 2; i >= 0; i--) {
            createHeap(i, array, array.length);
        }
        System.out.println("初始化堆:  \t" + Arrays.toString(array));

        // 进行n-1次循环，完成排序
        for (int i = array.length - 1; i > 0; i--) {
            // 最后一个元素和第一元素进行交换
            int temp = array[i];
            array[i] = array[0];
            array[0] = temp;

            // 筛选 R[0] 结点，得到i-1个结点的堆
            createHeap(0, array, i);
            System.out.format("第 %d 趟: \t", array.length - i);
            System.out.println(Arrays.toString(array));
        }

    }


    public static void main(String[] args) {
        // 初始化一个序列
        int[] array = {
                1, 3, 4, 5, 2, 6, 9, 7, 8, 0
        };


        System.out.print("排序前:\t");
        System.out.println(Arrays.toString(array));
        heapSort(array);
        System.out.print("排序后:\t");
        System.out.println(Arrays.toString(array));
    }


}
