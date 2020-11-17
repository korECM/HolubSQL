package com.holub.database;

import com.holub.util.WriterMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class HTMLExporterTest {

    HTMLExporter exporter;
    WriterMock writer;

    @BeforeEach
    void setUp() {
        writer = new WriterMock();
        exporter = new HTMLExporter(writer);
    }

    @Test
    void startTable() {
        String resultString = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head></head>\n" +
                "<body>\n";
        try {
            exporter.startTable();
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }

    }

    @Test
    @DisplayName("tableName is Null")
    void storeMetadataWhenTableNameIsNull() {
        try {
            exporter.storeMetadata(null, 0, 0, new ArrayList().iterator());
            Assertions.assertTrue(writer.getOutput().contains("<h1>anonymous</h1>"));
        } catch (IOException e) {
        }
    }

    @Test
    @DisplayName("tableName is not Null")
    void storeMetadataWhenTableNameIsNotNull() {
        String resultString = "<h1>SampleTable</h1>\n" +
                "<table>\n" +
                "<thead>\n" +
                "<tr>\n" +
                "<th>A</th>\n" +
                "<th>B</th>\n" +
                "<th>C</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody>\n";
        try {
            List columnNames = new ArrayList();
            columnNames.add("A");
            columnNames.add("B");
            columnNames.add("C");
            exporter.storeMetadata("SampleTable", 3, 0, columnNames.iterator());
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }

    @Test
    void storeRow() {
        String resultString = "<tr>\n" +
                "<td>DataA</td>\n" +
                "<td>DataB</td>\n" +
                "<td>DataC</td>\n" +
                "</tr>\n";
        try {
            List columnNames = new ArrayList();
            columnNames.add("DataA");
            columnNames.add("DataB");
            columnNames.add("DataC");
            exporter.storeRow(columnNames.iterator());
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }

    @Test
    void endTable() {
        String resultString = "</tbody>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>\n";
        try {
            exporter.endTable();
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }

    @Test
    void integrationTest() {
        Table tb = TableFactory.create("SampleTable", new String[]{"A", "B", "C"});
        for (int i = 1; i <= 5; i++) {
            tb.insert(new String[]{"Data" + i + "A", "Data" + i + "B", "Data" + i + "C"});

        }

        String resultString = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head></head>\n" +
                "<body>\n" +
                "<h1>SampleTable</h1>\n" +
                "<table>\n" +
                "<thead>\n" +
                "<tr>\n" +
                "<th>A</th>\n" +
                "<th>B</th>\n" +
                "<th>C</th>\n" +
                "</tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>Data1A</td>\n" +
                "<td>Data1B</td>\n" +
                "<td>Data1C</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>Data2A</td>\n" +
                "<td>Data2B</td>\n" +
                "<td>Data2C</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>Data3A</td>\n" +
                "<td>Data3B</td>\n" +
                "<td>Data3C</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>Data4A</td>\n" +
                "<td>Data4B</td>\n" +
                "<td>Data4C</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>Data5A</td>\n" +
                "<td>Data5B</td>\n" +
                "<td>Data5C</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>\n";
        try {
            WriterMock w = new WriterMock();
            tb.export(new HTMLExporter(w));
            Assertions.assertEquals(resultString, w.getOutput());
        } catch (IOException e) {
        }
    }
}