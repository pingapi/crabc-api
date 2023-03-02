package cn.crabc.core.system.mapper;


import cn.crabc.core.system.entity.BaseDatasource;
import cn.crabc.core.spi.bean.BaseDataSource;

import java.util.List;

/**
 * 数据源 Mapper接口
 *
 * @author yuqf
 */
public interface BaseDataSourceMapper {

    /**
     *
     * 数据源列表
     * @param dataSourceName
     * @return
     */
    List<BaseDataSource> selectDataSourceList(String dataSourceName);

    /**
     * 查询数据源详情
     * @param datasourceId
     * @return
     */
    BaseDataSource selectDataSource(Integer datasourceId);

    /**
     *  插入数据源
     * @param dataSource
     * @return
     */
    Integer insertDataSource(BaseDatasource dataSource);

    /**
     * 更新数据源
     * @param dataSource
     * @return
     */
    Integer updateDataSource(BaseDatasource dataSource);

    /**
     * 删除数据源
     * @param datasourceId
     * @return
     */
    Integer deleteDataSource(Integer datasourceId);
}
