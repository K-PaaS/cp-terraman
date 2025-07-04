package org.container.terraman.api.common.model;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
public class ClusterLogEmbededModel implements Serializable {

    @Column(name = "cluster_id")
    private String clusterId;
    //@Id
    @Column(name = "process_no")
    private int processNo;

}
