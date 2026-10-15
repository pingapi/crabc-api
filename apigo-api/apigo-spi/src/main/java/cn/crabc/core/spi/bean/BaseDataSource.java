/*
 * Copyright 2023, crabc.cn (creabc@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.crabc.core.spi.bean;

import java.io.Serializable;

/**
 * 数据源属性
 *
 * @author yuqf
 */
public class BaseDataSource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源ID
     */
    private Integer datasourceId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * 数据源类型 mysql、oracle、postgres
     */
    private String datasourceType;

    /**
     * jdbcURL
     */
    private String jdbcUrl;

    /**
     * IP
     */
    private String host;

    /**
     * port
     */
    private String port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 描述
     */
    private String remarks;

    /**
     * 分类
     */
    private String classify;

    /**
     * 状态
     */
    private Integer enabled;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 最小连接数
     */
    private Integer minIdle;
    /**
     * 最大连接数
     */
    private Integer maxActive;
    /**
     * 连接超时时间
     */
    private Integer connectTimeout;
    /**
     * 最大空闲时间
     */
    private Integer idleTimeout;
    /**
     * 最大生命周期
     */
    private Integer maxLifetime;
    /**
     * 保活时间频率
     */
    private Integer keepaliveTime;
    /**
     * 解密密钥
     */
    private String secretKey;

    public Integer getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Integer datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Integer idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Integer getMaxLifetime() {
        return maxLifetime;
    }

    public void setMaxLifetime(Integer maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public Integer getKeepaliveTime() {
        return keepaliveTime;
    }

    public void setKeepaliveTime(Integer keepaliveTime) {
        this.keepaliveTime = keepaliveTime;
    }
}
