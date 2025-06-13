package org.container.terraman.api.terraman;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.container.terraman.api.common.CommonService;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.VaultService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.model.ResultStatusModel;
import org.container.terraman.api.common.service.*;
import org.container.terraman.api.common.util.CommonFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;

/**
 * Terraman Controller 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */
@Tag(name = "TerramanController v1")
@RestController
@RequestMapping( "/clusters")
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
    @Operation(summary = "Terraman 생성(Create Terraman) - Container 실행", operationId = "initTerraman")
    @Parameters({
            @Parameter(name = "terramanRequest", description = "terraman 생성 정보", required = true, schema = @Schema(implementation = TerramanRequest.class)),
            @Parameter(name = "processGb", description = "terraman 생성 구분", required = true)
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
    @Operation(summary = "Terraman 생성(Create Terraman) - Daemon 실행", operationId = "initTerraman")
    @Parameter(name = "terramanRequest", description = "terramanRequest", required = true, schema = @Schema(implementation = TerramanRequest.class))
    @PostMapping(value = "/create")
    public void initTerraman(@RequestBody TerramanRequest terramanRequest) throws UnsupportedEncodingException, InterruptedException {
        terramanService.createTerraman(terramanRequest, "Daemon");
    }

    /**
     * Terraman 상태 체크(Check Terraman Status)
     *
     * @return the resultStatus
     */
    @Operation(summary = "Terraman 상태 체크(Check Terraman Status)", operationId = "checkTerramanStatus")
    @GetMapping(value = "/check")
    public ResultStatusModel checkTerramanStatus() {
        return new ResultStatusModel(Constants.RESULT_STATUS_SUCCESS);
    }

}
