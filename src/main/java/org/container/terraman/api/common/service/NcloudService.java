package org.container.terraman.api.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.container.terraman.api.common.CommonService;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.VaultService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class NcloudService {


    private static final Logger LOGGER = LoggerFactory.getLogger(TfFileService.class);
    private final VaultService vaultService;
    private final AccountService accountService;
    private final PropertyService propertyService;
    private final InstanceService instanceService;
    private final CommonService commonService;
    private final CommandService commandService;
    private final ClusterService clusterService;
    private final ClusterLogService clusterLogService;


    public NcloudService(VaultService vaultService,
                         AccountService accountService,
                         PropertyService propertyService,
                         InstanceService instanceService,
                         CommonService commonService,
                         CommandService commandService,
                         ClusterService clusterService,
                         ClusterLogService clusterLogService) {
        this.vaultService = vaultService;
        this.accountService = accountService;
        this.propertyService = propertyService;
        this.instanceService = instanceService;
        this.commonService = commonService;
        this.commandService = commandService;
        this.clusterService = clusterService;
        this.clusterLogService = clusterLogService;
    }

    /**
     * Select Ncloud SSH Key
     *
     * @param clusterId the clusterId
     * @param provider  the provider
     * @param host      the host
     * @param idRsa     the idRsa
     * @param processGb the processGb
     * @param seq       the seq
     * @return the NcloudInstanceKeyModel
     */
    public List<NcloudInstanceKeyModel> getNcloudSSHKey(String clusterId, String provider, String host, String idRsa, String processGb, int seq) throws UnsupportedEncodingException {
        URI uri = null;
        long timestamp = new Date().getTime();
        String path = propertyService.getVaultBase();
        if (provider.equalsIgnoreCase(Constants.UPPER_NCLOUD)){
            path = path + Constants.UPPER_NAVER.toUpperCase() + Constants.DIV + seq;
        } else {
            path = path + provider.toUpperCase() + Constants.DIV + seq;
        }
        HashMap<String, Object> res = vaultService.read(path, HashMap.class);
        AccountModel account = accountService.getAccountInfo(seq);
        String reqUrl = propertyService.getNcloudRootPasswordApiUrl();
        uri = URI.create(reqUrl);
        String requestURI = uri.getPath();

        List<NcloudPrivateKeyModel> ncloudPrivateKeysModel = instanceService.getNcloudPrivateKeys(clusterId, provider, host, idRsa, processGb);
        NcloudAuthKeyModel ncloudAuthKeyModel = new NcloudAuthKeyModel("", "");
        NcloudInstanceKeyInfoModel ncloudInstanceKeyInfoModel = new NcloudInstanceKeyInfoModel("", "", "", "", ncloudAuthKeyModel);

        String ncloudAccessKey = "";
        String ncloudSecretKey = "";
        ncloudAccessKey = res != null ? String.valueOf(res.get("accessKey")) : "";
        ncloudSecretKey = res != null ? String.valueOf(res.get("secretKey")) : "";
        ncloudAuthKeyModel.setAccess_key(ncloudAccessKey);
        ncloudAuthKeyModel.setSecret_key(ncloudSecretKey);
        ncloudInstanceKeyInfoModel.setSite(account.getSite());
        ncloudInstanceKeyInfoModel.setRegion(account.getRegion());

        List<NcloudInstanceKeyModel> resultList = new ArrayList<>();

        for (int i = 0; i < ncloudPrivateKeysModel.size(); i++) {
            String encodeParameter = "serverInstanceNo" + "=" + ncloudPrivateKeysModel.get(i).getInstanceNo() + "&" +
                    "privateKey" + "=" + URLEncoder.encode(ncloudPrivateKeysModel.get(i).getPrivateKey(), String.valueOf(StandardCharsets.UTF_8)) + "&" +
                    "responseFormatType" + "=" + "json";
            ncloudPrivateKeysModel.get(i).setEncodeParameter(encodeParameter);

            String signature = commonService.makeSignature(timestamp, ncloudAuthKeyModel.getAccess_key(), ncloudAuthKeyModel.getSecret_key(), requestURI, encodeParameter);
            ncloudPrivateKeysModel.get(i).setSignature(signature);

            String password = commonService.getData(timestamp, ncloudAuthKeyModel.getAccess_key(), reqUrl + "?" + encodeParameter, signature);
            ncloudPrivateKeysModel.get(i).setRootPassword(password);

            resultList.add(new NcloudInstanceKeyModel(ncloudPrivateKeysModel.get(i).getInstanceNo(), ncloudPrivateKeysModel.get(i).getRootPassword()));
        }

        return resultList;
    };

    /**
     * Create Ncloud Public Key File
     *
     * @param clusterId  the clusterId
     * @param provider   the provider
     * @param host       the host
     * @param idRsa      the idRsa
     * @param processGb  the processGb
     * @param seq        the seq
     * @param privateKey the privateKey
     * @param mpSeq      the mpSeq
     * @return the String
     */
    public String createNcloudPublicKey(String clusterId, String provider, String host, String idRsa, String processGb, int seq, String privateKey, int mpSeq) throws UnsupportedEncodingException {
        String masterHost = host;
        String cResult = "";
        String resultCode = Constants.RESULT_STATUS_SUCCESS;
        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();
        File file = new File(TerramanConstant.NCLOUD_PRIVATE_KEY_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId), clusterId));

        // Ncloud 루트 패스워드 조회
        List<NcloudInstanceKeyModel> ncloudInstanceKeyModel = getNcloudSSHKey(clusterId, provider, host, idRsa, processGb, seq);

        // Ncloud 개인키 조회
        NcloudPrivateKeyModel ncloudPrivateKeyModel = instanceService.getNcloudPrivateKey(clusterId, provider, host, idRsa, processGb, privateKey);

        // Ncloud 개인키 파일 생성
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsonString = gson.toJson(ncloudPrivateKeyModel.getPrivateKey());
            writer.write(jsonString);
            writer.flush();
            resultCode = Constants.RESULT_STATUS_SUCCESS;
        } catch (IOException e1) {
            resultCode = Constants.RESULT_STATUS_FAIL;
        }

        // 개인키 권한 변경 600
        terramanCommandModel.setCommand("20");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        // 개인키 따옴표 제거
        terramanCommandModel.setCommand("21");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        // 개인키 줄바꿈
        terramanCommandModel.setCommand("22");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        //개인키 Master로 업로드
        File uploadPriKeyFile = new File(TerramanConstant.NCLOUD_PRI_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId), clusterId));
        commandService.sshFileUpload(Constants.MASTER_SSH_DIR, host, idRsa, uploadPriKeyFile, TerramanConstant.DEFAULT_USER_NAME);

        // 공개키(authorized_keys) 생성
        terramanCommandModel.setCommand("23");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        commandService.execCommandOutput(terramanCommandModel);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        // 공개키 복사(authorized_keys -> clusterId + "-key.pub")
        terramanCommandModel.setCommand("24");
        terramanCommandModel.setDir(TerramanConstant.CLUSTER_STATE_DIR(clusterId));
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        commandService.execCommandOutput(terramanCommandModel);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }

        //공개키 Master로 업로드
        File uploadPubKeyFile = new File(TerramanConstant.NCLOUD_PUB_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId), clusterId));
        commandService.sshFileUpload(Constants.MASTER_SSH_DIR, host, idRsa, uploadPubKeyFile, TerramanConstant.DEFAULT_USER_NAME);

        // Ncloud 접속 및 .ssh 파일 생성
        List<NcloudPrivateKeyModel> ncloudPrivateKeysModel = instanceService.getNcloudPrivateKeys(clusterId, provider, host, idRsa, processGb);
        for (int i = 0; i < ncloudInstanceKeyModel.size(); i++) {
            terramanCommandModel.setCommand("19");
            terramanCommandModel.setDir(Constants.NCLOUD_HOST_DIR);
            if (ncloudPrivateKeysModel.get(i).getInstanceNo().equals(ncloudInstanceKeyModel.get(i).getServerInstanceNo())) {
                terramanCommandModel.setHost(ncloudPrivateKeysModel.get(i).getPublicIp());
            }
            terramanCommandModel.setUserName(TerramanConstant.NCLOUD_USER_NAME);
            terramanCommandModel.setInstanceKey(ncloudInstanceKeyModel.get(i).getRootPassword());
            terramanCommandModel.setIdRsa(ncloudInstanceKeyModel.get(i).getRootPassword());
            cResult = commandService.execPwdCommandOutput(terramanCommandModel);
            if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
                clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
                clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
            }
        }

        // Ncloud 접속 및 .ssh/authorized_keys 파일(공개키) 업로드
        File uploadFile = new File(TerramanConstant.NCLOUD_PUB_FILE_PATH(TerramanConstant.MOVE_DIR_CLUSTER(clusterId)));
        for (int i = 0; i < ncloudInstanceKeyModel.size(); i++) {

            if (ncloudPrivateKeysModel.get(i).getInstanceNo().equals(ncloudInstanceKeyModel.get(i).getServerInstanceNo())) {
                host = ncloudPrivateKeysModel.get(i).getPublicIp();
            }
            commandService.sshPwdFileUpload(Constants.NCLOUD_SSH_DIR, host, ncloudInstanceKeyModel.get(i).getRootPassword(), uploadFile, TerramanConstant.NCLOUD_USER_NAME);
        }

        // Master Ncloud 개인키 권한 변경 600
        terramanCommandModel.setCommand("20");
        terramanCommandModel.setDir(Constants.MASTER_SSH_DIR);
        terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
        terramanCommandModel.setClusterId(clusterId);
        terramanCommandModel.setHost(masterHost);
        terramanCommandModel.setIdRsa(idRsa);
        cResult = commandService.execCommandOutput(terramanCommandModel);
        if (StringUtils.equals(Constants.RESULT_STATUS_FAIL, cResult)) {
            clusterService.updateCluster(clusterId, TerramanConstant.CLUSTER_FAIL_STATUS);
            clusterLogService.saveClusterLog(clusterId, mpSeq, TerramanConstant.TERRAFORM_CREATE_CLUSTER_DIRECTORY_ERROR);
        }
        return resultCode;
    };
}

