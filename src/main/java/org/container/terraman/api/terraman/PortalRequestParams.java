package org.container.terraman.api.terraman;

import lombok.Data;

@Data
public class PortalRequestParams {
    String tag ;
    String cluster;
    String namespace;
    String resourceName;
}