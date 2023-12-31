package org.container.terraman.api.common.service;

import org.apache.commons.lang3.StringUtils;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.VaultService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.AccountModel;
import org.container.terraman.api.common.model.FileModel;
import org.container.terraman.api.common.model.TerramanCommandModel;
import org.container.terraman.api.common.terramanproc.TerramanFileProcess;
import org.container.terraman.api.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("PATH_TRAVERSAL_IN")
@Service
public class TfFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TfFileService.class);

    private final VaultService vaultService;
    private final AccountService accountService;
    private final CommandService commandService;
    private final PropertyService propertyService;

    @Autowired
    public TfFileService(
            VaultService vaultService
            , CommandService commandService
            , AccountService accountService
            , PropertyService propertyService
    ) {
        this.vaultService = vaultService;
        this.commandService = commandService;
        this.accountService = accountService;
        this.propertyService = propertyService;
    }

    /**
     * terraform provider.tf 파일 생성 및 작성 (String)
     *
     * @param clusterId the clusterId
     * @param provider the provider
     * @param seq the seq
     * @param pod the pod
     * @param host the host
     * @param idRsa the idRsa
     * @param processGb the processGb
     * @return the String
     */
    public String createProviderFile(String clusterId, String provider, int seq, String pod, String host, String idRsa, String processGb) {
        String resultCode = Constants.RESULT_STATUS_FAIL;

        String path = propertyService.getVaultBase();
        if (provider.equalsIgnoreCase(Constants.UPPER_NCLOUD)){
            path = path + Constants.UPPER_NAVER.toUpperCase() + Constants.DIV + seq;
        } else {
            path = path + provider.toUpperCase() + Constants.DIV + seq;
        }

        HashMap<String, Object> res = vaultService.read(path, HashMap.class);
        AccountModel account = accountService.getAccountInfo(seq);
        FileModel fileModel = new FileModel();
        String resultFile = "";

        String awsAccessKey = "";
        String awsSecretKey = "";

        String vsphereUser = "";
        String vspherePassword = "";
        String vsphereVsphereServer = "";

        String openstackPassword = "";
        String openstackAuthUrl = "";
        String openstackUserName = "";

        String nhnPassword = "";
        String nhnAuthUrl = "";
        String nhnUserName = "";

        String ncloudAccessKey = "";
        String ncloudSecretKey = "";
        String ncloudSupportVpc = "";

        switch(provider.toUpperCase()) {
            case Constants.UPPER_AWS :
                awsAccessKey = res != null ? String.valueOf(res.get("accessKey")) : "";
                awsSecretKey = res != null ? String.valueOf(res.get("secretKey")) : "";
                fileModel.setAwsAccessKey(awsAccessKey);
                fileModel.setAwsSecretKey(awsSecretKey);
                fileModel.setAwsRegion(account.getRegion());
                break;
            case Constants.UPPER_GCP :
                LOGGER.error("{} is Cloud not supported.", CommonUtils.loggerReplace(provider));
                break;
            case Constants.UPPER_VSPHERE :
                vsphereUser = res != null ? String.valueOf(res.get("user")) : "";
                vspherePassword = res != null ? String.valueOf(res.get("password")) : "";
                vsphereVsphereServer = res != null ? String.valueOf(res.get("vsphere_server")) : "";
                fileModel.setVSphereUser(vsphereUser);
                fileModel.setVSpherePassword(vspherePassword);
                fileModel.setVSphereServer(vsphereVsphereServer);
                break;
            case Constants.UPPER_OPENSTACK:
                openstackPassword = res != null ? String.valueOf(res.get("password")) : "";
                openstackAuthUrl = res != null ? String.valueOf(res.get("auth_url")) : "";
                openstackUserName = res != null ? String.valueOf(res.get("user_name")) : "";
                fileModel.setOpenstackTenantName(account.getProject());
                fileModel.setOpenstackPassword(openstackPassword);
                fileModel.setOpenstackAuthUrl(openstackAuthUrl);
                fileModel.setOpenstackUserName(openstackUserName);
                fileModel.setOpenstackRegion(account.getRegion());
                break;
            case Constants.UPPER_NHN:
                nhnPassword = res != null ? String.valueOf(res.get("password")) : "";
                nhnAuthUrl = res != null ? String.valueOf(res.get("auth_url")) : "";
                nhnUserName = res != null ? String.valueOf(res.get("user_name")) : "";
                fileModel.setNhnTenantName(account.getProject());
                fileModel.setNhnPassword(nhnPassword);
                fileModel.setNhnAuthUrl(nhnAuthUrl);
                fileModel.setNhnUserName(nhnUserName);
                fileModel.setNhnRegion(account.getRegion());
                break;
            case Constants.UPPER_NCLOUD:
                ncloudAccessKey = res != null ? String.valueOf(res.get("accessKey")) : "";
                ncloudSecretKey = res != null ? String.valueOf(res.get("secretKey")) : "";
                fileModel.setNcloudAccessKey(ncloudAccessKey);
                fileModel.setNcloudSecretKey(ncloudSecretKey);
                fileModel.setNcloudSite(account.getSite());
                fileModel.setNcloudSupportVpc(ncloudSupportVpc);
                fileModel.setNcloudRegion(account.getRegion());
                break;
            default :
                LOGGER.error("{} is Cloud not supported.", CommonUtils.loggerReplace(provider));
                return Constants.RESULT_STATUS_FAIL;
        }

        resultFile = new TerramanFileProcess().createTfFileDiv(fileModel, clusterId, processGb, provider.toUpperCase());

        if(StringUtils.equals(resultFile, Constants.RESULT_STATUS_SUCCESS)) {
            if(!StringUtils.isBlank(idRsa) && !StringUtils.isBlank(host)) {
                if(StringUtils.isNotBlank(clusterId)) {
                    File uploadfile = new File( TerramanConstant.FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId)) ); // 파일 객체 생성
                    commandService.sshFileUpload(TerramanConstant.MOVE_DIR_CLUSTER(clusterId), host, idRsa, uploadfile, TerramanConstant.DEFAULT_USER_NAME);
                }
            }
            TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
            terramanCommandModel.setCommand("14");
            terramanCommandModel.setDir("");
            terramanCommandModel.setHost(host);
            terramanCommandModel.setIdRsa(idRsa);
            terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
            terramanCommandModel.setClusterId(clusterId);
            terramanCommandModel.setPod(pod);
            resultCode = commandService.execCommandOutput(terramanCommandModel);
            if(!StringUtils.equals(Constants.RESULT_STATUS_FAIL, resultCode)) {
                resultCode = Constants.RESULT_STATUS_SUCCESS;
                LOGGER.info("인스턴스 파일 복사가 완료되었습니다. : {}", CommonUtils.loggerReplace(resultCode));
            }
        }
        return resultCode;
    }
}
