package cn.crabc.core.app.mapper;

import cn.crabc.core.app.entity.BaseApiInfo;
import cn.crabc.core.app.entity.dto.ApiInfoDTO;
import cn.crabc.core.app.entity.vo.ApiComboBoxVO;
import cn.crabc.core.app.entity.vo.BaseApiExcelVO;
import cn.crabc.core.app.entity.vo.BaseApiInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * API基本信息 Mapper接口
 *
 * @author yuqf
 */
@Mapper
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
     * 根据Id查询api基本信息
     *
     * @param apiId
     * @return
     */
    BaseApiInfoVO selectBaseApi(Long apiId);

    /**
     * 校验api地址是否已经存在
     *
     * @param apiPath
     * @param method
     * @return
     */
    Integer checkApiPath(@Param("apiId") Long apiId, @Param("apiPath") String apiPath, @Param("method") String method);


    /**
     * 根据apiId查询信息
     * @param apiId
     * @return
     */
    List<ApiInfoDTO>  selectApiDetail(@Param("apiId") Long apiId);

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

    /**
     * API详情列表
     * @param apiName
     * @return
     */
    List<BaseApiExcelVO> selectApiInfoList(@Param("apiName") String apiName, @Param("devType") String devType);

}
