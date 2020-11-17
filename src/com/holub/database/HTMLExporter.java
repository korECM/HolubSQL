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
        out.write("<!DOCTYPE html>\n");
        out.write("<html>\n");
        out.write("<head></head>\n");
        out.write("<body>\n");
    }

    @Override
    public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
        out.write("<h1>" + (tableName == null ? "anonymous" : tableName) + "</h1>\n");
        out.write("<table>\n");
        out.write("<thead>\n");
        out.write("<tr>\n");
        while (columnNames.hasNext()) {
            out.write("<th>");
            out.write(columnNames.next().toString());
            out.write("</th>\n");
        }
        out.write("</tr>\n");
        out.write("</thead>\n");
        out.write("<tbody>\n");
    }

    @Override
    public void storeRow(Iterator data) throws IOException {
        out.write("<tr>\n");
        while (data.hasNext()) {
            out.write("<td>");
            out.write(data.next().toString());
            out.write("</td>\n");
        }
        out.write("</tr>\n");
    }

    @Override
    public void endTable() throws IOException {
        out.write("</tbody>\n");
        out.write("</table>\n");
        out.write("</body>\n");
        out.write("</html>\n");
    }
}