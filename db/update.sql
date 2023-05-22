DROP TABLE IF EXISTS `base_api_param`;
-- 2.0新增表
CREATE TABLE `base_api_param` (
                                  `param_id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                  `api_id` bigint NOT NULL COMMENT 'apiId',
                                  `param_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '参数名',
                                  `param_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '参数类型：1:请求参数，2:返回参数',
                                  `column_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '关联字段名',
                                  `param_model` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '参数模型，request,response',
                                  `required` tinyint(1) DEFAULT '1' COMMENT '是否必填',
                                  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作符',
                                  `default_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '默认值',
                                  `example` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '示例值',
                                  `datasource_id` varchar(128) DEFAULT NULL COMMENT '数据源ID',
                                  `schema_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'Schema名',
                                  `table_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表名',
                                  `param_desc` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '描述',
                                  `create_time` datetime DEFAULT NULL,
                                  PRIMARY KEY (`param_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='API接口参数';

DROP TABLE IF EXISTS `base_flow_api`;
CREATE TABLE `base_flow_api` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `flow_id` int DEFAULT NULL,
                                 `api_id` int DEFAULT NULL,
                                 `create_time` datetime DEFAULT NULL,
                                 `create_by` varchar(128) DEFAULT NULL,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='API限流关系表';

DROP TABLE IF EXISTS `base_flow_rule`;
CREATE TABLE `base_flow_rule` (
                                  `flow_id` int NOT NULL AUTO_INCREMENT,
                                  `flow_name` varchar(128) DEFAULT NULL COMMENT '规则名称',
                                  `flow_grade` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '限流等级',
                                  `unit_time` int DEFAULT NULL COMMENT '单位时间s',
                                  `flow_count` int DEFAULT NULL COMMENT '限流阈值',
                                  `flow_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '策略类型：flow、degrade',
                                  `api_count` int DEFAULT NULL COMMENT '关联API数量',
                                  `api_json` text COMMENT 'api数据',
                                  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `update_time` datetime DEFAULT NULL,
                                  `create_by` varchar(128) DEFAULT NULL,
                                  `update_by` varchar(128) DEFAULT NULL,
                                  PRIMARY KEY (`flow_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='限流规则表';
