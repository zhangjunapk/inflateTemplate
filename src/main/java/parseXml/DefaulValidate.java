package parseXml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaulValidate implements Validate {
    public DefaulValidate() {
    }

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
}
