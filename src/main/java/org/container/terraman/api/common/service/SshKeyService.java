package org.container.terraman.api.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.container.terraman.api.common.PropertyService;
import org.container.terraman.api.common.VaultService;
import org.container.terraman.api.common.constants.Constants;
import org.container.terraman.api.common.constants.TerramanConstant;
import org.container.terraman.api.common.model.SshKeyModel;
import org.container.terraman.api.common.model.TerramanCommandModel;
import org.container.terraman.api.common.repository.SshKeyRepository;
import org.container.terraman.api.common.util.CommonUtils;
import org.container.terraman.api.exception.ContainerTerramanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

@Service
public class SshKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshKeyService.class);

    private final PropertyService propertyService;
    private final CommandService commandService;
    private final VaultService vaultService;
    private final SshKeyRepository sshKeyRepository;


    @Autowired
    public SshKeyService(
            PropertyService propertyService
            , CommandService commandService
            , VaultService vaultService
            , SshKeyRepository sshKeyRepository
    ) {
        this.propertyService = propertyService;
        this.commandService = commandService;
        this.vaultService = vaultService;
        this.sshKeyRepository = sshKeyRepository;
    }

    /**
     * Create SSH Key File
     *
     * @param sshKeyName the ssh key name
     * @param clusterId the cluster id
     * @return the String
     */
    public String createSshKeyFile(String sshKeyName, String clusterId, String host, String idRsa) {
        String resultCode = Constants.RESULT_STATUS_FAIL;

        SshKeyModel sshKeyModel = getSshKeyInfo(sshKeyName);

        String path = propertyService.getVaultBase();
        path =  path + Constants.DIR_SSH_KEY + sshKeyModel.getId();
        HashMap<String, Object> res = vaultService.read(path, HashMap.class);

        String privateKey = "";
        privateKey = res != null ? String.valueOf(res.get("privateKey")) : "";

        TerramanCommandModel terramanCommandModel = new TerramanCommandModel();

        boolean fileFlag = true;
        try {
            File file = new File(TerramanConstant.SUBCLUSTER_PRIVATE_KEY_FILE_PATH(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR, sshKeyName));
            if (!file.exists()) {
                fileFlag = file.createNewFile();
            }

            if (fileFlag) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));) {
                    // 파일 쓰기
                    Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
                    String jsonString = "";
                    if (privateKey.contains("\n")){
                        jsonString = gson.toJson(privateKey.replaceAll("\n", ","));
                    }

                    if (privateKey.contains("\r\n")){
                        jsonString = gson.toJson(privateKey.replaceAll("\r\n", ","));
                    }

                    if (privateKey.contains("\n\r")){
                        jsonString = gson.toJson(privateKey.replaceAll("\n\r", ","));
                    }

                    writer.write(jsonString);
                    writer.flush();

                    //ssh private key 생성
                    terramanCommandModel.setCommand("30");
                    terramanCommandModel.setDir(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR);
                    terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
                    terramanCommandModel.setClusterId(clusterId);
                    terramanCommandModel.setSshKeyName(sshKeyName);
                    commandService.execCommandOutput(terramanCommandModel);

                    terramanCommandModel.setCommand("27");
                    terramanCommandModel.setDir(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR);
                    terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
                    terramanCommandModel.setClusterId(clusterId);
                    terramanCommandModel.setSshKeyName(sshKeyName);
                    commandService.execCommandOutput(terramanCommandModel);

                    terramanCommandModel.setCommand("28");
                    terramanCommandModel.setDir(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR);
                    terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
                    terramanCommandModel.setClusterId(clusterId);
                    terramanCommandModel.setSshKeyName(sshKeyName);
                    commandService.execCommandOutput(terramanCommandModel);

                    File uploadPriKeyFile = new File(TerramanConstant.SSH_KEY_PRI_FILE_PATH(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR, sshKeyName));
                    commandService.sshFileUpload(Constants.MASTER_SSH_DIR, host, idRsa, uploadPriKeyFile, TerramanConstant.DEFAULT_USER_NAME);

                    //ssh public key 생성
                    terramanCommandModel.setCommand("29");
                    terramanCommandModel.setDir(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR);
                    terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
                    terramanCommandModel.setClusterId(clusterId);
                    terramanCommandModel.setSshKeyName(sshKeyName);
                    commandService.execCommandOutput(terramanCommandModel);

                    terramanCommandModel.setCommand("31");
                    terramanCommandModel.setDir(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR);
                    terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
                    terramanCommandModel.setClusterId(clusterId);
                    terramanCommandModel.setSshKeyName(sshKeyName);
                    commandService.execCommandOutput(terramanCommandModel);

                    File uploadPubKeyFile = new File(TerramanConstant.SSH_KEY_PUB_FILE_PATH(TerramanConstant.TERRAMAN_IP_SSH_KEY_DIR, sshKeyName));
                    commandService.sshFileUpload(Constants.MASTER_SSH_DIR, host, idRsa, uploadPubKeyFile, TerramanConstant.DEFAULT_USER_NAME);

                    //master key pair 권한 변경
                    terramanCommandModel.setCommand("30");
                    terramanCommandModel.setHost(host);
                    terramanCommandModel.setIdRsa(idRsa);
                    terramanCommandModel.setDir(Constants.MASTER_SSH_DIR);
                    terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
                    terramanCommandModel.setClusterId(clusterId);
                    terramanCommandModel.setSshKeyName(sshKeyName);
                    commandService.execCommandOutput(terramanCommandModel);

                    terramanCommandModel.setCommand("31");
                    terramanCommandModel.setHost(host);
                    terramanCommandModel.setIdRsa(idRsa);
                    terramanCommandModel.setDir(Constants.MASTER_SSH_DIR);
                    terramanCommandModel.setUserName(TerramanConstant.DEFAULT_USER_NAME);
                    terramanCommandModel.setClusterId(clusterId);
                    terramanCommandModel.setSshKeyName(sshKeyName);
                    commandService.execCommandOutput(terramanCommandModel);

                    LOGGER.info("SSH Key Pair 생성이 완료되었습니다.");

                    resultCode = Constants.RESULT_STATUS_SUCCESS;
                } catch (IOException e1) {
                    resultCode = Constants.RESULT_STATUS_FAIL;
                }


                return resultCode;
            }
        } catch (IOException e) {
            resultCode = Constants.RESULT_STATUS_FAIL;
            LOGGER.error(CommonUtils.loggerReplace(e.getMessage()));
        }

        return resultCode;
    }

    /**
     * Select SSH Key Info
     *
     * @param sshKeyName the ssh key name
     * @return the SshKeyModel
     */
    public SshKeyModel getSshKeyInfo(String sshKeyName) {
        SshKeyModel sshKeyModel = null;
        try {
            sshKeyModel = sshKeyRepository.findByName(sshKeyName);
        } catch (Exception e) {
            throw new ContainerTerramanException(e.getMessage());
        }

        return sshKeyModel;
    }
}