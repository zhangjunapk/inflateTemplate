import com.xandryex.WordReplacer;

import java.io.File;
import java.io.IOException;

public class WordReplacerTest {
    public static void main(String[] args) throws Exception {

        WordReplacer wordReplacer=new WordReplacer(new File("/media/zhangjun/新加卷1/ii.docx"));
        wordReplacer.replaceWordsInTables("名称","151551");
        wordReplacer.saveAndGetModdedFile("/media/zhangjun/新加卷1/ii_ubuntu.docx");
    }
}
