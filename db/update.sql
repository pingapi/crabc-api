-- v4.5.0
ALTER TABLE base_api_info ADD draft_content longtext COMMENT '接口暂存内容';
-- v5.0.0
ALTER TABLE base_api_info ADD transaction_enabled int DEFAULT 0 COMMENT '是否开启事务 1/0';
ALTER TABLE base_api_info ADD rate_limit_window_seconds int DEFAULT NULL COMMENT '限流时间窗口秒数';
ALTER TABLE base_api_info ADD rate_limit_count int DEFAULT NULL COMMENT '限流窗口请求次数';

-- 索引优化 2026-08-11
-- base_api_info 表新增索引
ALTER TABLE base_api_info ADD INDEX idx_api_status_release_time (api_status, release_time);
ALTER TABLE base_api_info ADD INDEX idx_group_id (group_id);
ALTER TABLE base_api_info ADD INDEX idx_parent_id_status (parent_id, api_status);
ALTER TABLE base_api_info ADD INDEX idx_create_by (create_by);

-- base_api_log 表新增索引
ALTER TABLE base_api_log ADD INDEX idx_api_id (api_id);
ALTER TABLE base_api_log ADD INDEX idx_request_time (request_time);
ALTER TABLE base_api_log ADD INDEX idx_app_name (app_name);
ALTER TABLE base_api_log ADD INDEX idx_api_id_time (api_id, request_time);

-- base_api_param 表新增索引
ALTER TABLE base_api_param ADD INDEX idx_api_id (api_id);
ALTER TABLE base_api_param ADD INDEX idx_api_id_model (api_id, param_model);
