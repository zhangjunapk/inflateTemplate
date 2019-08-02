package poi;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import poi.zkr.WordOperator;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * @Auther: ZhangJun
 * @Date: 2019/5/13 16:43
 * @Description: doc操作
 */
public class DocUtil {
    /**
     * 填充doc模板并生成数据
     * @param templateDocPath 绝对路径+文件名.后缀
     * @param toPath 绝对路径+文件名.后缀
     * @param params 要放的数据>书签和值的对应
     */
    public static void inflateData(String templateDocPath, String toPath, Map<String,String> params) throws Exception {
        if(templateDocPath==null||"".equals(templateDocPath)){
            throw new Exception("请传递templateDocPath,格式:路径/文件名.后缀名");
        }
        if(toPath==null||"".equals(toPath)){
            throw new Exception("请传递toPath,格式:路径/文件名.后缀名");
        }

        //接下来判断他的路径规则是否符合
        String templateDocFileNameWithSuffix = templateDocPath.substring(templateDocPath.lastIndexOf("/") + 1);
        String toPathFileNameWithSuffix = toPath.substring(toPath.lastIndexOf("/") + 1);
        if(templateDocFileNameWithSuffix.lastIndexOf(".")==-1){
            throw new Exception("templateDocPath:路径/文件名.后缀名");
        }
        if(toPathFileNameWithSuffix.lastIndexOf(".")==-1){
            throw new Exception("toPath格式:路径/文件名.后缀名");
        }
        if(params==null||params.size()==0){
            //如果是这种情况，我要直接复制过去就行
            FileUtils.copyFile(new File(templateDocPath),new File(toPath));
            System.out.println("没有要往书签填入的数据，直接复制");
            return;
        }
        Map<String,String> inTableParams=new HashMap<>();
        MSWordTool msWordTool=new MSWordTool();
        msWordTool.setTemplate(templateDocPath);
        msWordTool.replaceBookMark(params,inTableParams);
        msWordTool.saveAs(toPath.replace(toPathFileNameWithSuffix,""),toPathFileNameWithSuffix);

        if(inTableParams.size()>0){
            System.out.println("我需要用中科软的来进行表格填充");
            //我需要复制一份
            File file = new File(toPath);
            String parent = file.getParent();
            String absolutePath = file.getAbsolutePath();
            String s = UUID.randomUUID().toString().replaceAll("-", "");
            File temp = new File(parent + s);
            FileUtils.copyFile(file,temp);
            FileUtil.deleteFile(file,10);

            WordOperator wordOperator = new WordOperator();
            wordOperator.openFile(parent+s);
            wordOperator.replaceBookMark(inTableParams);
            try {
                wordOperator.saveAs(absolutePath);
                FileUtil.deleteFile(temp,10);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                //异常的话
                FileUtils.copyFile(temp,file);
            }
        }

        /**/

        /*WordUtils util = new WordUtils(true);
        util.openDocument(templateDocPath);
        util.setSaveOnExit(true);
        for(Map.Entry<String,String> e: params.entrySet()){
            util.insertAtBookmark(e.getKey(),e.getValue());
        }
        util.saveAs(toPath);
        util.closeDocument();*/

    }

    /**
     * 获得一个doc文件里面的所有书签
     * @param is
     * @return
     */
    public static List<String> getBookMarks(InputStream is){
        List<String> result=new ArrayList<>();
        try {
         XWPFDocument document = new XWPFDocument(
                 OPCPackage.open(is));

            BookMarks bookMarks = new BookMarks(document);

            //然后遍历里面的所有书签

            Iterator<String> bookMarkIter = bookMarks.getNameIterator();
            while (bookMarkIter.hasNext()) {
                String bookMarkName = bookMarkIter.next();
                result.add(bookMarkName);
            }
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        /*
        Map<String,String> map=new HashMap<>();
        map.put("idcard","516634665651465163");
        try {
            inflateData("d:/fuckk.docx","d:/fuckk_after.docx",map);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
