package com.holub.database;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class XMLExporter implements Table.Exporter {

    private final Writer out;
    private String tableName;

    public XMLExporter(Writer out) {
        this.out = out;
    }

    @Override
    public void startTable() throws IOException {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    @Override
    public void storeMetadata(String tableName, int width, int height, Iterator<String> columnNames) throws IOException {
        this.tableName = tableName;
        out.write(String.format("<%s>", tableName == null ? "anonymous" : tableName));
        out.write("<column>");
        while (columnNames.hasNext()) {
            out.write(String.format("<name>%s</name>", columnNames.next()));
        }
        out.write("</column>");
    }

    @Override
    public void storeRow(Iterator<String> data) throws IOException {
        out.write("<data>");
        while (data.hasNext()) {
            out.write(String.format("<value>%s</value>", data.next()));
        }
        out.write("</data>");
    }

    @Override
    public void endTable() throws IOException {
        out.write(String.format("</%s>", tableName == null ? "anonymous" : tableName));

    }
}
