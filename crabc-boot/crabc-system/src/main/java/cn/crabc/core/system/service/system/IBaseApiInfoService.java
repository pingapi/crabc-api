package cn.crabc.core.system.service.system;

import cn.crabc.core.system.entity.dto.ApiInfoDTO;
import cn.crabc.core.system.entity.param.ApiInfoParam;
import cn.crabc.core.system.entity.vo.ApiComboBoxVO;
import cn.crabc.core.system.entity.vo.ApiInfoVO;
import cn.crabc.core.system.entity.BaseApiInfo;
import cn.crabc.core.system.util.PageInfo;

import java.util.List;

/**
 * API 基本信息 服务接口
 *
 * @author yuqf
 */
public interface IBaseApiInfoService {

    /**
     * 获取API全关联数据
     * @return
     */
    List<ApiInfoDTO> getApiDetail();

    /**
     * API 分页列表
     *
     * @param apiName
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<BaseApiInfo> getApiPage(String apiName, int pageNum, int pageSize);

    /**
     * API列表
     *
     * @param apiName
     * @return
     */
    List<BaseApiInfo> getApiList(String apiName);

    /**
     * 分组下API列表
     * @param groupId
     * @return
     */
    List<ApiComboBoxVO> getApiListGroup(Integer groupId);

    /**
     * 根据API地址查询详情
     *
     * @param apiPath
     * @param method
     * @return
     */
    BaseApiInfo getApiInfo(String apiPath, String method);

    /**
     * apiId获取详情
     * @param apiId
     * @return
     */
    BaseApiInfo getApiInfo(Long apiId);

    /**
     * 根据apiId查询详情
     * @param apiId
     * @return
     */
    ApiInfoVO getApiDetail(Long apiId);

    /**
     * 新增API
     *
     * @param apiInfo
     * @return
     */
    Long addApiInfo(ApiInfoParam apiInfo);

    /**
     * 编辑API信息
     *
     * @param apiInfo
     * @return
     */
    Long updateApiInfo(ApiInfoParam apiInfo);

    /**
     * 更新API状态
     * @param apiId
     * @param status
     * @param enabled
     * @return
     */
    Integer updateApiState(Long apiId,String status, Integer enabled);

    /**
     * API发布
     * @param apiInfoParam
     * @return
     */
    Long apiPublish(ApiInfoParam apiInfoParam);

}
