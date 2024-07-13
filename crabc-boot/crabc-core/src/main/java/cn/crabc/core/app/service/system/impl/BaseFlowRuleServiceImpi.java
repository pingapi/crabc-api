package cn.crabc.core.app.service.system.impl;

import cn.crabc.core.app.entity.BaseFlowRule;
import cn.crabc.core.app.entity.vo.FlowApiVO;
import cn.crabc.core.app.entity.vo.GroupApiVO;
import cn.crabc.core.app.mapper.BaseFlowRuleMapper;
import cn.crabc.core.app.mapper.BaseGroupMapper;
import cn.crabc.core.app.service.system.IBaseFlowRuleService;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.app.util.UserThreadLocal;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 限流策略 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseFlowRuleServiceImpi implements IBaseFlowRuleService {

    @Autowired
    private BaseFlowRuleMapper baseFlowRuleMapper;
    @Autowired
    private BaseGroupMapper baseGroupMapper;

    @PostConstruct
    @Scheduled(cron = "${crabc.corn.flow:0 0/1 * * * ?}")
    public void task() {
        initFlowRule();
    }

    @Override
    public void initFlowRule() {
        // 加载限流规则
        List<FlowRule> rules = new ArrayList<>();
        List<BaseFlowRule> flowList = this.getCacheFlowList();
        for (BaseFlowRule flow : flowList) {
            FlowRule rule = new FlowRule("/api/web/"+flow.getApiPath());
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(flow.getFlowCount());
            rule.setId(flow.getApiId());
            rules.add(rule);
        }
        FlowRuleManager.loadRules(rules);
    }

    @Override
    public List<BaseFlowRule> getCacheFlowList() {
        return baseFlowRuleMapper.selectFlows();
    }

    @Override
    public PageInfo<BaseFlowRule> getFlowPage(String flowName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BaseFlowRule> list = baseFlowRuleMapper.selectList(flowName);
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    public FlowApiVO getFlowAuthApi(Integer flowId) {
        FlowApiVO flowApi = new FlowApiVO();
        List<GroupApiVO> groupAllApi = baseGroupMapper.selectGroupApi();
        List<GroupApiVO> groupApi = baseFlowRuleMapper.selectGroupApi(flowId);
        // 删除已授权的API
        groupAllApi.removeAll(groupApi);
        flowApi.setLeft(this.getChildApi(groupAllApi));
        flowApi.setRight(this.getChildApi(groupApi));
        return flowApi;
    }

    /**
     * 获取分组子集API
     * @param groupApiList
     * @return
     */
    private List<GroupApiVO> getChildApi(List<GroupApiVO> groupApiList){
        Map<Integer, List<GroupApiVO>> groupMap = groupApiList.stream().collect(Collectors.groupingBy(GroupApiVO::getGroupId));
        List<GroupApiVO> list = new ArrayList<>();
        for (Integer groupId : groupMap.keySet()) {
            List<GroupApiVO> groupApis = groupMap.get(groupId);
            GroupApiVO parentGroup = new GroupApiVO();
            parentGroup.setGroupId(groupId);
            parentGroup.setGroupName(groupApis.get(0).getGroupName());
            parentGroup.setChild(groupApis);
            list.add(parentGroup);
        }
        return list;
    }

    @Override
    public Integer deleteFlow(Integer flowId) {
        return baseFlowRuleMapper.delete(flowId);
    }

    @Override
    public Integer addFlowRule(BaseFlowRule flowRule) {
        String userId = UserThreadLocal.getUserId();
        Date date = new Date();
        flowRule.setCreateBy(userId);
        flowRule.setCreateTime(date);
        flowRule.setUpdateTime(date);
        flowRule.setFlowType("flow");
        return baseFlowRuleMapper.insert(flowRule);
    }

    @Override
    public Integer updateFlowRule(BaseFlowRule flowRule) {
        String userId = UserThreadLocal.getUserId();
        Date date = new Date();
        flowRule.setUpdateBy(userId);
        flowRule.setUpdateTime(date);
        return baseFlowRuleMapper.update(flowRule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addFlowApi(Integer flowId, List<Integer> apiIds) {
        String userId = UserThreadLocal.getUserId();
        baseFlowRuleMapper.deleteFlowApi(flowId);
        if (!apiIds.isEmpty()) {
            baseFlowRuleMapper.insertFlowApi(flowId, userId, apiIds);
        }
        return 1;
    }
}
