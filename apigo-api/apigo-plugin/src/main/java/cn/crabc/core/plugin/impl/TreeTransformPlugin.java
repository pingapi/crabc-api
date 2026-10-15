package cn.crabc.core.plugin.impl;

import cn.crabc.core.plugin.DataPlugin;
import cn.crabc.core.plugin.PluginKeys;
import cn.crabc.core.plugin.util.PluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 层级转树插件（treeTransform）
 * <p>
 * 根据唯一标识字段（idField）与父标识字段（parentIdField）将平铺数据转换为树形结构；
 * rootId 指定根节点标识值，留空时自动识别（父标识在数据中不存在或为空的节点作为根）。
 * 节点保留原始字段，子节点挂载在 childrenField（默认 children）下。
 *
 * @author yuqf
 */
@Component
public class TreeTransformPlugin implements DataPlugin {

    private static final Logger log = LoggerFactory.getLogger(TreeTransformPlugin.class);

    private static final String DEFAULT_CHILDREN_FIELD = "children";

    @Override
    public String getName() {
        return PluginKeys.TREE_TRANSFORM;
    }

    @Override
    public Object apply(Object data, Map<String, Object> config, Map<String, Object> requestParams) {
        List<Map<String, Object>> rows = PluginUtils.asRowList(data);
        if (rows == null || rows.isEmpty()) {
            return data;
        }
        String idField = PluginUtils.str(config.get("idField"));
        String parentIdField = PluginUtils.str(config.get("parentIdField"));
        if (PluginUtils.isBlank(idField) || PluginUtils.isBlank(parentIdField)) {
            throw new IllegalArgumentException("层级转树插件必须配置 ID 字段和父 ID 字段");
        }
        String childrenField = PluginUtils.isBlank(config.get("childrenField"))
                ? DEFAULT_CHILDREN_FIELD : PluginUtils.str(config.get("childrenField"));
        String rootId = PluginUtils.isBlank(config.get("rootId")) ? null : PluginUtils.str(config.get("rootId"));
        // 配置字段不存在时所有行都会被视为根节点，显式告警避免"配置无效却无感知"
        if (!PluginUtils.containsField(rows, idField)) {
            log.warn("数据插件[{}]ID字段[{}]不存在于结果集中", getName(), idField);
        }
        if (!PluginUtils.containsField(rows, parentIdField)) {
            log.warn("数据插件[{}]父ID字段[{}]不存在于结果集中", getName(), parentIdField);
        }

        // id -> 节点索引，保持原始顺序
        Map<String, Map<String, Object>> nodeMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = PluginUtils.str(row.get(idField));
            if (id != null && !nodeMap.containsKey(id)) {
                nodeMap.put(id, row);
            }
        }

        // 建立父子关系
        List<Map<String, Object>> roots = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String id = PluginUtils.str(row.get(idField));
            String parentId = PluginUtils.str(row.get(parentIdField));
            boolean isRoot;
            if (rootId != null) {
                // 指定根标识：父标识等于根值的节点为根节点；父标识在数据中不存在时兜底为根
                isRoot = rootId.equals(parentId) || !nodeMap.containsKey(parentId) || PluginUtils.isBlank(row.get(parentIdField));
            } else {
                isRoot = PluginUtils.isBlank(row.get(parentIdField)) || !nodeMap.containsKey(parentId);
            }
            if (isRoot) {
                roots.add(row);
                continue;
            }
            Map<String, Object> parent = nodeMap.get(parentId);
            if (parent == row || id != null && id.equals(parentId)) {
                // 自引用防御，视为根节点
                roots.add(row);
                continue;
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> children = (List<Map<String, Object>>) parent.computeIfAbsent(
                    childrenField, k -> new ArrayList<Map<String, Object>>());
            children.add(row);
        }
        // 清理空 children，保持输出干净
        for (Map<String, Object> node : nodeMap.values()) {
            Object children = node.get(childrenField);
            if (children instanceof List<?> list && list.isEmpty()) {
                node.remove(childrenField);
            }
        }
        return roots;
    }
}
