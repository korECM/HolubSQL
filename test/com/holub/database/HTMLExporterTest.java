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
        String resultString = "<!DOCTYPE html>" +
                "<html>" +
                "<head></head>" +
                "<body>";
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
        String resultString = "<h1>SampleTable</h1>" +
                "<table>" +
                "<thead>" +
                "<tr>" +
                "<th>A</th>" +
                "<th>B</th>" +
                "<th>C</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>";
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
        String resultString = "<tr>" +
                "<td>DataA</td>" +
                "<td>DataB</td>" +
                "<td>DataC</td>" +
                "</tr>";
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
        String resultString = "</tbody>" +
                "</table>" +
                "</body>" +
                "</html>";
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

        String resultString = "<!DOCTYPE html>" +
                "<html>" +
                "<head></head>" +
                "<body>" +
                "<h1>SampleTable</h1>" +
                "<table>" +
                "<thead>" +
                "<tr>" +
                "<th>A</th>" +
                "<th>B</th>" +
                "<th>C</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                "<tr>" +
                "<td>Data1A</td>" +
                "<td>Data1B</td>" +
                "<td>Data1C</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Data2A</td>" +
                "<td>Data2B</td>" +
                "<td>Data2C</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Data3A</td>" +
                "<td>Data3B</td>" +
                "<td>Data3C</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Data4A</td>" +
                "<td>Data4B</td>" +
                "<td>Data4C</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Data5A</td>" +
                "<td>Data5B</td>" +
                "<td>Data5C</td>" +
                "</tr>" +
                "</tbody>" +
                "</table>" +
                "</body>" +
                "</html>";
        try {
            WriterMock w = new WriterMock();
            tb.export(new HTMLExporter(w));
            Assertions.assertEquals(resultString, w.getOutput());
        } catch (IOException e) {
        }
    }
}