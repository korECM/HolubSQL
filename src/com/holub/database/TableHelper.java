package com.holub.database;

import java.util.*;

public class TableHelper {

    public static List<Map<String, String>> tableToMapList(Table t){
        return tableToMapList(t.rows(), t.columnNames());
    }

    public static List<Map<String, String>> tableToMapList(Cursor c, List<String> columns) {
        List<Map<String, String>> rows = new ArrayList<>();
        while (c.advance()) {
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 0; i < c.columnCount(); i++) {
                String columnName = c.columnName(i);
                if (columns == null || columns.contains(columnName))
                    map.put(columnName, c.column(columnName).toString());
            }
            rows.add(map);
        }
        return rows;
    }

    public static List<Map<String, String>> tableToMapList(Cursor c, String[] columns) {
        return tableToMapList(c, columns == null ? null : Arrays.asList(columns));
    }
}
