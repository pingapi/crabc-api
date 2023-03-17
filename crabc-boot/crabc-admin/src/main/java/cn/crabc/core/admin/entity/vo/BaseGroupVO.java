package cn.crabc.core.admin.entity.vo;

import cn.crabc.core.admin.entity.BaseGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组对象
 *
 */
public class BaseGroupVO extends BaseGroup {

    private List<BaseGroupVO> children = new ArrayList<>();

    private List<ApiComboBoxVO> apis = new ArrayList<>();

    public List<BaseGroupVO> getChildren() {
        return children;
    }

    public void setChildren(List<BaseGroupVO> children) {
        this.children = children;
    }

    public List<ApiComboBoxVO> getApis() {
        return apis;
    }

    public void setApis(List<ApiComboBoxVO> apis) {
        this.apis = apis;
    }
}
