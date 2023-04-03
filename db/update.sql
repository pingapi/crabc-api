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
