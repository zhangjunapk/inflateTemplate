package parseXml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WordUitls {
    public WordUitls() {
    }

    public static Document getDoc(ZipFile docxFile) throws Exception {
        ZipEntry documentXML = docxFile.getEntry("word/document.xml");
        InputStream documentXMLIS = docxFile.getInputStream(documentXML);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().parse(documentXMLIS);
        return doc;
    }

    public static void setDoOUt(ZipFile docxFile, Node doc, File destSavePath) throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        t.transform(new DOMSource(doc), new StreamResult(baos));
        ZipOutputStream docxOutFile = new ZipOutputStream(new FileOutputStream(destSavePath));
        Enumeration entriesIter = docxFile.entries();

        while(entriesIter.hasMoreElements()) {
            ZipEntry entry = (ZipEntry)entriesIter.nextElement();
            if (entry.getName().equals("word/document.xml")) {
                byte[] data = baos.toByteArray();
                docxOutFile.putNextEntry(new ZipEntry(entry.getName()));
                docxOutFile.write(data, 0, data.length);
                docxOutFile.closeEntry();
            } else {
                InputStream incoming = docxFile.getInputStream(entry);
                byte[] data = new byte[524288];
                int readCount = incoming.read(data, 0, (int)entry.getSize());
                if(readCount!=-1){
                    docxOutFile.putNextEntry(new ZipEntry(entry.getName()));
                    docxOutFile.write(data, 0, readCount);
                    docxOutFile.closeEntry();
                }

            }
        }

        docxOutFile.close();
    }

    public static Element recursionFindNextNode(Element oldBookStart, String bookName) {
        Element node = (Element)oldBookStart.getNextSibling();
        if ("w:bookmarkEnd".equals(node.getNodeName())) {
            return oldBookStart;
        } else {
            if (!bookName.endsWith(node.getNodeName())) {
                recursionFindNextNode(node, bookName);
            }

            return node;
        }
    }

    public static void recursionFindNextNode(Element oldBookStart, StringBuffer sb) {
        Element node = (Element)oldBookStart.getNextSibling();
        if (node != null) {
            sb.append(node.getTextContent());
            if ("w:bookmarkEnd".equals(node.getNodeName())) {
                return;
            }

            recursionFindNextNode(node, sb);
        } else {
            Element parentNode = (Element)oldBookStart.getParentNode();
            recursionPFindNextNode(parentNode, sb);
        }

    }

    private static void recursionPFindNextNode(Element parentNode, StringBuffer sb) {
        Element pnode = (Element)parentNode.getNextSibling();
        if ("w:p".equals(pnode.getNodeName())) {
            if (pnode.getElementsByTagName("w:bookmarkEnd").getLength() > 0) {
                sb.append(pnode.getTextContent());
            } else {
                sb.append(pnode.getTextContent());
                recursionPFindNextNode(pnode, sb);
            }
        }
    }
}
