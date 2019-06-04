import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;


public class SimpleWordNamespaceContext implements NamespaceContext {
    private final Map<String, String> PREF_MAP = new HashMap<String, String>();
  
    public SimpleWordNamespaceContext(final Map<String, String> prefMap) {  
        //PREF_MAP.putAll(prefMap);
    }  
  
    public String getNamespaceURI(String prefix) {  
        return PREF_MAP.get(prefix);  
    }  
  
    public String getPrefix(String uri) {  
        throw new UnsupportedOperationException();  
    }  
  
    public Iterator getPrefixes(String uri) {
        throw new UnsupportedOperationException();  
    }


       public void replaceValueByXPath(String filePath,
                                       Map<String, String> paramMap) throws Exception {
           DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
           dbf.setNamespaceAware(true);
           DocumentBuilder builder = dbf.newDocumentBuilder();
           Document doc = builder
                   .parse(new FileInputStream(new File(filePath)));
           XPathFactory factory = XPathFactory.newInstance();
           XPath xpath = factory.newXPath();
           HashMap<String, String> prefMap = new HashMap<String, String>();
           prefMap.put("ve","http://schemas.openxmlformats.org/markup-compatibility/2006");
           prefMap.put("o", "urn:schemas-microsoft-com:office:office");
           prefMap.put("r","http://schemas.openxmlformats.org/officeDocument/2006/relationships");
           prefMap.put("m","http://schemas.openxmlformats.org/officeDocument/2006/math");
           prefMap.put("v", "urn:schemas-microsoft-com:vml");
           prefMap.put("wp","http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing");
           prefMap.put("w10", "urn:schemas-microsoft-com:office:word");
           prefMap.put("w","http://schemas.openxmlformats.org/wordprocessingml/2006/main");
           prefMap.put("wne","http://schemas.microsoft.com/office/word/2006/wordml");
           SimpleWordNamespaceContext context = new SimpleWordNamespaceContext(prefMap);
           xpath.setNamespaceContext(context);
           XPathExpression expr = xpath.compile("//w:t");
           NodeList resultNodeList = (NodeList) expr.evaluate(doc,
                   XPathConstants.NODESET);
           for (int i = 0, len = resultNodeList.getLength(); i < len; i++) {
               Node node = resultNodeList.item(i);
               String textValue = node.getTextContent();
               for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                   textValue = textValue.replaceAll("\\$\\{" + entry.getKey()
                           + "\\}", Matcher.quoteReplacement(entry.getValue()));
               }
               node.setTextContent(textValue);
           }
           saveDoc2XmlFile(doc, filePath);
       }


       public boolean saveDoc2XmlFile(Document document, String filename) {
           boolean flag = true;
           try {
               TransformerFactory transFactory = TransformerFactory.newInstance();
               Transformer transformer = transFactory.newTransformer();
               DOMSource source = new DOMSource();
               source.setNode(document);
               StreamResult result = new StreamResult();
               FileOutputStream fileOutputStream = new FileOutputStream(filename);
               result.setOutputStream(fileOutputStream);
               transformer.transform(source, result);
               fileOutputStream.close();
           } catch (Exception ex) {
               flag = false;
               ex.printStackTrace();
           }
           return flag;
       }



   }
      