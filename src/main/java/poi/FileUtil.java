package poi;

import java.io.*;

public class FileUtil {


    /**
     * 删除一个文件
     * @param file
     * @param times
     */
        public static void deleteFile(File file,int times){
            for(int i=0;i<times;i++){
                System.gc();
                file.delete();
            }
        }

}