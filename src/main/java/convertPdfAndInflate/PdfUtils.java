package convertPdfAndInflate;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Description: PdfUtils <br>
 * 依赖的包：itextpdf    itext-asian
 * commons-io,commons-codec
 * @author mk
 * @Date 2018-11-2 14:32 <br>
 * @Param
 * @return
 */
public class PdfUtils {


    public static void main(String[] args) throws IOException {
        HashMap map = new HashMap<String, String>();
        map.put("sx_no","对应pdf中的表单名为realName");
        map.put("idNo","对应pdf中的表单名为idNo");
//        String path = PdfUtils.class.getResource("/template").getPath();
//        System.out.println("path:"+path);
//        String sourceFile = path + File.separator + "test.pdf";
        String sourceFile = "d:/aa.pdf";
        String targetFile = "d:/target.pdf";
        String imageFilePath = "d:/pdf/test_fill.jpg";

        genPdf(map,sourceFile,targetFile);

//        System.out.println("获取pdf表单中的fieldNames:"+getTemplateFileFieldNames(sourceFile));
//        System.out.println("读取文件数组:"+fileBuff(sourceFile));
//        System.out.println("pdf转图片:"+pdf2Img(new File(targetFile),imageFilePath));
    }

    private static void genPdf(HashMap map, String sourceFile, String targetFile) throws IOException {
        File templateFile = new File(sourceFile);
        fillParam(map, FileUtils.readFileToByteArray(templateFile), targetFile);
    }

    /**
     * Description: 使用map中的参数填充pdf，map中的key和pdf表单中的field对应 <br>
     * @author mk
     * @Date 2018-11-2 15:21 <br>
     * @Param
     * @return
     */
    public static void fillParam(Map<String, String> fieldValueMap, byte[] file, String contractFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(contractFileName);
            PdfReader reader = null;
            PdfStamper stamper = null;
            BaseFont base = null;
            try {
                reader = new PdfReader(file);
                stamper = new PdfStamper(reader, fos);
                stamper.setFormFlattening(true);
                base = BaseFont.createFont("ms-black.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

                AcroFields acroFields = stamper.getAcroFields();
                for (String key : acroFields.getFields().keySet()) {

                    acroFields.setFieldProperty(key, "textfont", base, null);
                    acroFields.setFieldProperty(key, "textsize", new Float(8), null);
                }
                if (fieldValueMap != null) {
                    for (String fieldName : fieldValueMap.keySet()) {
                        acroFields.setField(fieldName, fieldValueMap.get(fieldName));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stamper != null) {
                    try {
                        stamper.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (reader != null) {
                    reader.close();
                }
            }

        } catch (Exception e) {
            System.out.println("填充参数异常");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * Description: 获取pdf表单中的fieldNames<br>
     * @author mk
     * @Date 2018-11-2 15:21 <br>
     * @Param
     * @return
     */
    public static Set<String> getTemplateFileFieldNames(String pdfFileName) {
        Set<String> fieldNames = new TreeSet<String>();
        PdfReader reader = null;
        try {
            reader = new PdfReader(pdfFileName);
            Set<String> keys = reader.getAcroFields().getFields().keySet();
            for (String key : keys) {
                int lastIndexOf = key.lastIndexOf(".");
                int lastIndexOf2 = key.lastIndexOf("[");
                fieldNames.add(key.substring(lastIndexOf != -1 ? lastIndexOf + 1 : 0, lastIndexOf2 != -1 ? lastIndexOf2 : key.length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return fieldNames;
    }


    /**
     * Description: 读取文件数组<br>
     * @author mk
     * @Date 2018-11-2 15:21 <br>
     * @Param
     * @return
     */
    public static byte[] fileBuff(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            //System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] file_buff = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < file_buff.length && (numRead = fi.read(file_buff, offset, file_buff.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != file_buff.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        fi.close();
        return file_buff;
    }

    /**
     * Description: 合并pdf <br>
     * @author mk
     * @Date 2018-11-2 15:21 <br>
     * @Param
     * @return
     */
    public static void mergePdfFiles(String[] files, String savepath) {
        Document document = null;
        try {
            document = new Document(); //默认A4大小
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(savepath));
            document.open();
            for (int i = 0; i < files.length; i++) {
                PdfReader reader = null;
                try {
                    reader = new PdfReader(files[i]);
                    int n = reader.getNumberOfPages();
                    for (int j = 1; j <= n; j++) {
                        document.newPage();
                        PdfImportedPage page = copy.getImportedPage(reader, j);
                        copy.addPage(page);
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭PDF文档流，OutputStream文件输出流也将在PDF文档流关闭方法内部关闭
            if (document != null) {
                document.close();
            }

        }
    }



    /**
     * pdf转图片
     * @param file pdf
     * @return
     */
    public static boolean pdf2Img(File file,String imageFilePath) {
        try {
            //生成图片保存
            byte[] data = pdfToPic(PDDocument.load(file));
            File imageFile = new File(imageFilePath);
            ImageThumbUtils.thumbImage(data, 1, imageFilePath); //按比例压缩图片
            System.out.println("pdf转图片文件地址:" + imageFilePath);
            return true;
        } catch (Exception e) {
            System.out.println("pdf转图片异常：");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * pdf转图片
     */
    private static byte[] pdfToPic(PDDocument pdDocument) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<BufferedImage> piclist = new ArrayList<BufferedImage>();
        try {
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {//
                // 0 表示第一页，300 表示转换 dpi，越大转换后越清晰，相对转换速度越慢
                BufferedImage image = renderer.renderImageWithDPI(i, 108);
                piclist.add(image);
            }
            // 总高度 总宽度 临时的高度 , 或保存偏移高度 临时的高度，主要保存每个高度
            int height = 0, width = 0, _height = 0, __height = 0,
                    // 图片的数量
                    picNum = piclist.size();
            // 保存每个文件的高度
            int[] heightArray = new int[picNum];
            // 保存图片流
            BufferedImage buffer = null;
            // 保存所有的图片的RGB
            List<int[]> imgRGB = new ArrayList<int[]>();
            // 保存一张图片中的RGB数据
            int[] _imgRGB;
            for (int i = 0; i < picNum; i++) {
                buffer = piclist.get(i);
                heightArray[i] = _height = buffer.getHeight();// 图片高度
                if (i == 0) {
                    // 图片宽度
                    width = buffer.getWidth();
                }
                // 获取总高度
                height += _height;
                // 从图片中读取RGB
                _imgRGB = new int[width * _height];
                _imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
                imgRGB.add(_imgRGB);
            }

            // 设置偏移高度为0
            _height = 0;
            // 生成新图片
            BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            int[] lineRGB = new int[8 * width];
            int c = new Color(128, 128, 128).getRGB();
            for (int i = 0; i < lineRGB.length; i++) {
                lineRGB[i] = c;
            }
            for (int i = 0; i < picNum; i++) {
                __height = heightArray[i];
                // 计算偏移高度
                if (i != 0)
                    _height += __height;
                imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中

                // 模拟页分隔
                if (i > 0) {
                    imageResult.setRGB(0, _height + 2, width, 8, lineRGB, 0, width);
                }
            }
            // 写流
            ImageIO.write(imageResult, "jpg", baos);
        } catch (Exception e) {
            System.out.println("pdf转图片异常：");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(baos);
            try {
                pdDocument.close();
            } catch (Exception ignore) {
            }
        }

        return baos.toByteArray();
    }
}