package org.paasta.container.terraman.api.common.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Property Service 클래스
 *
 * @author yjh
 * @version 1.0
 * @since 2022.07.11
 */

@Service
@Data
public class PropertyService {
    @Value("${vault.path.base}")
    private String vaultBase;

//    @Value("${vault.path.cluster-token}")
//    private String vaultClusterTokenPath;

}
