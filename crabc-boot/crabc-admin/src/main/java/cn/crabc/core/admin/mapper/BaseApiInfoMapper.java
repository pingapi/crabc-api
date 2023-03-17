package cn.crabc.core.admin.mapper;

import cn.crabc.core.admin.entity.BaseApiInfo;
import cn.crabc.core.admin.entity.dto.ApiInfoDTO;
import cn.crabc.core.admin.entity.vo.ApiComboBoxVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * API基本信息 Mapper接口
 *
 * @author yuqf
 */
public interface BaseApiInfoMapper {

    /**
     * API列表
     *
     * @param apiName
     * @return
     */
    List<BaseApiInfo> selectList(@Param("apiName") String apiName, @Param("devType") String devType);

    /**
     * 分组查询API信息
     *
     * @param groupId
     * @return
     */
    List<ApiComboBoxVO> selectApiGroup(@Param("groupId") Integer groupId, @Param("userId") String userId);

    /**
     * 查询关联应用的API
     *
     * @param appId
     * @return
     */
    List<ApiComboBoxVO> selectApiApp(Long appId);

    /**
     * 根据Id查询基本信息
     *
     * @param apiId
     * @return
     */
    BaseApiInfo selectApiById(Long apiId);

    /**
     * 校验api地址是否已经存在
     *
     * @param apiPath
     * @param method
     * @return
     */
    Integer checkApiPath(@Param("apiId") Long apiId, @Param("apiPath") String apiPath, @Param("method") String method);


    List<ApiInfoDTO> selectApiDetail(Long apiId);

    /**
     * 插入API
     *
     * @param apiInfo
     * @return
     */
    Integer insertApiInfo(BaseApiInfo apiInfo);

    /**
     * 编辑API信息
     *
     * @param apiInfo
     * @return
     */
    Integer updateApiInfo(BaseApiInfo apiInfo);

    /**
     * 更新API状态
     *
     * @param apiInfo
     * @return
     */
    Integer updateApiState(BaseApiInfo apiInfo);

    /**
     * 删除API
     * @param apiId
     * @return
     */
    Integer deleteApiInfo(@Param("apiId") Long apiId,@Param("userId") String userId);

    /**
     * API校验统计
     *
     * @param apiId
     * @return
     */
    Integer countApi(Long apiId);
}
