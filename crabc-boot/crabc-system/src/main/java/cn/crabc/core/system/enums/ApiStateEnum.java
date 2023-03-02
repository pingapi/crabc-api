package cn.crabc.core.system.enums;

/**
 * API生命周期状态枚举
 *
 * @author yuqf
 */
public enum ApiStateEnum {
    EDIT("edit", "编辑"),
    AUDIT("audit", "审批"),
    RELEASE("release", "发布"),
    DESTROY("destroy", "销毁"),
    HISTORY("history", "历史");

    private String name;
    private String value;

    ApiStateEnum(String name, String value) {
        this.name = name;
        this.value = value;
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
