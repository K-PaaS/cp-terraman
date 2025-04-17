package org.container.terraman.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cp_cluster_ssh_key")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SshKeyModel {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "created", nullable = false)
    private String created;

    @Column(name = "last_modified", nullable = false)
    private String lastModified;

    public SshKeyModel(int id, String name, String provider, String created, String lastModified) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.created = created;
        this.lastModified = lastModified;
    }

    public SshKeyModel() {

    }
}
