package cn.crabc.core.admin.service.system;

import cn.crabc.core.admin.entity.BaseDatasource;
import cn.crabc.core.admin.util.PageInfo;
import cn.crabc.core.spi.bean.BaseDataSource;

import java.util.List;

/**
 * 数据源 服务接口
 *
 * @author yuqf
 */
public interface IBaseDataSourceService {

    /**
     * 数据源列表
     * @return
     */
    List<BaseDataSource> getList();

    /**
     * 数据源 分页列表
     * @param dataSourceName
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<BaseDatasource> getDataSourcePage(String dataSourceName, int pageNum, int pageSize);

    /**
     * 数据源列表
     * @param dataSourceName
     * @return
     */
    List<BaseDatasource> getDataSourceList(String dataSourceName);

    /**
     * 查询数据源详情
     * @param dataSourceId
     * @return
     */
    BaseDatasource getDataSource(Integer dataSourceId);

    /**
     *  添加数据源
     * @param dataSource
     * @return
     */
    Integer addDataSource(BaseDatasource dataSource);
    /**
     * 更新数据源
     * @param dataSource
     * @return
     */
    Integer updateDataSource(BaseDatasource dataSource);
    /**
     * 删除数据源
     * @param dataSourceId
     * @return
     */
    Integer deleteDataSource(Integer dataSourceId);

    /**
     * 测试数据源连接
     * @param dataSource
     * @return 1(成功) 或 errorMessage
     */
    String test(BaseDatasource dataSource);
}
