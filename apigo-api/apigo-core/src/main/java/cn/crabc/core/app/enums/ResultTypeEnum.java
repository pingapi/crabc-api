package cn.crabc.core.app.enums;

/**
 * 返回结果类型 操作枚举
 *
 * @author yuqf
 */
public enum ResultTypeEnum {
    ONE("one", "对象"),
    ARRAY("array", "数组"),
    EXCEL("excel", "excel文件");

    private String name;
    private String value;

    ResultTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 校验是否存在类型
     * @param name
     * @return
     */
    public static boolean  checkType(String name){
        for (ResultTypeEnum type : values()){
            if (type.getName().equals(name.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
