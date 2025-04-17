package org.container.terraman.api.terraman;

import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.TerramanCommandModel;
import org.container.terraman.api.common.service.CommandService;
import org.container.terraman.api.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PortalRequestService {
    private final CommandService commandService;
    private final PropertyService propertyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TerramanProcessService.class);
    public PortalRequestService(CommandService commandService, PropertyService propertyService) {
        this.commandService = commandService;
        this.propertyService = propertyService;
    }

    public void portalRequest(PortalRequestParams portalRequestParams) {
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        String chkCli = "";
        String host = propertyService.getMasterHost();
        String idRsa = TerramanConstant.MASTER_ID_RSA;

        if (portalRequestParams.getTag().equals(Constants.TAG_ROLL_BACK)) {

            terramanCommandModel.setCommand("32");
            terramanCommandModel.setNamespace(portalRequestParams.namespace);
            terramanCommandModel.setResourceName(portalRequestParams.resourceName);
            terramanCommandModel.setHost(host);
            terramanCommandModel.setIdRsa(idRsa);
            terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
            chkCli = commandService.execCommandOutput(terramanCommandModel);
            LOGGER.info("Deployment Rollback!! :: {}", CommonUtils.loggerReplace(chkCli));

        }

    }
}