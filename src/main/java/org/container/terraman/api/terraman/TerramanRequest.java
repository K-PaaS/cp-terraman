package org.container.terraman.api.terraman;

import lombok.Data;

@Data
public class TerramanRequest {
    private String clusterId;
    private String seq;
    private String provider;
    private String sshKey;
}
