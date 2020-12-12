package com.holub.database;

import java.util.List;

public class JoinTable extends ConcreteTable{

    List<Table> otherTableList;

    public JoinTable(String tableName, String[] columnNames, List<Table> otherTableList) {
        super(tableName, columnNames);
        assert otherTableList != null : "JoinTable's otherTableList should not be null";
        this.otherTableList = otherTableList;
    }

    @Override
    public boolean isMadeOf(Table t){
        return otherTableList.stream().anyMatch(table -> table.isMadeOf(t));
    }

}
