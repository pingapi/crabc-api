package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SQL运行预览结果
 *
 * @author yuqf
 */
@Setter
@Getter
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

}
