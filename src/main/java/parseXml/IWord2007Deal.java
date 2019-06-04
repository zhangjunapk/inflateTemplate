package parseXml;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface IWord2007Deal {
    boolean fill(File var1, File var2, Map<String, MarkbookBean> var3, Map<String, String> var4, boolean var5) throws Exception;

    Map<String, String> extract(File var1, Set<String> var2) throws Exception;

    boolean compareStyle(File var1, File var2, Map<String, MarkbookBean> var3);
}
