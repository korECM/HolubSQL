package com.holub.database;

import java.util.*;
import java.util.stream.Collectors;

class SelectorHandler implements TableHandler {

    private String[] columns;

    public SelectorHandler(String[] columns) {
        this.columns = columns;
    }

    public SelectorHandler(List<String> columns) {
        if (columns == null) this.columns = null;
        else
            this.columns = columns.toArray(new String[0]);
    }

    public Table handle(Table target) {
        if (columns == null) {
            columns = target.columnNames();
        }

        // aggregate 이름이 중복되는 경우 insert 할 때 오류가 발생하므로 중복 제거
        // ex) MIN(year), MAX(year)
        columns = new LinkedHashSet<>(Arrays.asList(columns)).toArray(new String[0]);

        Table result = TableFactory.create(null, columns);
        List<Map<String, String>> tableMap = TableHelper.tableToMapList(target.rows(), columns);
        for (Map<String, String> row : tableMap) {
            result.insert(row.values());
        }

        return result;
    }
}

class OrderHandler implements TableHandler {

    List<OrderFactory.Order> orderColumns;

    public OrderHandler(List<OrderFactory.Order> orderColumns) {
        this.orderColumns = orderColumns;
    }

    public Table handle(Table target) {
        String[] columns = target.columnNames();
        Table result = TableFactory.create(null, columns);
        // 정렬을 위해 해당 테이블의 값을 List<Map>으로 변경
        List<Map<String, String>> rowList = TableHelper.tableToMapList(target.rows(), columns);

        // ORDER BY 에서 주어졌던 정렬 방법으로 List 정렬
        Collections.sort(rowList, OrderFactory.getOrderComparator(orderColumns));

        for (Map<String, String> row : rowList) {
            result.insert(row.values());
        }
        return result;
    }
}

class DistinctHandler implements TableHandler {

    public DistinctHandler() {
    }

    public Table handle(Table target) {
        Table result = TableFactory.create(null, target.columnNames());

        TableHelper.tableToMapList(target)
                .stream().distinct()
                .forEach(row -> result.insert(row.values()));
        return result;
    }
}

class AggregateHandler implements TableHandler{

    private List<String> aggregateColumnList;

    public AggregateHandler(List<String> aggregateColumnList){
        this.aggregateColumnList = aggregateColumnList;
    }

    public Table handle(Table target) {
        // aggregate 이름을 포함하는 테이블 생성
        Table result = TableFactory.create(null, aggregateColumnList.toArray(new String[0]));
        // aggregate 처리를 위한 Visitor 생성
        List<Visitor> visitorList =
                aggregateColumnList.stream()
                        .map(VisitorFactory::getVisitor)
                        .collect(Collectors.toList());

        Cursor c = target.rows();
        while (c.advance()) {
            for (Visitor visitor : visitorList) {
                c.accept(visitor);
            }
        }

        // Visit 결과를 테이블에 삽입
        result.insert(
                visitorList.stream()
                        .map(Visitor::getValue)
                        .collect(Collectors.toList()));
        return result;
    }
}

public class HandlerBuilder {

    private List<TableHandler> handlers = new ArrayList<>();
    private SelectorHandler selectorHandler;
    private OrderHandler orderHandler;
    private DistinctHandler distinctHandler;
    private AggregateHandler aggregateHandler;

    public HandlerBuilder distinct() {
        this.distinctHandler = new DistinctHandler();
        return this;
    }

    public HandlerBuilder order(List<OrderFactory.Order> orderColumns) {
        this.orderHandler = new OrderHandler(orderColumns);
        return this;
    }

    public HandlerBuilder selectColumns(String[] columns) {
        selectorHandler = new SelectorHandler(columns);
        return this;
    }

    public HandlerBuilder selectColumns(List<String> columns) {
        selectorHandler = new SelectorHandler(columns);
        return this;
    }

    public HandlerBuilder aggregate(List<String> aggregateColumnList){
        aggregateHandler = new AggregateHandler(aggregateColumnList);
        return this;
    }

    public List<TableHandler> getHandlerList() {
        handlers.clear();
        handlers.add(orderHandler);
        handlers.add(selectorHandler);
        handlers.add(distinctHandler);
        handlers.add(aggregateHandler);
        return handlers;
    }

    public TableHandler[] getHandlerArray() {
        return getHandlerList().toArray(new TableHandler[0]);
    }
}
