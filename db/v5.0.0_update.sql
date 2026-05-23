ALTER TABLE base_api_info ADD transaction_enabled int DEFAULT 0 COMMENT '是否开启事务 1/0';
ALTER TABLE base_api_info ADD rate_limit_window_seconds int DEFAULT NULL COMMENT '限流时间窗口秒数';
ALTER TABLE base_api_info ADD rate_limit_count int DEFAULT NULL COMMENT '限流窗口请求次数';
