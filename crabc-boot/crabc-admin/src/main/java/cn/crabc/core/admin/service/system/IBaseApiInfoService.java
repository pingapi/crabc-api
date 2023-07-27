package cn.crabc.core.admin.service.system;

import cn.crabc.core.admin.entity.BaseApiInfo;
import cn.crabc.core.admin.entity.BaseAppApi;
import cn.crabc.core.admin.entity.dto.ApiInfoDTO;
import cn.crabc.core.admin.entity.param.ApiInfoParam;
import cn.crabc.core.admin.entity.vo.ApiComboBoxVO;
import cn.crabc.core.admin.entity.vo.ApiInfoVO;
import cn.crabc.core.admin.entity.vo.BaseApiExcelVO;
import cn.crabc.core.admin.entity.vo.BaseApiInfoVO;
import cn.crabc.core.admin.util.PageInfo;

import java.util.List;

/**
 * API 基本信息 服务接口
 *
 * @author yuqf
 */
public interface IBaseApiInfoService {

    /**
     * 获取API关联数据
     *
     * @param method
     * @return
     */
    ApiInfoDTO getApiCache(String method, String apiPath, boolean update);

    /**
     * API 分页列表
     *
     * @param apiName
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<BaseApiInfo> getApiPage(String apiName, String devType, int pageNum, int pageSize);

    /**
     * API列表
     *
     * @param apiName
     * @return
     */
    List<BaseApiInfo> getApiList(String apiName);

    /**
     * 分组下API列表
     *
     * @param groupId
     * @return
     */
    List<ApiComboBoxVO> getApiListGroup(Integer groupId);

    /**
     * 分组用户API列表
     *
     * @param userId
     * @return
     */
    List<ApiComboBoxVO> getApiListUser(String userId);

    /**
     * 校验接口URL是否存在
     *
     * @param apiPath
     * @param method
     * @return
     */
    Boolean checkApiPath(Long apiId, String apiPath, String method);

    /**
     * apiId获取详情
     *
     * @param apiId
     * @return
     */
    BaseApiInfoVO getApiDetail(Long apiId);

    /**
     * 根据apiId查询详情
     *
     * @param apiId
     * @return
     */
    ApiInfoVO getApiInfo(Long apiId);

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
     *
     * @param apiId
     * @param status
     * @param enabled
     * @return
     */
    Integer updateApiState(Long apiId, String status, Integer enabled);

    /**
     * 删除API
     * @param apiId
     * @param userId
     * @return
     */
    Integer deleteApi(Long apiId, String userId);

    /**
     * API发布
     *
     * @param apiInfoParam
     * @return
     */
    Long apiPublish(ApiInfoParam apiInfoParam);

    /**
     * 获取未关联应用的API
     *
     * @param appId
     * @return
     */
    PageInfo getNotChooseApi(Long appId, Integer pageNum, Integer pageSize);

    /**
     * 获取已关联应用的API
     *
     * @param appId
     * @return
     */
    PageInfo getChooseApi(Long appId, Integer pageNum, Integer pageSize);

    /**
     * 保存应用API的关系
     *
     * @param appApi
     * @return
     */
    Integer addChooseApi(BaseAppApi appApi);

    /**
     * API基本信息列表
     *
     * @param apiName
     * @return
     */
    List<BaseApiExcelVO> getApiInfoList(String apiName, String devType);

    /**
     * 导入新增API
     * @return
     */
    Long addApiInfo(List<BaseApiExcelVO> list, String type);
}
