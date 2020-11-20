package com.holub.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class OrderFactoryTest {
    OrderFactory.Order order;
    List<OrderFactory.Order> orders;
    Map<String, String> value1;
    Map<String, String> value2;

    @BeforeEach
    void setUp() {
        value1 = new LinkedHashMap<>();
        value2 = new LinkedHashMap<>();
        orders = new ArrayList();

    }

    private int getCompare() {
        return OrderFactory.getOrderComparator(orders).compare(value1, value2);
    }

    @Test
    void numAscTest() {
        order = new OrderFactory.Order("key", true);
        orders.add(order);

        value1.put("key", "1");
        value2.put("key", "2");

        Assertions.assertTrue(getCompare() < 0);

        value1.put("key", "2");
        value2.put("key", "2");

        Assertions.assertTrue(getCompare() == 0);

        value1.put("key", "2");
        value2.put("key", "1");

        Assertions.assertTrue(getCompare() > 0);
    }

    @Test
    void numDescTest() {
        order = new OrderFactory.Order("key", false);
        orders.add(order);

        value1.put("key", "1");
        value2.put("key", "2");

        Assertions.assertTrue(getCompare() > 0);

        value1.put("key", "2");
        value2.put("key", "2");

        Assertions.assertTrue(getCompare() == 0);

        value1.put("key", "2");
        value2.put("key", "1");

        Assertions.assertTrue(getCompare() < 0);
    }

    @Test
    void strAscTest() {
        order = new OrderFactory.Order("key", true);
        orders.add(order);

        value1.put("key", "apple");
        value2.put("key", "banana");

        Assertions.assertTrue(getCompare() < 0);

        value1.put("key", "banana");
        value2.put("key", "banana");

        Assertions.assertTrue(getCompare() == 0);

        value1.put("key", "banana");
        value2.put("key", "apple");

        Assertions.assertTrue(getCompare() > 0);
    }

    @Test
    void strDescTest() {
        order = new OrderFactory.Order("key", false);
        orders.add(order);

        value1.put("key", "apple");
        value2.put("key", "banana");

        Assertions.assertTrue(getCompare() > 0);

        value1.put("key", "banana");
        value2.put("key", "banana");

        Assertions.assertTrue(getCompare() == 0);

        value1.put("key", "banana");
        value2.put("key", "apple");

        Assertions.assertTrue(getCompare() < 0);
    }

    @Test
    void booleanAscTest() {
        order = new OrderFactory.Order("key", true);
        orders.add(order);

        value1.put("key", "false");
        value2.put("key", "true");

        Assertions.assertTrue(getCompare() < 0);

        value1.put("key", "true");
        value2.put("key", "true");

        Assertions.assertTrue(getCompare() == 0);

        value1.put("key", "true");
        value2.put("key", "false");

        Assertions.assertTrue(getCompare() > 0);
    }

    @Test
    void booleanDescTest() {
        order = new OrderFactory.Order("key", false);
        orders.add(order);

        value1.put("key", "false");
        value2.put("key", "true");

        Assertions.assertTrue(getCompare() > 0);

        value1.put("key", "true");
        value2.put("key", "true");

        Assertions.assertTrue(getCompare() == 0);

        value1.put("key", "true");
        value2.put("key", "false");

        Assertions.assertTrue(getCompare() < 0);
    }
}