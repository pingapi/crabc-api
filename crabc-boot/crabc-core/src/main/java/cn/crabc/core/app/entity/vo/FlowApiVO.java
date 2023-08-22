package cn.crabc.core.app.entity.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 授权关联API
 */
public class FlowApiVO {
    private List<GroupApiVO> left = new ArrayList<>();
    private List<GroupApiVO> right = new ArrayList<>();

    public List<GroupApiVO> getLeft() {
        return left;
    }

    public void setLeft(List<GroupApiVO> left) {
        this.left = left;
    }

    public List<GroupApiVO> getRight() {
        return right;
    }

    public void setRight(List<GroupApiVO> right) {
        this.right = right;
    }
}
