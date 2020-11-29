package com.holub.database;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class ImporterFactory {

    private interface ReturnImporter {
        Table.Importer createImporter(Reader in) throws IOException;
    }

    private static final Map<String, ReturnImporter> importers = new HashMap<>();

    static {
        importers.put("csv", CSVImporter::new);
        importers.put("xml", XMLImporter::new);
    }

    private ImporterFactory() {
    }

    private static class NullImporter implements Table.Importer {
        public NullImporter(Reader in) throws IOException {
            throw new java.io.IOException(
                    "Filename does not end in "
                            + "supported extension (.csv, .xml)");
        }

        public void startTable() {
        }

        public String loadTableName() {
            return null;
        }

        public int loadWidth() {
            return 0;
        }

        public Iterator<String> loadColumnNames() {
            return null;
        }

        public Iterator<String> loadRow() {
            return null;
        }

        public void endTable() {
        }
    }

    public static Table.Importer getImporter(String fileName, Reader in) throws IOException {
        String[] args = fileName.toLowerCase().split("\\.");
        return importers.getOrDefault(args[args.length - 1], NullImporter::new).createImporter(in);
    }

}
