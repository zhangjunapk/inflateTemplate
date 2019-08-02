package poi.zkr;

public class TextValue {
    private Object value;
    private String format;

    public TextValue(Object value) {
        this.value = value;
    }

    public TextValue(Object value, String format) {
        this.value = value;
        this.format = format;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
