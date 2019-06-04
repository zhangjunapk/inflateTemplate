package convertPdfAndInflate;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewPdfUtil {
    public static ByteArrayOutputStream generatePdfStream(String fileName, String fontName, Map<String, String> data) throws  Exception{
        PdfReader reader = new PdfReader(fileName);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        /* 将要生成的目标PDF文件名称 */
        PdfStamper ps = new PdfStamper(reader, bos);
        PdfContentByte under = ps.getUnderContent(1);
        /* 使用中文字体 */
        BaseFont bf = BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        ArrayList<BaseFont> fontList = new ArrayList<BaseFont>();
        fontList.add(bf);
        /* 取出报表模板中的所有字段 */
        AcroFields fields = ps.getAcroFields();
        fields.setSubstitutionFonts(fontList);
        fillData(fields, data);
        /* 必须要调用这个，否则文档不会生成的 */
        ps.setFormFlattening(true);
        ps.close();
        return bos;
    }

    public static void fillData(AcroFields fields, Map<String, String> data)
            throws IOException, DocumentException {
        for (String key : data.keySet()) {
            String value = data.get(key);
            fields.setField(key, value); // 为字段赋值,注意字段名称是区分大小写
        }
    }

    public static void main(String[] args) throws Exception {

        Map<String,String> map=new HashMap<>();
        map.put("sx_no","我是中国人");
        ByteArrayOutputStream bos = generatePdfStream("d:/raw_1.pdf", "ms-black.ttf", map);

        byte[] bytes = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream("d:/raw_1_pdf.pdf");
        fos.write(bytes);
        fos.flush();
        fos.close();

    }

}
