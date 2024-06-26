package org.container.terraman.api.terraman;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.bcel.Const;
import org.container.terraman.api.common.CommonService;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.VaultService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.ResultStatusModel;
import org.container.terraman.api.common.model.TerramanCommandModel;
import org.container.terraman.api.common.service.*;
import org.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * Terraman Controller 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */
@Api(value = "TerramanController v1")
@RestController
@RequestMapping(value = "/clusters")
public class TerramanController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanController.class);

    private final CommonService commonService;
    private final VaultTemplate vaultTemplate;
    private final TerramanService terramanService;
    private final CommonFileUtils commonFileUtils;
    private final ClusterService clusterService;
    private final ClusterLogService clusterLogService;
    private final VaultService vaultService;
    private final PropertyService propertyService;
    private final TfFileService tfFileService;
    private final CommandService commandService;

    @Value("${master.host}")
    private String MASTER_HOST;

    @Autowired
    public TerramanController(
            CommonService commonService
            , TerramanService terramanService
            , CommonFileUtils commonFileUtils
            , ClusterService clusterService
            , ClusterLogService clusterLogService
            , VaultService vaultService
            , PropertyService propertyService
            , VaultTemplate vaultTemplate
            , TfFileService tfFileService
            , CommandService commandService) {
        this.commonService = commonService;
        this.terramanService = terramanService;
        this.commonFileUtils = commonFileUtils;
        this.clusterService = clusterService;
        this.clusterLogService = clusterLogService;
        this.vaultService = vaultService;
        this.propertyService = propertyService;
        this.vaultTemplate = vaultTemplate;
        this.tfFileService = tfFileService;
        this.commandService = commandService;
    }

    /**
     * Terraman 생성(Create Terraman) - Container 실행
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 생성(Create Terraman) - Container 실행", nickname = "initTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 생성 정보", required = true, dataType = "TerramanRequest", paramType = "body"),
            @ApiImplicitParam(name = "processGb", value = "Terraman 생성 구분", required = false, dataType = "string", paramType = "path")
    })
    @PostMapping(value = "/create/{processGb:.+}")
    public void initTerraman(@RequestBody TerramanRequest terramanRequest, @PathVariable String processGb) throws UnsupportedEncodingException, InterruptedException {
        terramanService.createTerraman(terramanRequest, processGb);
    }

    /**
     * Terraman 생성(Create Terraman) - Daemon 실행
     *
     * @param terramanRequest the terramanRequest
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 생성(Create Terraman) - Daemon 실행", nickname = "initTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 생성 정보", required = true, dataType = "TerramanRequest", paramType = "body")
    })
    @PostMapping(value = "/create")
    public void initTerraman(@RequestBody TerramanRequest terramanRequest) throws UnsupportedEncodingException, InterruptedException {
        terramanService.createTerraman(terramanRequest, "Daemon");
    }

    /**
     * Terraman 상태 체크(Check Terraman Status) - Daemon 실행
     *
     * @return the resultStatus
     */
    @ApiOperation(value = "Terraman 생성(Create Terraman) - Daemon 실행", nickname = "initTerraman")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terramanRequest", value = "Terraman 생성 정보", required = true, dataType = "TerramanRequest", paramType = "body")
    })
    @GetMapping(value = "/check")
    public ResultStatusModel checkTerramanStatus() {
        return new ResultStatusModel(Constants.RESULT_STATUS_SUCCESS);
    }

//    @ApiOperation(value = "Terraman 생성(Create Terraman) - Daemon 실행", nickname = "initTerraman")
//    @GetMapping(value = "/test")
//    public void test() {
//        LOGGER.info("test start");
//        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
//        terramanCommandModel.setClusterId("testClusterId;");
//        terramanCommandModel.setDir("testDir|");
//        terramanCommandModel.setUserName("testUserName&");
//        terramanCommandModel.setIdRsa("testIdRsa\"");
//
//        Field[] fields = terramanCommandModel.getClass().getDeclaredFields();
//        try {
//            for(Field field : fields){
//                field.setAccessible(true);
//                Object obj = field.get(terramanCommandModel);
//                field.set(terramanCommandModel, String.valueOf(obj).replaceAll("[;|&\"]",""));
//            }
//        } catch (Exception e) {
//            LOGGER.info("Exception : {}", e.getMessage());
//        }
//
//        LOGGER.info("change Object :: {}", terramanCommandModel.toString());
//
//    }
}
