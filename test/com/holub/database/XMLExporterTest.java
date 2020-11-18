package com.holub.database;

import com.holub.util.WriterMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class XMLExporterTest {

    XMLExporter exporter;
    WriterMock writer;

    @BeforeEach
    void setUp() {
        writer = new WriterMock();
        exporter = new XMLExporter(writer);
    }

    @Test
    void startTable() {
        String resultString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        try {
            exporter.startTable();
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }

    @Test
    @DisplayName("storeMetadataWhenTableNameIsNull")
    void storeMetadataWhenTableNameIsNull() {
        String resultString = "<anonymous>" +
                "<column>" +
                "<name>name</name>" +
                "<name>age</name>" +
                "<name>money</name>" +
                "</column>";
        try {
            List columnNames = new ArrayList<String>();
            columnNames.add("name");
            columnNames.add("age");
            columnNames.add("money");
            exporter.storeMetadata(null, 3, 0, columnNames.iterator());
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }

    @Test
    @DisplayName("storeMetadataWhenTableNameIsNotNull")
    void storeMetadataWhenTableNameIsNotNull() {
        String resultString = "<sampletable>" +
                "<column>" +
                "<name>name</name>" +
                "<name>age</name>" +
                "<name>money</name>" +
                "</column>";
        try {
            List columnNames = new ArrayList<String>();
            columnNames.add("name");
            columnNames.add("age");
            columnNames.add("money");
            exporter.storeMetadata("sampletable", 3, 0, columnNames.iterator());
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }

    @Test
    void storeRow() {
        String resultString = "<data>" +
                "<value>sampleName</value>" +
                "<value>sampleAge</value>" +
                "<value>sampleMoney</value>" +
                "</data>";
        List data = new ArrayList();
        data.add("sampleName");
        data.add("sampleAge");
        data.add("sampleMoney");
        try {
            exporter.storeRow(data.iterator());
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }

    @Test
    @DisplayName("endTableWhenTableNameIsNull")
    void endTableWhenTableNameIsNull() {
        String resultString = "</anonymous>";
        try {
            List columnNames = new ArrayList<String>();
            exporter.storeMetadata(null, 0, 0, columnNames.iterator());
            exporter.endTable();
            Assertions.assertTrue(writer.getOutput().contains(resultString));
        } catch (IOException e) {
        }
    }

    @Test
    @DisplayName("endTableWhenTableNameIsNotNull")
    void endTableWhenTableNameIsNotNull() {
        String resultString = "</sampletable>";
        try {
            List columnNames = new ArrayList<String>();
            exporter.storeMetadata("sampletable", 0, 0, columnNames.iterator());
            exporter.endTable();
            Assertions.assertTrue(writer.getOutput().contains(resultString));
        } catch (IOException e) {
        }
    }


    @Test
    void integrationTest() {
        String resultString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<sampletable>" +
                "<column>" +
                "<name>name</name>" +
                "<name>age</name>" +
                "<name>money</name>" +
                "</column>" +
                "<data>" +
                "<value>sampleName</value>" +
                "<value>sampleAge</value>" +
                "<value>sampleValue</value>" +
                "</data>" +
                "</sampletable>";
        try {
            Table sampleTable = TableFactory.create("sampletable", new String[]{"name", "age", "money"});
            sampleTable.insert(new Object[]{"sampleName", "sampleAge", "sampleValue"});
            sampleTable.export(new XMLExporter(writer));
            Assertions.assertEquals(resultString, writer.getOutput());
        } catch (IOException e) {
        }
    }
}