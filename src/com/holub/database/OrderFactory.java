package com.holub.database;

import java.util.*;
import java.util.regex.Pattern;

// 주어진 column 값 비교를 할 수 있는 인터페이스
interface OrderStrategy {
    boolean canHandle(String column);

    int compare(String column1, String column2);
}

class BooleanOrder implements OrderStrategy {

    @Override
    public boolean canHandle(String column) {
        return column.equals("false") || column.equals("true");
    }

    @Override
    public int compare(String column1, String column2) {
        // true : 1
        // false : 0
        // 으로 치환해서 순서 결정
        return (column2.length() - column1.length());
    }
}

class NumberOrder implements OrderStrategy {

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Override
    public boolean canHandle(String column) {
        return pattern.matcher(column).matches();
    }

    @Override
    public int compare(String column1, String column2) {
        return (int) (strToDouble(column1) - strToDouble(column2));
    }

    private double strToDouble(String str) {
        return Double.parseDouble(str);
    }
}

class StringOrder implements OrderStrategy {

    @Override
    public boolean canHandle(String column) {
        return true;
    }

    @Override
    public int compare(String column1, String column2) {
        return column1.compareTo(column2);
    }
}

public class OrderFactory {

    // 순서를 판단하는 Responsibility를 가진 객체들의 Chain
    private static List<OrderStrategy> orderStrategyList = new ArrayList<>();

    static {
        orderStrategyList.add(new BooleanOrder());
        orderStrategyList.add(new NumberOrder());
        orderStrategyList.add(new StringOrder());
    }

    private OrderFactory() {
    }

    static class Order {
        String columnName;
        Boolean isAsc;

        public Order(String columnName, Boolean isAsc) {
            this.columnName = columnName;
            this.isAsc = isAsc;
        }
    }

    // 주어진 Order에 맞는 Comparator를 반환하는 함수
    static public Comparator<Map<String, String>> getOrderComparator(List<Order> orderColumn) {
        return new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                Iterator<Order> it = orderColumn.iterator();
                while (it.hasNext()) {
                    Order order = it.next();
                    String column1 = o1.get(order.columnName);
                    String column2 = o2.get(order.columnName);
                    if (column1.equals(column2)) continue;

                    Iterator<OrderStrategy> chain = orderStrategyList.iterator();

                    while (chain.hasNext()) {
                        OrderStrategy strategy = chain.next();
                        if (strategy.canHandle(column1) && strategy.canHandle(column2))
                            return strategy.compare(column1, column2) * orderDirectionToNumber(order);
                    }
                }
                // 주어진 모든 조건에 대해 두 값이 같은 경우 같은 row라고 간주
                return 0;
            }
        };
    }

    // 정렬 방향 결정해주는 함수
    private static int orderDirectionToNumber(Order order) {
        return order.isAsc ? 1 : -1;
    }
}
