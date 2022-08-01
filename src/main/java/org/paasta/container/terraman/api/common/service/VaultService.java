package org.paasta.container.terraman.api.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.HashMap;
import java.util.Map;

@Service
public class VaultService {
    Logger logger = LoggerFactory.getLogger(VaultService.class);

    @Autowired
    VaultTemplate vaultTemplate;

    @Autowired
    PropertyService propertyService;

    @Autowired
    CommonService commonService;

    /**
     * Vault read를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public <T> T read(String path,  Class<T> requestClass) {
        VaultResponse vaultResponse;

        path = setPath(path);

        try {
            vaultResponse = vaultTemplate.read(path);
        }
        catch (Exception e){
            logger.info("Invalid path");
            return null;
        }
        HashMap responseMap = (HashMap) vaultResponse.getData().get("data");
        return  commonService.setResultObject(responseMap, requestClass);
    }

    /**
     * Vault write를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public Object write(String path, Object body){
        path = setPath(path);

        Map<String, Object> data = new HashMap<>();
        data.put("data", body);

        return vaultTemplate.write(path, data);
    }

    /**
     * Vault delete를 위한 method
     *
     * @param path the path
     * @return the object
     */
    public void delete(String path){
        path = setPath(path);
        vaultTemplate.delete(path);
    }

    /**
     * Vault path 처리 를 위한 method
     *
     * @param path the path
     * @return the String
     */
    String setPath(String path){
        return new StringBuilder(path).insert(path.indexOf("/") + 1, "data/").toString();
    }


    /**
     * Vault를 통한 Cluster 정보 조회
     *
     * @param clusterId the clusterId
     * @return the String
     */
//    public Clusters getClusterDetails(String clusterId){
//        return read(propertyService.getVaultClusterTokenPath().replace("{id}", clusterId), Clusters.class);
//    }

}
