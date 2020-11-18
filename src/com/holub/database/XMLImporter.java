package com.holub.database;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLImporter implements Table.Importer {

    private final BufferedReader in;
    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private final DocumentBuilder dBuilder;
    private Document doc = null;

    private String tableName;
    private List columnName = new ArrayList<String>();
    private List dataNodeList = new ArrayList<NodeList>();
    private Iterator<NodeList> dataNodeListIterator;

    public XMLImporter(Reader in) {
        this.in = in instanceof BufferedReader
                ? (BufferedReader) in
                : new BufferedReader(in)
        ;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Error("Can not Initialize DocumentBuilder");
        }
    }

    @Override
    public void startTable() throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        try {
            doc = dBuilder.parse(new ByteArrayInputStream(sb.toString().getBytes()));
            Element root = doc.getDocumentElement();
            tableName = root.getTagName();

            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    if (nodeName == "column") {
                        NodeList dataList = node.getChildNodes();
                        for (int j = 0; j < dataList.getLength(); j++) {
                            Node valueNode = dataList.item(j);
                            if (valueNode.getNodeName() == "name") {
                                columnName.add(valueNode.getTextContent());
                            }
                        }
                    } else if (nodeName == "data") {
                        NodeList dataList = node.getChildNodes();
                        if (dataList.getLength() > 0) dataNodeList.add(dataList);
                    }
                }
            }
            dataNodeListIterator = dataNodeList.iterator();
        } catch (SAXException e) {
            throw new Error("Can not Parse Data");
        }

    }

    @Override
    public String loadTableName() throws IOException {
        return tableName;
    }

    @Override
    public int loadWidth() throws IOException {
        return columnName.size();
    }

    @Override
    public Iterator loadColumnNames() throws IOException {
        return columnName.iterator();
    }

    @Override
    public Iterator loadRow() throws IOException {
        if (dataNodeListIterator == null || dataNodeListIterator.hasNext() == false) return null;
        List row = new ArrayList();
        if (dataNodeListIterator.hasNext()) {
            NodeList dataList = dataNodeListIterator.next();
            for (int i = 0; i < dataList.getLength(); i++) {
                Node dataNode = dataList.item(i);
                row.add(dataNode.getTextContent());
            }
        }
        return row.iterator();
    }

    @Override
    public void endTable() throws IOException {

    }
}
