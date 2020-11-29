package com.holub.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

class ImporterFactoryTest {

    @Test
    @DisplayName("확장자가 xml로 끝나면 XML Importer를 반환한다")
    void getXMLImportTest() throws IOException {
        Assertions.assertTrue(ImporterFactory.getImporter("asdf.asdf.xml", new StringReader("")) instanceof XMLImporter);
        Assertions.assertTrue(ImporterFactory.getImporter("asdf.asdf.XML", new StringReader("")) instanceof XMLImporter);
    }

    @Test
    @DisplayName("확장자가 csv로 끝나면 XML Importer를 반환한다")
    void getCSVImportTest() throws IOException {
        Assertions.assertTrue(ImporterFactory.getImporter("asdf.asdf.csv", new StringReader("")) instanceof CSVImporter);
        Assertions.assertTrue(ImporterFactory.getImporter("asdf.asdf.CSV", new StringReader("")) instanceof CSVImporter);
    }

    @Test
    @DisplayName("확장자와 일치하는 Importer가 없다면 IOException을 던진다")
    void testNoImporterAvailable() {
        Assertions.assertThrows(IOException.class, () -> ImporterFactory.getImporter("asdf.asdf.asdf", new StringReader("")));
        Assertions.assertThrows(IOException.class, () -> ImporterFactory.getImporter("asdf", new StringReader("")));
        Assertions.assertThrows(IOException.class, () -> ImporterFactory.getImporter("asdf.", new StringReader("")));
    }
}