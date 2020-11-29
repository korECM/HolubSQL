package com.holub.database;

import com.holub.util.MapListHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class XMLImporterTest {

    Reader in;
    XMLImporter importer;

    private final String resultString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<sampletable>" +
            "<column>" +
            "<name>name</name>" +
            "<name>age</name>" +
            "<name>money</name>" +
            "</column>" +
            "<data>" +
            "<value>sampleName</value>" +
            "<value>sampleAge</value>" +
            "<value>sampleMoney</value>" +
            "</data>" +
            "<data>" +
            "<value>sampleName2</value>" +
            "<value>sampleAge2</value>" +
            "<value>sampleMoney2</value>" +
            "</data>" +
            "</sampletable>";

    @BeforeEach
    void setUp() throws IOException {
        in = new BufferedReader(new StringReader(resultString));
        importer = new XMLImporter(in);
        importer.startTable();
    }


    @Test
    void loadTableName() {
        Assertions.assertEquals(importer.loadTableName(), "sampletable");
    }

    @Test
    void loadWidth() {
        Assertions.assertEquals(importer.loadWidth(), 3);
    }

    @Test
    void loadColumnNames() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = importer.loadColumnNames();
        while (it.hasNext()) {
            String item = it.next();
            sb.append(item);
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        Assertions.assertEquals(sb.toString(), "name,age,money");
    }

    @Test
    void loadRow() {
        Iterator<String> it = importer.loadRow();
        List<String> data = new ArrayList<>();
        while (it.hasNext()) {
            data.add(it.next());
        }
        Assertions.assertArrayEquals(data.toArray(), new Object[]{"sampleName", "sampleAge", "sampleMoney"});
        it = importer.loadRow();
        data = new ArrayList<>();
        while (it.hasNext()) {
            data.add(it.next());
        }
        Assertions.assertArrayEquals(data.toArray(), new Object[]{"sampleName2", "sampleAge2", "sampleMoney2"});
    }


    @Test
    void integrationTest() throws IOException {
        Table tb = TableFactory.create(new XMLImporter(new StringReader(resultString)));
        Assertions.assertEquals(tb.name(), "sampletable");
        Cursor c = tb.rows();
        c.advance();
        Assertions.assertEquals(c.columnCount(), 3);
        Assertions.assertEquals(c.column("name").toString(), "sampleName");
        Assertions.assertEquals(c.column("age").toString(), "sampleAge");
        Assertions.assertEquals(c.column("money").toString(), "sampleMoney");

        c.advance();
        Assertions.assertEquals(c.columnCount(), 3);
        Assertions.assertEquals(c.column("name").toString(), "sampleName2");
        Assertions.assertEquals(c.column("age").toString(), "sampleAge2");
        Assertions.assertEquals(c.column("money").toString(), "sampleMoney2");

    }

    @Test
    void tableFactoryTest() throws IOException {
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><sampletable><column><name>name</name><name>age" +
                "</name><name>money</name></column><data><value>sampleName</value><value>sampleAge</value><value" +
                ">sampleValue</value></data></sampletable>";
        Table tb = TableFactory.load("test.xml", new StringReader(xmlString));
        MapListHelper mapListHelper = new MapListHelper();
        mapListHelper.setColumnName("name", "age", "money");
        mapListHelper.addRow("sampleName", "sampleAge", "sampleValue");
        Assertions.assertTrue(mapListHelper.verify(tb));
    }

}