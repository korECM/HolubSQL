package com.holub.database;

import java.util.regex.Pattern;

public abstract class Visitor {

    private Double value = null;
    private String name;
    protected int columnCount = 0;
    private Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?$");

    public Visitor(String columnName) {
        this.name = columnName;
    }

    // row를 방문하는 함수
    final public void visitRow(Cursor c) {
        Double data = columnToDouble(c);
        columnCount++;
        if (value == null) value = data;
        else value = processValue(value, data);
    }

    // row에서 읽은 데이터를 조작하는 경우 사용하는 template
    abstract Double processValue(Double orgValue, Double newValue);

    // 문자열을 데이터로 바꾸는 함수
    final private Double columnToDouble(Cursor c) {
        String data = c.column(name).toString();
        if (numberPattern.matcher(data).matches()) {
            // 숫자인 경우
            return Double.parseDouble(data);
        } else {
            // 문자열 -> 길이를 크기로 생각
            return (double) data.length();
        }
    }

    // 모든 row를 방문한 후 결과를 얻는 함수
    final public String getValue() {
        if (value == null) return "null";
        value = beforeReturnSetValue(value);
        if (value.toString().contains(".")) {
            return value.intValue() + "";
        }
        return value.toString();
    }

    // 결과를 얻기 전에 처리하는 내용이 있다면 담기는 함수
    protected Double beforeReturnSetValue(Double value) {
        return value;
    }
}
