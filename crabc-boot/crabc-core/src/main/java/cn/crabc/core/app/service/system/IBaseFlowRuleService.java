package cn.crabc.core.app.service.system;

import cn.crabc.core.app.entity.BaseFlowRule;
import cn.crabc.core.app.entity.vo.FlowApiVO;
import cn.crabc.core.datasource.util.PageInfo;

import java.util.List;

/**
 * 限流策略 服务接口
 *
 * @author yuqf
 */
public interface IBaseFlowRuleService {

    /**
     * 初始化限流规则
     */
    void initFlowRule();

    /**
     * 缓存限流规则json
     * @return
     */
    List<BaseFlowRule> getCacheFlowList();

    /**
     * 限流规则分页列表
     * @param flowName
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<BaseFlowRule> getFlowPage(String flowName, Integer pageNum, Integer pageSize);

    /**
     * 限流关联的API列表
     * @return
     */
    FlowApiVO getFlowAuthApi(Integer flowId);

    /**
     * 删除
     * @param flowId
     * @return
     */
    Integer deleteFlow(Integer flowId);

    /**
     * 新增限流规则
     * @param flowRule
     * @return
     */
    Integer addFlowRule(BaseFlowRule flowRule);

    /**
     * 编辑限流规则
     * @param flowRule
     * @return
     */
    Integer updateFlowRule(BaseFlowRule flowRule);

    /**
     * 保存规则关联API
     * @param flowId
     * @param apiIds
     * @return
     */
    Integer addFlowApi(Integer flowId, List<Integer> apiIds);
}
