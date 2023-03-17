package cn.crabc.core.admin.enums;

/**
 * SQL DML操作枚举
 *
 * @author yuqf
 */
public enum SqlTypeEnum {
    SELECT("select", "查询"),
    INSERT("insert", "插入"),
    UPDATE("update", "修改"),
    DELETE("delete", "删除");

    private String name;
    private String value;

    SqlTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 校验是否存在类型
     * @param name
     * @return
     */
    public static boolean  checkType(String name){
        for (SqlTypeEnum type : values()){
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
