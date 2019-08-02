package poi.zkr;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextFormat {
    private String NUM_FORMAT = "###,##0.00";
    private String DATE_FORMAT = "yyyy-MM-dd";

    public TextFormat() {
    }

    public String getValue(Object value) {
        String result = "";
        value = value == null ? "" : value;
        if (value instanceof Double) {
            result = (new DecimalFormat(this.NUM_FORMAT)).format((Double)value);
        } else if (value instanceof Date) {
            result = (new SimpleDateFormat(this.DATE_FORMAT)).format((Date)value);
        } else if (value instanceof TextValue) {
            TextValue textValue = (TextValue)value;
            result = this.getValue(textValue.getValue(), textValue.getFormat());
        } else {
            result = value.toString();
        }

        return result;
    }

    public String getValue(Object value, String format) {
        String result = "";
        if (value instanceof Double) {
            result = (new DecimalFormat(format)).format((Double)value);
        } else if (value instanceof Date) {
            result = (new SimpleDateFormat(format)).format((Date)value);
        } else {
            result = value.toString();
        }

        return result;
    }

    public String getDATE_FORMAT() {
        return this.DATE_FORMAT;
    }

    public void setDATE_FORMAT(String dATE_FORMAT) {
        this.DATE_FORMAT = dATE_FORMAT;
    }

    public String getNUM_FORMAT() {
        return this.NUM_FORMAT;
    }

    public void setNUM_FORMAT(String nUM_FORMAT) {
        this.NUM_FORMAT = nUM_FORMAT;
    }
}
