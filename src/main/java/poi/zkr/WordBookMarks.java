package poi.zkr;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;

import java.util.*;
import java.util.Map.Entry;

public class WordBookMarks {
    private HashMap<String, WordBookMark> _bookmarks = null;

    public WordBookMarks(XWPFDocument document) {
        this._bookmarks = new HashMap();

        List<XWPFTable> tables = document.getTables();
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        for (XWPFTable table : tables) {
            //表格属性
            CTTblPr pr = table.getCTTbl().getTblPr();
            //获取表格对应的行
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                //获取行对应的单元格
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    this.procParaList(cell);
                }
            }
        }
        //this.procParaList(cells);
        this.procTableList(document.getTables());
    }

    public void procTableList(List<XWPFTable> tableList) {
        Iterator var3 = tableList.iterator();

        while (var3.hasNext()) {
            XWPFTable table = (XWPFTable) var3.next();
            List<XWPFTableRow> rowList = table.getRows();
            Iterator var6 = rowList.iterator();

            while (var6.hasNext()) {
                XWPFTableRow row = (XWPFTableRow) var6.next();
                List<XWPFTableCell> cellList = row.getTableCells();
                Iterator var9 = cellList.iterator();

                while (var9.hasNext()) {
                    XWPFTableCell cell = (XWPFTableCell) var9.next();
                    this.procParaList(cell);
                }
            }
        }

    }

    public WordBookMark getBookmark(String bookmarkName) {
        return this._bookmarks.get(bookmarkName) == null ? null : (WordBookMark) this._bookmarks.get(bookmarkName);
    }

    public Collection<WordBookMark> getBookmarkList() {
        return this._bookmarks.values();
    }

    public List<WordBookMark> getBookmarkListByNormal() {
        List<WordBookMark> marks = new ArrayList();
        Iterator iter = this._bookmarks.entrySet().iterator();

        while (iter.hasNext()) {
            WordBookMark wordBookMark = (WordBookMark) ((Entry) iter.next()).getValue();
            marks.add(wordBookMark);
        }

        SortListUtil<WordBookMark> sort = new SortListUtil();
        sort.sortByMethod(marks, false, new String[]{"bookMarkName"});
        return marks;
    }

    public Iterator<String> getNameIterator() {
        return this._bookmarks.keySet().iterator();
    }

    private void procParaList(XWPFTableCell cell) {
        List<XWPFParagraph> paragraphList = cell.getParagraphs();
        Iterator var4 = paragraphList.iterator();

        while (var4.hasNext()) {
            XWPFParagraph paragraph = (XWPFParagraph) var4.next();
            this.procTableList(paragraph.getBody().getTables());
            List<CTBookmark> bookmarkList = paragraph.getCTP().getBookmarkStartList();
            Iterator var7 = bookmarkList.iterator();

            while (var7.hasNext()) {
                CTBookmark bookmark = (CTBookmark) var7.next();
                this._bookmarks.put(bookmark.getName(), new WordBookMark(bookmark, paragraph, cell));
            }
        }

    }

    private void procParaList(List<XWPFParagraph> paragraphList, XWPFTableRow tableRow) {


    }
}