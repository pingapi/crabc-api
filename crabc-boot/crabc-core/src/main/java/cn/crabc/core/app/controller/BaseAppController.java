package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.BaseApp;
import cn.crabc.core.app.entity.vo.BaseAppExcelVO;
import cn.crabc.core.app.service.system.IBaseAppService;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.app.util.Result;
import com.alibaba.excel.EasyExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *  应用管理
 *
 */
@RestController
@RequestMapping("/api/box/sys/app")
public class BaseAppController {

    @Autowired
    private IBaseAppService iBaseAppService;

    @GetMapping("/page")
    public Result page(String appName, String appCode, Integer pageNum, Integer pageSize){
        PageInfo<BaseApp> page = iBaseAppService.appPage(appName, appCode, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 应用列表
     * @param appName
     * @return
     */
    @GetMapping("/list")
    public Result list(String appName){
        List<BaseApp> appList = iBaseAppService.appList(appName);
        return Result.success(appList);
    }

    /**
     * 新增应用
     * @param baseApp
     * @return
     */
    @PostMapping
    public Result addApp(@RequestBody BaseApp baseApp){
        return Result.success(iBaseAppService.addApp(baseApp));
    }

    /**
     * 编辑应用
     * @param baseApp
     * @return
     */
    @PutMapping
    public Result updateApp(@RequestBody BaseApp baseApp){
        return Result.success(iBaseAppService.updateApp(baseApp));
    }

    /**
     * 修改状态
     * @param baseApp
     * @return
     */
    @PutMapping("/state")
    public Result updateState(@RequestBody BaseApp baseApp){
        return Result.success(iBaseAppService.updateApp(baseApp));
    }

    /**
     * 删除应用
     * @param appId
     * @return
     */
    @DeleteMapping("/{appId}")
    public Result deleteApp(@PathVariable Long appId){
        return Result.success(iBaseAppService.deleteApp(appId));
    }

    /**
     * 应用列表导出
     * @param appName
     * @param response
     */
    @GetMapping("/export")
    public void apiExport(String appName, HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.ms-excel");
            String fileName = "应用列表";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<BaseAppExcelVO> data = iBaseAppService.getAppExcelList(appName);
            EasyExcel.write(response.getOutputStream(), BaseAppExcelVO.class).sheet(fileName).doWrite(data);
        }catch (Exception e) {
            new RuntimeException("导出异常");
        }
    }

    /**
     * 导入应用列表
     * @param file
     */
    @PostMapping("/import")
    public void apiImport(@RequestParam MultipartFile file) throws IOException {
        List<BaseAppExcelVO> list = EasyExcel.read(file.getInputStream())
                .head(BaseAppExcelVO.class)
                .sheet()
                .doReadSync();
        iBaseAppService.addAppList(list);
    }

}
