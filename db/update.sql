ALTER TABLE base_api_info ADD result_type varchar(30) NULL COMMENT '返回结果类型： one、array、excel';
ALTER TABLE base_api_log ADD response_code int NULL COMMENT 'HTTP请求响应码';