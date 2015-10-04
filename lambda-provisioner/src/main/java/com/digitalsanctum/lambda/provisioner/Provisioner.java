package com.digitalsanctum.lambda.provisioner;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.common.DropletStatus;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Image;
import com.myjeeva.digitalocean.pojo.Images;
import com.myjeeva.digitalocean.pojo.Network;
import com.myjeeva.digitalocean.pojo.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Requires the following system variables to be set:
 * - DO_TOKEN
 * - DOCKER_EMAIL
 * - DOCKER_USERNAME
 * - DOCKER_PASSWORD
 */
public class Provisioner {

    private final DigitalOcean apiClient;

    public Provisioner(DigitalOcean apiClient) {
        this.apiClient = apiClient;
    }

    public static void main(String[] args) throws Exception {

        String authToken = System.getenv("DO_TOKEN");

        DigitalOcean apiClient = new DigitalOceanClient("v2", authToken);

        Provisioner provisioner = new Provisioner(apiClient);

        Integer dropletId = provisioner.createDroplet("lambda-api", "docker", "tor1");
        provisioner.waitForDropletCreation(dropletId);

//        Delete delete = apiClient.deleteDroplet(8008758);
//        System.out.println(delete);
    }

    private String getUserData(String email,
                               String user,
                               String pass,
                               String lambdaTimeout,
                               String lambdaHandler,
                               String lambdaPort,
                               String lambdaDockerImage) {

        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash").append("\n")
                .append(String.format("docker login --email=%s --username=%s --password=%s", email, user, pass)).append("\n")
                .append(String.format("docker pull %s", lambdaDockerImage)).append("\n")
                .append(String.format("docker run -d -e \"LAMBDA_TIMEOUT=%s\" -e \"LAMBDA_HANDLER=%s\" -p %s:8080 %s",
                        lambdaTimeout, lambdaHandler, lambdaPort, lambdaDockerImage));
        return sb.toString();
    }

    public Integer createDroplet(String name, String imageSlug, String regionSlug) throws RequestUnsuccessfulException, DigitalOceanException {
        Droplet newDroplet = new Droplet();
        newDroplet.setName(name);
        newDroplet.setSize("512mb"); // setting size by slug value
        newDroplet.setRegion(new Region(regionSlug)); // setting region by slug value; tor1 => Toronto
        newDroplet.setImage(new Image(imageSlug));
        newDroplet.setEnableBackup(TRUE);
        newDroplet.setEnableIpv6(TRUE);
        newDroplet.setEnablePrivateNetworking(FALSE);

        newDroplet.setKeys(apiClient.getAvailableKeys(1).getKeys());

        String dockerEmail = System.getenv("DOCKER_EMAIL");
        String dockerUser = System.getenv("DOCKER_USERNAME");
        String dockerPass = System.getenv("DOCKER_PASSWORD");
        newDroplet.setUserData(getUserData(dockerEmail, dockerUser, dockerPass, "3", "com.digitalsanctum.lambda.samples.HelloPojo", "80", "digitalsanctum/lambda-api"));

        Droplet droplet = apiClient.createDroplet(newDroplet);
        return droplet.getId();
    }

    // Adding Metadata API - User Data
    /*
     * droplet .setUserData("#!/bin/bash" + "apt-get -y update" + "apt-get -y install nginx" +
     * "export HOSTNAME=$(curl -s http://169.254.169.254/metadata/v1/hostname)" +
     * "export PUBLIC_IPV4=$(curl -s http://169.254.169.254/metadata/v1/interfaces/public/0/ipv4/address)"
     * + "echo Droplet: $HOSTNAME, IP Address: $PUBLIC_IPV4 > /usr/share/nginx/html/index.html");
     */

    public void waitForDropletCreation(int id)
            throws RequestUnsuccessfulException, DigitalOceanException, InterruptedException, IOException {
        while(true) {
            Droplet dropletInfo = apiClient.getDropletInfo(id);
            DropletStatus status = dropletInfo.getStatus();
            if ("active".equals(status.toString())) {
                for (Network nw : dropletInfo.getNetworks().getVersion4Networks()) {
                    System.out.print(nw.getIpAddress());
                }
                break;
            }
            Thread.sleep(5000);
        }
    }

    public List<Image> getAllImages() throws RequestUnsuccessfulException, DigitalOceanException {
        List<Image> allImages = new ArrayList<>();

        // gets the first 20 images (page 1)
        Images images = apiClient.getAvailableImages(1);
        allImages.addAll(images.getImages());

        // determine the max number of pages to fetch
        int imagesPerPage = 20;
        double totalImages = (double)images.getMeta().getTotal();
        Double numPages = Math.ceil(totalImages/imagesPerPage);

        // iterate through pages 2 thru numPages and fetch the rest of the images
        for (int i = 2; i <= numPages.intValue(); i++) {
            images = apiClient.getAvailableImages(i);
            allImages.addAll(images.getImages());
        }

        return allImages;
    }
}
