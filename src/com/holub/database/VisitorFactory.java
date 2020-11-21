package com.holub.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MaxVisitor extends Visitor {

    public MaxVisitor(String columnName) {
        super(columnName);
    }

    @Override
    Double processValue(Double orgValue, Double newValue) {
        return Math.max(orgValue, newValue);
    }
}

class MinVisitor extends Visitor {

    public MinVisitor(String columnName) {
        super(columnName);
    }

    @Override
    Double processValue(Double orgValue, Double newValue) {
        return Math.min(orgValue, newValue);
    }
}

class AvgVisitor extends Visitor {
    public AvgVisitor(String columnName) {
        super(columnName);
    }

    @Override
    Double processValue(Double orgValue, Double newValue) {
        return orgValue + newValue;
    }

    @Override
    protected Double beforeReturnSetValue(Double value) {
        return value / columnCount;
    }
}

// 일치하는 aggregate 함수가 없는 경우를 처리하기 위한 Visitor
// null을 반환한다
// 실제 일치하는 Visitor가 없는 경우 regex에서 걸러지지만
// 아래 Factory 분기 처리를 위해 아래 클래스 사용
class EmptyVisitor extends Visitor {

    public EmptyVisitor(String columnName) {
        super(columnName);
    }

    @Override
    Double processValue(Double orgValue, Double newValue) {
        return null;
    }
}

public class VisitorFactory {

    private static Pattern aggregatePattern = Pattern.compile("(min|max|avg)\\(([a-zA-Z_0-9/\\\\:~]+)\\)");

    private VisitorFactory() {
    }

    public static Visitor getVisitor(String columnName) {
        Matcher m = aggregatePattern.matcher(columnName.toLowerCase());
        m.find();

        switch (m.group(1)) {
            case "min":
                return new MinVisitor(m.group(2));
            case "max":
                return new MaxVisitor(m.group(2));
            case "avg":
                return new AvgVisitor(m.group(2));
            default:
                return new EmptyVisitor(m.group(2));
        }
    }
}

