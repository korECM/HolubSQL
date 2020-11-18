package com.holub.database;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class HTMLExporter implements Table.Exporter {

    private final Writer out;
    private int width;

    public HTMLExporter(Writer out) {
        this.out = out;
    }

    @Override
    public void startTable() throws IOException {
        out.write("<!DOCTYPE html>");
        out.write("<html>");
        out.write("<head></head>");
        out.write("<body>");
    }

    @Override
    public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
        out.write("<h1>" + (tableName == null ? "anonymous" : tableName) + "</h1>");
        out.write("<table>");
        out.write("<thead>");
        out.write("<tr>");
        while (columnNames.hasNext()) {
            out.write("<th>");
            out.write(columnNames.next().toString());
            out.write("</th>");
        }
        out.write("</tr>");
        out.write("</thead>");
        out.write("<tbody>");
    }

    @Override
    public void storeRow(Iterator data) throws IOException {
        out.write("<tr>");
        while (data.hasNext()) {
            out.write("<td>");
            out.write(data.next().toString());
            out.write("</td>");
        }
        out.write("</tr>");
    }

    @Override
    public void endTable() throws IOException {
        out.write("</tbody>");
        out.write("</table>");
        out.write("</body>");
        out.write("</html>");
    }
}