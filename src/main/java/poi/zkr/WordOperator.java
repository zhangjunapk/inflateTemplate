package poi.zkr;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WordOperator {
    private XWPFDocument document;
    private WordBookMarks bookMarks = null;

    public WordOperator() {
    }

    public void openFile(String filename) {
        File file = null;
        FileInputStream fis = null;

        try {
            file = new File(filename);
            fis = new FileInputStream(file);
            this.document = new XWPFDocument(fis);
            this.bookMarks = new WordBookMarks(this.document);
        } catch (Exception var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (Exception var12) {
                var12.printStackTrace();
            }

        }

    }

    public WordBookMarks getBookMarks() {
        return this.bookMarks;
    }

    public void replaceBookMark(Map<String, String> indicator) {
        Iterator<String> bookMarkIter = this.bookMarks.getNameIterator();
        TextFormat format = new TextFormat();

        while(bookMarkIter.hasNext()) {
            String bookMarkName = (String)bookMarkIter.next();
            WordBookMark bookMark = this.bookMarks.getBookmark(bookMarkName);
            if (indicator.containsKey(bookMarkName)) {
                String value = format.getValue(indicator.get(bookMarkName));
                bookMark.insertTextAtBookMark(value, 1);
            }
        }

    }

    public void fillTableAtBookMark(String bookMarkName, List<Map<String, Object>> content) {
        WordBookMark bookMark = this.bookMarks.getBookmark(bookMarkName);
        TableWordBookMark tableWordBookMark = new TableWordBookMark(bookMark);
        if (tableWordBookMark.isTableBookMark()) {
            tableWordBookMark.replaceBookMark(content);
        }

    }

    public void saveAs(String fileName) throws Throwable {
        File newFile = new File(fileName);

        try {
            Throwable var3 = null;
            Object var4 = null;

            try {
                FileOutputStream fos = new FileOutputStream(newFile);

                try {
                    this.document.write(fos);
                    fos.flush();
                } finally {
                    if (fos != null) {
                        fos.close();
                    }

                }
            } catch (Throwable var13) {
                throw var13;
            }
        } catch (Exception var14) {
            var14.printStackTrace();
        }

    }

    public InputStream getInputStream() throws Exception {
        if (this.document != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            this.document.write(output);
            return new ByteArrayInputStream(output.toByteArray());
        } else {
            return null;
        }
    }


}
