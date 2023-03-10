package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.app.exception.CustomException;
import cn.crabc.core.system.entity.BaseApiInfo;
import cn.crabc.core.system.entity.BaseApiSql;
import cn.crabc.core.system.entity.BaseApp;
import cn.crabc.core.system.entity.BaseAppApi;
import cn.crabc.core.system.entity.dto.ApiInfoDTO;
import cn.crabc.core.system.entity.param.ApiInfoParam;
import cn.crabc.core.system.entity.vo.ApiComboBoxVO;
import cn.crabc.core.system.entity.vo.ApiInfoVO;
import cn.crabc.core.system.enums.ApiStateEnum;
import cn.crabc.core.system.mapper.BaseApiInfoMapper;
import cn.crabc.core.system.mapper.BaseApiSqlMapper;
import cn.crabc.core.system.mapper.BaseAppApiMapper;
import cn.crabc.core.system.mapper.BaseAppMapper;
import cn.crabc.core.system.service.system.IBaseApiInfoService;
import cn.crabc.core.system.util.PageInfo;
import cn.crabc.core.system.util.UserThreadLocal;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API基本信息 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseApiInfoServiceImpl implements IBaseApiInfoService {

    @Autowired
    private BaseApiInfoMapper apiInfoMapper;
    @Autowired
    private BaseApiSqlMapper apiSqlMapper;
    @Autowired
    private BaseAppMapper baseAppMapper;
    @Autowired
    private BaseAppApiMapper baseAppApiMapper;
    @Autowired
    @Qualifier("apiCache")
    Cache<String, Object> apiInfoCache;

    @Override
    public void initApi() {
        List<ApiInfoDTO> apis = this.getApiCache(null);
        for (ApiInfoDTO api : apis) {
            apiInfoCache.put(api.getApiMethod() + "_" + api.getApiPath(), api);
        }
    }

    @Override
    public List<ApiInfoDTO> getApiCache(Long apiId) {
        List<ApiInfoDTO> apiInfos = apiInfoMapper.selectApiDetail(apiId);
        if (apiInfos.size() == 0) {
            return apiInfos;
        }
        List<BaseApp> appApis = baseAppMapper.selectApiApp(apiId);
        Map<Long, List<BaseApp>> appMap = appApis.stream().collect(Collectors.groupingBy(BaseApp::getApiId));
        for (ApiInfoDTO api : apiInfos) {
            if (appMap.containsKey(api.getApiId())) {
                api.setAppList(appMap.get(api.getApiId()));
            }
        }

        if (apiId != null && apiInfos.size() > 0) {
            ApiInfoDTO api = apiInfos.get(0);
            apiInfoCache.put(api.getApiMethod() + "_" + api.getApiPath(), api);
        }
        return apiInfos;
    }

    @Override
    public PageInfo<BaseApiInfo> getApiPage(String apiName, String devType, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BaseApiInfo> list = apiInfoMapper.selectList(apiName, devType);
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    public List<BaseApiInfo> getApiList(String apiName) {
        return apiInfoMapper.selectList(apiName, null);
    }

    @Override
    public List<ApiComboBoxVO> getApiListGroup(Integer groupId) {
        return apiInfoMapper.selectApiGroup(groupId);
    }

    @Override
    public BaseApiInfo getApiInfo(String apiPath, String method) {
        return apiInfoMapper.selectApiInfo(apiPath, method);
    }

    @Override
    public BaseApiInfo getApiInfo(Long apiId) {
        return apiInfoMapper.selectApiById(apiId);
    }

    @Override
    public ApiInfoVO getApiDetail(Long apiId) {
        ApiInfoVO result = new ApiInfoVO();
        BaseApiInfo baseApiInfo = apiInfoMapper.selectApiById(apiId);
        BaseApiSql baseApiSql = apiSqlMapper.selectApiSql(apiId);
        result.setSqlInfo(baseApiSql);
        baseApiInfo.setPageSetup(baseApiSql.getPageSetup());
        result.setBaseInfo(baseApiInfo);
        result.setQueryEngine("jdbc");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addApiInfo(ApiInfoParam apiInfo) {
        Date date = new Date();
        BaseApiInfo baseApiInfo = apiInfo.getBaseInfo();
        baseApiInfo.setApiStatus(ApiStateEnum.EDIT.getName());
        baseApiInfo.setEnabled(0);
        baseApiInfo.setApiType("SQL");
        baseApiInfo.setCreateTime(date);
        baseApiInfo.setUpdateBy(UserThreadLocal.getUserId());
        apiInfoMapper.insertApiInfo(baseApiInfo);

        BaseApiSql baseApiSql = apiInfo.getSqlInfo();
        baseApiSql.setApiId(baseApiInfo.getApiId());
        baseApiSql.setPageSetup(baseApiInfo.getPageSetup());
        baseApiSql.setUpdateTime(date);
        baseApiSql.setUpdateBy(UserThreadLocal.getUserId());
        apiSqlMapper.insertApiSql(baseApiSql);
        return baseApiInfo.getApiId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateApiInfo(ApiInfoParam apiInfo) {
        Integer count = apiInfoMapper.countApi(apiInfo.getBaseInfo().getApiId());
        if (count == 0) {
            throw new CustomException(52002, "API不存在");
        }
        BaseApiInfo baseApiInfo = apiInfo.getBaseInfo();
        Date updateTime = new Date();
        baseApiInfo.setUpdateTime(updateTime);
        baseApiInfo.setApiStatus(ApiStateEnum.EDIT.getName());
        baseApiInfo.setEnabled(0);
        baseApiInfo.setApiType("SQL");
        baseApiInfo.setUpdateBy(UserThreadLocal.getUserId());
        apiInfoMapper.updateApiInfo(baseApiInfo);

        BaseApiSql baseApiSql = apiInfo.getSqlInfo();
        baseApiSql.setUpdateTime(updateTime);
        baseApiSql.setUpdateBy(UserThreadLocal.getUserId());
        baseApiSql.setPageSetup(baseApiInfo.getPageSetup());
        apiSqlMapper.updateApiSql(baseApiSql);
        return apiInfo.getSqlInfo().getApiId();
    }

    @Override
    public Integer updateApiState(Long apiId, String status, Integer enabled) {
        Integer count = apiInfoMapper.countApi(apiId);
        if (count == 0) {
            throw new CustomException(52002, "API不存在");
        }
        BaseApiInfo baseApiInfo = new BaseApiInfo();
        baseApiInfo.setApiId(apiId);
        baseApiInfo.setUpdateTime(new Date());
        baseApiInfo.setUpdateBy(UserThreadLocal.getUserId());
        if (status != null && !"".equals(status)) {
            baseApiInfo.setApiStatus(status);
        }
        if (enabled != null) {
            baseApiInfo.setEnabled(enabled);
        }
        apiInfoMapper.updateApiState(baseApiInfo);
        return 1;
    }

    @Override
    public Long apiPublish(ApiInfoParam apiInfoParam) {
        Long apiId = apiInfoParam.getBaseInfo().getApiId();
        BaseApiInfo oldApiInfo = apiInfoMapper.selectApiById(apiId);
        if (oldApiInfo == null) {
            throw new CustomException(52002, "API不存在");
        }
        if (apiInfoParam.getBaseInfo().getVersion().equals(oldApiInfo.getVersion())) {
            throw new CustomException(52003, "API版本号[" + oldApiInfo.getVersion() + "]已存在，请变更版本号");
        }
        // 已存在发布的版本则保存一条历史数据
        if (ApiStateEnum.RELEASE.getName().equals(oldApiInfo.getApiStatus())) {
            this.insertHistory(oldApiInfo);
        }
        Date updateTime = new Date();
        BaseApiInfo baseApiInfo = apiInfoParam.getBaseInfo();
        baseApiInfo.setEnabled(1);
        baseApiInfo.setParentId(0L);
        baseApiInfo.setApiType("SQL");
        baseApiInfo.setReleaseTime(updateTime);
        baseApiInfo.setUpdateBy(UserThreadLocal.getUserId());
        baseApiInfo.setApiStatus(ApiStateEnum.RELEASE.getName());
        apiInfoMapper.updateApiInfo(baseApiInfo);

        BaseApiSql baseApiSql = apiInfoParam.getSqlInfo();
        baseApiSql.setUpdateTime(updateTime);
        baseApiSql.setUpdateBy(UserThreadLocal.getUserId());
        apiSqlMapper.updateApiSql(baseApiSql);
        // 更新缓存
        this.getApiCache(baseApiInfo.getApiId());
        return apiId;
    }

    @Override
    public PageInfo getNotChooseApi(Long appId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ApiComboBoxVO> allApi = apiInfoMapper.selectApiApp(null);
        List<ApiComboBoxVO> appApis = apiInfoMapper.selectApiApp(appId);
        allApi.removeAll(appApis);
        return new PageInfo<>(allApi, pageNum, pageSize);
    }

    @Override
    public PageInfo getChooseApi(Long appId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ApiComboBoxVO> list = apiInfoMapper.selectApiApp(appId);
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addChooseApi(BaseAppApi appApi) {
        String userId = UserThreadLocal.getUserId();
        baseAppApiMapper.delete(appApi.getAppId(), userId);

        if (appApi.getApiIds() == null || appApi.getApiIds().size() == 0) {
            return 1;
        }
        Date time = new Date();
        List<BaseAppApi> list = new ArrayList<>();
        for (Long apiId : appApi.getApiIds()) {
            BaseAppApi a = new BaseAppApi();
            a.setAppId(appApi.getAppId());
            a.setApiId(apiId);
            a.setCreateBy(userId);
            a.setCreateTime(time);
            list.add(a);
        }
        return baseAppApiMapper.insert(list);
    }

    /**
     * api信息历史发布版本存档
     *
     * @param apiInfo
     */
    public void insertHistory(BaseApiInfo apiInfo) {
        Long apiId = apiInfo.getApiId();
        Date updateTime = new Date();
        apiInfo.setApiStatus(ApiStateEnum.HISTORY.getName());
        apiInfo.setParentId(apiId);
        apiInfo.setApiPath(apiInfo.getApiPath() + "/" + apiInfo.getVersion());
        apiInfo.setUpdateTime(updateTime);
        apiInfo.setApiId(null);
        apiInfoMapper.insertApiInfo(apiInfo);

        BaseApiSql oldApiSql = apiSqlMapper.selectApiSql(apiId);
        oldApiSql.setSqlId(null);
        oldApiSql.setUpdateTime(updateTime);
        oldApiSql.setApiId(apiInfo.getApiId());
        apiSqlMapper.insertApiSql(oldApiSql);
    }
}
