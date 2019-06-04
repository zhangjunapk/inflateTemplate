package parseXml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//图片不支持,单元格里面的字体和大小有点问题
public class csdn上面的一个方法 {
    public static void main(String[] args) {
        try {
            fill();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void fill() throws Exception {
        IWord2007Deal Word2007Deal = new Word2007Deal(new Validate() {
            public void pass(MarkbookBean bean, String value) throws ValidateException {
                boolean result = true;
                System.out.println("标签:" + bean.getName());
                if (bean.isValidate()) {
                    Matcher m = Pattern.compile(bean.getRegValue()).matcher(value);
                    result = m.find();
                    if (!result) {
                        System.out.println("标签:" + bean.getName() + "格式错误");
                        throw new ValidateException("标签:" + bean.getName() + "格式错误");
                    }
                }
            }

        });
        File tempWord = new File("/media/zhangjun/新加卷/raw_1.docx");
        File descword = new File("/media/zhangjun/新加卷/raw_1_ubuntu.docx");
        Map<String, String> datas = new HashMap();
        datas.put("sx_no", "25f1515");

        Map<String, MarkbookBean> markbookBeans = new HashMap();
        markbookBeans.put("sx_no", new MarkbookBean("sx_no"));
        Word2007Deal.fill(descword, tempWord, markbookBeans, datas, true);
        System.out.println();
    }
}
