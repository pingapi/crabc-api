package cn.crabc.core.app.mapper;

import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.app.entity.vo.RequestParamsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * API参数 Mapper接口
 *
 * @author yuqf
 */
public interface BaseApiParamMapper {

    /**
     * API请求参数
     * @param apiId
     * @return
     */
    List<BaseApiParam> selectReqParams(Long apiId);

    /**
     * 查询API参数列表
     * @param apiId
     * @return
     */
    List<BaseApiParam> selectList(Long apiId);

    /**
     * 查询api详情参数
     * @param apiId
     * @return
     */
    List<RequestParamsVO> selectApiParams(Long apiId);

    /**
     * 新增参数
     * @param param
     * @return
     */
    Long insert(BaseApiParam param);

    /**
     * 批量新增
     * @param list
     * @return
     */
    Long insertBatch(@Param("list") List<BaseApiParam> list, @Param("apiId") Long apiId);

    /**
     * 删除API参数
     * @param apiId
     * @return
     */
    Integer delete(Long apiId);
}
