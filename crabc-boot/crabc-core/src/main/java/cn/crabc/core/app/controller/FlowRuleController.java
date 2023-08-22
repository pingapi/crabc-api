package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.BaseFlowRule;
import cn.crabc.core.app.entity.param.FlowAuthParam;
import cn.crabc.core.app.entity.vo.FlowApiVO;
import cn.crabc.core.app.service.system.IBaseFlowRuleService;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.app.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 限流管理
 *
 * @author yuqf
 */
@RestController
@RequestMapping("/api/box/sys/flow")
public class FlowRuleController {

    @Autowired
    private IBaseFlowRuleService baseFlowRuleService;

    /**
     * 限流规则分页
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result page(String keyword, Integer pageNum, Integer pageSize){
        PageInfo<BaseFlowRule> page = baseFlowRuleService.getFlowPage(keyword, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 关联的API
     * @return
     */
    @GetMapping("/auth/list")
    public Result authList(Integer id){
        FlowApiVO flowAuthApi = baseFlowRuleService.getFlowAuthApi(id);
        return Result.success(flowAuthApi);
    }

    /**
     * 关联API保存
     * @param flowAuth
     * @return
     */
    @PostMapping("/auth")
    public Result atuhApi(@RequestBody FlowAuthParam flowAuth){

        return Result.success(baseFlowRuleService.addFlowApi(flowAuth.getId(), flowAuth.getApiId()));
    }

    /**
     * 删除
     * @param flowId
     * @return
     */
    @DeleteMapping
    public Result delete(Integer flowId){
        baseFlowRuleService.deleteFlow(flowId);
        return Result.success("删除成功");
    }

    /**
     * 新增规则
     * @param rule
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseFlowRule rule){
        baseFlowRuleService.addFlowRule(rule);
        return Result.success("新增成功");
    }

    /**
     * 编辑规则
     * @param rule
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseFlowRule rule){
        baseFlowRuleService.updateFlowRule(rule);
        return Result.success("修改成功");
    }

}
