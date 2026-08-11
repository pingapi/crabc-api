package cn.crabc.core.app.service.system;

import cn.crabc.core.app.entity.BaseApiLog;
import cn.crabc.core.app.entity.param.ApiLogParam;
import cn.crabc.core.app.entity.vo.ApiLogDailyTrendVO;
import cn.crabc.core.app.entity.vo.ApiLogGroupCountVO;
import cn.crabc.core.app.entity.vo.ApiLogStatisticsVO;
import cn.crabc.core.app.entity.vo.ApiLogSummaryVO;
import cn.crabc.core.datasource.util.PageInfo;

import java.util.List;


/**
 * API日志 服务接口
 *
 * @author yuqf
 */
public interface IBaseApiLogService {

    /**
     * 添加日志
     *
     * @param log
     * @return
     */
    Integer addLog(BaseApiLog log);

    /**
     * 批量添加日志
     *
     * @param logs
     * @return
     */
    Integer batchAddLog(List<BaseApiLog> logs);

    /**
     * 日志列表
     *
     * @param param
     * @return
     */
    PageInfo page(ApiLogParam param);

    /**
     * 日志详情
     *
     * @param logId
     * @return
     */
    BaseApiLog logDetail(Long logId);

    /**
     * API日志统计摘要
     *
     * @param param 查询条件，未传时间时默认近七天，避免统计接口扫描全量日志
     * @return 监控页顶部指标卡数据
     */
    ApiLogSummaryVO summary(ApiLogParam param);

    /**
     * API日志每日趋势
     *
     * @param param 查询条件，未传时间时默认近七天，避免统计接口扫描全量日志
     * @return 按天聚合的调用趋势
     */
    List<ApiLogDailyTrendVO> dailyTrend(ApiLogParam param);

    /**
     * API日志请求结果占比
     *
     * @param param 查询条件，未传时间时默认近七天，避免统计接口扫描全量日志
     * @return success/fail等状态分组计数
     */
    List<ApiLogGroupCountVO> statusPie(ApiLogParam param);

    /**
     * API接口调用量Top10
     *
     * @param param 查询条件，未传时间时默认近七天，避免统计接口扫描全量日志
     * @return 按接口聚合的调用量Top10
     */
    List<ApiLogGroupCountVO> topApis(ApiLogParam param);

    /**
     * API接口耗时Top10
     *
     * @param param 查询条件，服务层会固定成功状态，只统计成功调用的耗时
     * @return 按接口平均耗时聚合的成功调用Top10
     */
    List<ApiLogGroupCountVO> topCostApis(ApiLogParam param);

    /**
     * IP访问量Top10
     *
     * @param param 查询条件，未传时间时默认近七天，避免统计接口扫描全量日志
     * @return 按request_ip分组聚合的访问量Top10
     */
    List<ApiLogGroupCountVO> topIps(ApiLogParam param);

    /**
     * API日志统计兼容接口
     *
     * @param param 查询条件，未传时间时默认近七天，避免统计接口扫描全量日志
     * @return 监控页统计指标和图表数据
     */
    ApiLogStatisticsVO statistics(ApiLogParam param);
}
