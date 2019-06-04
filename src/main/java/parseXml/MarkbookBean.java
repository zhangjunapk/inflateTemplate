package parseXml;

public class MarkbookBean {
    private String name;
    private String regValue;
    private boolean isValidate;

    public MarkbookBean() {
    }

    public MarkbookBean(String name) {
        this.name = name;
    }

    public MarkbookBean(String name, String regValue, boolean isValidate) {
        this(name);
        this.regValue = regValue;
        this.isValidate = isValidate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegValue() {
        return this.regValue;
    }

    public void setRegValue(String regValue) {
        this.regValue = regValue;
    }

    public boolean isValidate() {
        return this.isValidate;
    }

    public void setValidate(boolean isValidate) {
        this.isValidate = isValidate;
    }



    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            MarkbookBean other = (MarkbookBean)obj;
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!this.name.equals(other.name)) {
                return false;
            }

            return true;
        }
    }
}
