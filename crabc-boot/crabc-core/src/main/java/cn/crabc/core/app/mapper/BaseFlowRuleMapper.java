package cn.crabc.core.app.mapper;

import cn.crabc.core.app.entity.BaseFlowRule;
import cn.crabc.core.app.entity.vo.GroupApiVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 限流策略 Mapper接口
 *
 * @author yuqf
 */
public interface BaseFlowRuleMapper {


    /**
     * 查询规则列表
     * @return
     */
    List<BaseFlowRule> selectList(@Param("flowName") String flowName);

    /**
     * 获取缓存限流api
     * @return
     */
    List<BaseFlowRule> selectFlows();

    /**
     * 获取分组下限流API
     * @return
     */
    List<GroupApiVO> selectGroupApi(@Param("flowId") Integer flowId);

    /**
     * 添加规则
     * @param rule
     * @return
     */
    Integer insert(BaseFlowRule rule);

    /**
     * 删除
     * @param flowId
     * @return
     */
    Integer delete(@Param("flowId") Integer flowId);

    /**
     * 编辑
     * @param rule
     * @return
     */
    Integer update(BaseFlowRule rule);

    /**
     * 新增关联API
     * @param flowId
     * @param list
     * @return
     */
    Integer insertFlowApi(@Param("flowId") Integer flowId, @Param("userId") String userId, @Param("list") List<Integer> list);

    /**
     * 删除关联API
     * @param flowId
     * @return
     */
    Integer deleteFlowApi(@Param("flowId") Integer flowId);
}
