package com.holub.util;

import com.holub.database.Table;
import com.holub.database.TableHelper;

import java.util.*;

public class MapListHelper {
    private List<Map<String, String>> listMap;
    private List<String> columnNameList;
    private boolean printReason = true;

    public MapListHelper(){
        listMap = new ArrayList<>();
    }

    public void setPrintReason(boolean printReason){
        this.printReason = printReason;
    }

    public void setColumnName(String...dataArray){
        this.columnNameList = Arrays.asList(dataArray);
    }

    public void addRow(String...dataArray){
        assert columnNameList.size() == dataArray.length : "size mismatch";
        Map<String, String> row = new HashMap<>();

        Iterator<String> columnNameIterator = columnNameList.iterator();

        for(String data : dataArray){
            row.put(columnNameIterator.next(), data);
        }

        listMap.add(row);
    }

    public void clear(){
        columnNameList = new ArrayList<>();
        listMap = new ArrayList<>();
    }

    public boolean verify(Table t){
        List<Map<String, String>> tableMap = TableHelper.tableToMapList(t);
        boolean result = tableMap.equals(listMap);
        if(!result && printReason){
            System.out.println("expected");
            System.out.println(listMap.toString());
            System.out.println("received");
            System.out.println(tableMap.toString());
        }
        return result;
    }
}
