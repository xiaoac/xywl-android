package com.nandasoftits.xingyi.utils;

import com.nandasoftits.xingyi.entity.MyMessage;
import com.nandasoftits.xingyi.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class TestData {

    public static List<Order> getOrderList(int currentPage, int pageSize) {
        List<Order> retList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Order order = new Order();

            order.setTitle("测试标题" + i + "[" + currentPage + "][" + pageSize + "]");
            order.setStartTime(System.currentTimeMillis() + i * 1000 * 60 * 60);
            order.setStartAddress("重庆" + i + "号码头！");
            order.setEndAddress("成都" + i + "号码头！");
            order.setMsg("特殊要求：需要" + i + "个人。");

            retList.add(order);
        }

        return retList;
    }

    public static List<Order> getUserOrderList(int currentPage, int pageSize) {
        List<Order> retList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Order order = new Order();

            order.setTitle("测试标题" + i + "[" + currentPage + "][" + pageSize + "]");
            order.setStartTime(System.currentTimeMillis() + i * 1000 * 60 * 60);
            order.setStartAddress("重庆" + i + "号码头！");
            order.setEndAddress("成都" + i + "号码头！");
            order.setMsg("特殊要求：需要" + i + "个人。");
            order.setState((i % 6));

            retList.add(order);
        }

        return retList;
    }

    public static List<MyMessage> getMyMessageList(int currentPage, int pageSize) {
        List<MyMessage> retList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MyMessage message = new MyMessage();

            message.setTitle("测试标题" + i + "[" + currentPage + "][" + pageSize + "]");
            message.setTime(System.currentTimeMillis() - i * 5 * 1000 * 60 * 60);
            message.setGoodName("货物" + i);
            message.setMsg("已审核通过请查看。");
            message.setOrderNumber("000" + i);
            retList.add(message);
        }

        return retList;
    }


}
