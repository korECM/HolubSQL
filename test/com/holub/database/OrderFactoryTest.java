package com.holub.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class OrderFactoryTest {
    OrderFactory.Order order;
    List<OrderFactory.Order> orders;
    String keyName = "key";

    @BeforeEach
    void setUp() {
        orders = new ArrayList<>();
    }

    private int getCompare(String value1, String value2) {
        return OrderFactory.getOrderComparator(orders).compare(Map.of(keyName, value1), Map.of(keyName, value2));
    }

    @Test
    @DisplayName("숫자 column에 대해서 방향이 ASC일 때 정상적인 Compare 결과를 반환해야 한다")
    void numAscTest() {
        order = new OrderFactory.Order(keyName, true);
        orders.add(order);

        Assertions.assertTrue(getCompare("1", "2") < 0);
        Assertions.assertEquals(getCompare("2", "2"), 0);
        Assertions.assertTrue(getCompare("2", "1") > 0);
    }

    @Test
    @DisplayName("숫자 column에 대해서 방향이 DESC일 때 정상적인 Compare 결과를 반환해야 한다")
    void numDescTest() {
        order = new OrderFactory.Order(keyName, false);
        orders.add(order);

        Assertions.assertTrue(getCompare("1" ,"2") > 0);
        Assertions.assertEquals(getCompare("2" ,"2"), 0);
        Assertions.assertTrue(getCompare("2", "1") < 0);
    }

    @Test
    @DisplayName("문자열 column에 대해서 방향이 ASC일 때 정상적인 Compare 결과를 반환해야 한다")
    void strAscTest() {
        order = new OrderFactory.Order(keyName, true);
        orders.add(order);

        Assertions.assertTrue(getCompare("apple" ,"banana") < 0);
        Assertions.assertEquals(getCompare("banana" ,"banana"), 0);
        Assertions.assertTrue(getCompare("banana" ,"apple") > 0);
    }

    @Test
    @DisplayName("문자열 column에 대해서 방향이 DESC일 때 정상적인 Compare 결과를 반환해야 한다")
    void strDescTest() {
        order = new OrderFactory.Order(keyName, false);
        orders.add(order);

        Assertions.assertTrue(getCompare("apple" ,"banana") > 0);
        Assertions.assertEquals(getCompare("banana" ,"banana"), 0);
        Assertions.assertTrue(getCompare("banana" ,"apple") < 0);
    }

    @Test
    @DisplayName("boolean column에 대해서 방향이 ASC일 때 정상적인 Compare 결과를 반환해야 한다")
    void booleanAscTest() {
        order = new OrderFactory.Order(keyName, true);
        orders.add(order);

        Assertions.assertTrue(getCompare("false", "true") < 0);
        Assertions.assertEquals(getCompare("true", "true"), 0);
        Assertions.assertTrue(getCompare("true", "false") > 0);
    }

    @Test
    @DisplayName("boolean column에 대해서 방향이 DESC일 때 정상적인 Compare 결과를 반환해야 한다")
    void booleanDescTest() {
        order = new OrderFactory.Order(keyName, false);
        orders.add(order);

        Assertions.assertTrue(getCompare("false", "true") > 0);
        Assertions.assertEquals(getCompare("true", "true"), 0);
        Assertions.assertTrue(getCompare("true", "false") < 0);
    }

    @Test
    @DisplayName("null column에 대해서 방향이 ASC일 때 정상적인 Compare 결과를 반환해야 한다")
    void nullAscTest() {
        order = new OrderFactory.Order(keyName, true);
        orders.add(order);

        Assertions.assertTrue(getCompare("value", "null") > 0);
        Assertions.assertEquals(getCompare("null", "null") ,0);
        Assertions.assertTrue(getCompare("null", "value") < 0);
    }

    @Test
    @DisplayName("null column에 대해서 방향이 DESC일 때 정상적인 Compare 결과를 반환해야 한다")
    void nullDescTest() {
        order = new OrderFactory.Order(keyName, false);
        orders.add(order);

        Assertions.assertTrue(getCompare("value", "null") < 0);
        Assertions.assertEquals(getCompare("null", "null") ,0);
        Assertions.assertTrue(getCompare("null", "value") > 0);
    }
}