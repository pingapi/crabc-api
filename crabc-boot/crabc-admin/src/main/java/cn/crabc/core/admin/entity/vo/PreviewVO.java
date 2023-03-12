package cn.crabc.core.admin.entity.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SQL运行预览结果
 *
 * @author yuqf
 */
public class PreviewVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标题字段名
     */
    Set<String> metadata;

    /**
     * 数据
     */
    List<Map<String, Object>> data;

    public Set<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Set<String> metadata) {
        this.metadata = metadata;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
