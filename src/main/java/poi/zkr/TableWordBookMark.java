package poi.zkr;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.w3c.dom.Node;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableWordBookMark {
    private Map<String, String> columnMap = new HashMap();
    private Map<String, Node> styleNode = new HashMap();
    private WordBookMark bookMark = null;
    private XWPFTable table = null;
    private XWPFTableRow row = null;
    private int rowNum = 0;

    public TableWordBookMark(WordBookMark bookMark) {
        this.bookMark = bookMark;
        this.init();
    }

    public boolean isTableBookMark() {
        return this.bookMark.isInTable() && this.columnMap.size() > 0;
    }

    private void init() {
        if (this.bookMark.isInTable()) {
            this.table = this.bookMark.getContainerTable();
            this.row = this.bookMark.getContainerTableRow();
            List<XWPFTableCell> rowCell = this.row.getTableCells();

            for(int i = 0; i < rowCell.size(); ++i) {
                this.columnMap.put(String.valueOf(i), ((XWPFTableCell)rowCell.get(i)).getText().trim());
                Node node1 = ((XWPFParagraph)((XWPFTableCell)rowCell.get(i)).getParagraphs().get(0)).getCTP().getDomNode();

                for(int x = 0; x < node1.getChildNodes().getLength(); ++x) {
                    if (node1.getChildNodes().item(x).getNodeName().equals("w:r")) {
                        Node node2 = node1.getChildNodes().item(x);

                        for(int y = 0; y < node2.getChildNodes().getLength(); ++y) {
                            if (node2.getChildNodes().item(y).getNodeName().endsWith("w:rPr")) {
                                this.styleNode.put(String.valueOf(i), node2.getChildNodes().item(y));
                            }
                        }
                    }
                }
            }
        }

    }

    public void replaceBookMark(List<Map<String, Object>> content) {
        int rcount;
        for(rcount = 0; rcount < this.table.getNumberOfRows(); ++rcount) {
            if (this.table.getRow(rcount).equals(this.row)) {
                this.rowNum = rcount;
                break;
            }
        }

        this.table.removeRow(this.rowNum);

        for(rcount = 0; rcount < content.size(); ++rcount) {
            XWPFTableRow tableRow = this.table.createRow();
            CTTrPr trPr = tableRow.getCtRow().addNewTrPr();
            CTHeight ht = trPr.addNewTrHeight();
            ht.setVal(BigInteger.valueOf(360L));
        }

        rcount = this.table.getNumberOfRows();
        List<XWPFTableCell> rowCell = this.row.getTableCells();

        for(int i = this.rowNum; i < rcount; ++i) {
            XWPFTableRow newRow = this.table.getRow(i);
            if (newRow.getTableCells().size() != rowCell.size()) {
                int sub = Math.abs(newRow.getTableCells().size() - rowCell.size());

                for(int j = 0; j < sub; ++j) {
                    newRow.addNewTableCell();
                }
            }

            List<XWPFTableCell> cells = newRow.getTableCells();
            TextFormat nTextFormat = new TextFormat();
            Map<String, Object> rowValue = (Map)content.get(i - this.rowNum);

            for(int j = 0; j < cells.size(); ++j) {
                XWPFParagraph para = (XWPFParagraph)((XWPFTableCell)cells.get(j)).getParagraphs().get(0);
                XWPFRun run = para.createRun();
                Node node = run.getCTR().getDomNode();
                if (rowValue.containsKey(this.columnMap.get(String.valueOf(j)))) {
                    String value = nTextFormat.getValue(rowValue.get(this.columnMap.get(String.valueOf(j))));
                    run.setText(value);
                    node.insertBefore(((Node)this.styleNode.get(String.valueOf(j))).cloneNode(true), node.getFirstChild());
                }

                para.setAlignment(ParagraphAlignment.CENTER);
            }
        }

    }

    public Map<String, String> getColumnMap() {
        return this.columnMap;
    }

    public void setColumnMap(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }

    public Map<String, Node> getStyleNode() {
        return this.styleNode;
    }

    public void setStyleNode(Map<String, Node> styleNode) {
        this.styleNode = styleNode;
    }

    public WordBookMark getBookMark() {
        return this.bookMark;
    }

    public void setBookMark(WordBookMark bookMark) {
        this.bookMark = bookMark;
    }
}
