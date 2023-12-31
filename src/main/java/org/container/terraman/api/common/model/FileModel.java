package org.container.terraman.api.common.model;

import lombok.Data;

@Data
public class FileModel {

    /*
     * OPENSTACK FOR TERRAFORM
     * */
    private String openstackTenantName;
    private String openstackPassword;
    private String openstackAuthUrl;
    private String openstackUserName;
    private String openstackRegion;

    /*
     * NHN FOR TERRAFORM
     * */
    private String nhnTenantName;
    private String nhnPassword;
    private String nhnAuthUrl;
    private String nhnUserName;
    private String nhnRegion;

    /*
     * AWS FOR TERRAFORM
     * */
    private String awsAccessKey;
    private String awsSecretKey;
    private String awsRegion;

    /*
     * VSPHERE FOR TERRAFORM
     * */
    private String vSphereUser;
    private String vSpherePassword;
    private String vSphereServer;

    /*
     * NCP FOR TERRAFORM
     * */
    private String ncloudAccessKey;
    private String ncloudSecretKey;
    private String ncloudRegion;
    private String ncloudSite;
    private Object ncloudSupportVpc;
}
