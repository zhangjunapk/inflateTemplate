package parseXml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Word2007Deal implements IWord2007Deal {
    private Validate validate;

    public Word2007Deal(Validate validate) {
        this.validate = validate;
    }

    public boolean fill(File descword, File tempWord, Map<String, MarkbookBean> markbookBeans, Map<String, String> datas, boolean is_validate) throws Exception {
        ZipFile docxFile = null;

        try {
            docxFile = new ZipFile(tempWord);
        } catch (Exception var20) {
            var20.printStackTrace();
            //throw new RuntimeException("请使用word2007的文档模板");
        }

        Document doc = WordUitls.getDoc(docxFile);
        NodeList this_book_list = doc.getElementsByTagName("w:bookmarkStart");
        if (this_book_list.getLength() != 0) {
            for(int j = 0; j < this_book_list.getLength(); ++j) {
                Element oldBookStart = (Element)this_book_list.item(j);
                String bookMarkName = oldBookStart.getAttribute("w:name");
                if (datas.containsKey(bookMarkName)) {
                    String book_value = (String)datas.get(bookMarkName);
                    if (is_validate) {
                        if (markbookBeans == null || markbookBeans.isEmpty()) {
                            throw new IllegalArgumentException("没有对应markbookBeans数据源");
                        }

                        if (!markbookBeans.containsKey(bookMarkName)) {
                            throw new RuntimeException(bookMarkName + "标签没有对应的bean数据源");
                        }

                        MarkbookBean bean = (MarkbookBean)markbookBeans.get(bookMarkName);
                        this.validate.pass(bean, book_value);
                    }

                    Node wr = doc.createElement("w:r");
                    Node wt = doc.createElement("w:t");
                    Node wt_text = doc.createTextNode(book_value);
                    wt.appendChild(wt_text);
                    wr.appendChild(wt);
                    Element node = WordUitls.recursionFindNextNode(oldBookStart, "w:r");
                    System.out.println(node.getNodeName());
                    node = WordUitls.recursionFindNextNode(oldBookStart, "w:r");
                    NodeList wtList = node.getElementsByTagName("w:t");
                    Element pnode;
                    if (wtList.getLength() == 0) {
                        pnode = (Element)oldBookStart.getParentNode();
                        Element endnode = (Element)oldBookStart.getNextSibling();
                        pnode.insertBefore(wr, endnode);
                    } else {
                        pnode = (Element)wtList.item(0);
                        pnode.setTextContent(book_value);
                    }
                }
            }
        }

        WordUitls.setDoOUt(docxFile, doc, descword);
        return true;
    }

    public Map<String, String> extract(File descword, Set<String> markbooks) throws Exception {
        Map<String, String> datas = new HashMap();
        ZipFile docxFile = null;

        try {
            docxFile = new ZipFile(descword);
        } catch (Exception var11) {
            throw new RuntimeException("请使用word2007的文档模板");
        }

        Document doc = WordUitls.getDoc(docxFile);
        NodeList this_book_list = doc.getElementsByTagName("w:bookmarkStart");
        if (this_book_list.getLength() != 0) {
            for(int j = 0; j < this_book_list.getLength(); ++j) {
                Element oldBookStart = (Element)this_book_list.item(j);
                String bookMarkName = oldBookStart.getAttribute("w:name");
                if (markbooks.contains(bookMarkName)) {
                    StringBuffer sb = new StringBuffer(100);
                    WordUitls.recursionFindNextNode(oldBookStart, sb);
                    datas.put(bookMarkName, sb.toString());
                }
            }
        }

        return datas;
    }

    public boolean compareStyle(File descword, File tempWord, Map<String, MarkbookBean> markbookBeans) {
        throw new UnsupportedOperationException("方法不支持");
    }
}
