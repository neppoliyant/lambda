package com.digitalsanctum.lambda.provisioner;

import com.digitalsanctum.lambda.model.DigitalOceanConfig;
import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.common.DropletStatus;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Image;
import com.myjeeva.digitalocean.pojo.Images;
import com.myjeeva.digitalocean.pojo.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Provisioner {

    private final DigitalOceanConfig config;
    private final DigitalOcean apiClient;

    public Provisioner(DigitalOceanConfig config) {
        this.config = config;
        this.apiClient = new DigitalOceanClient(config.getApiVersion(), config.getToken());
    }

    public static void main(String[] args) throws Exception {

        Provisioner provisioner = new Provisioner(new DigitalOceanConfig.Builder().fromEnv()
                .hostname("test")
                .imageSlug("docker")
                .region("tor1")
                .build());
        Integer dropletId = provisioner.createDroplet();
        String ipAddress = provisioner.waitForDropletCreation(dropletId);
        System.out.println(ipAddress);

        /*List<Image> images = new Provisioner(new DigitalOceanConfig.Builder().fromEnv()
                .build()).getAllImages();
        for (Image image : images) {
            System.out.println(image.getSlug());
        }*/
    }

    public Integer createDroplet() throws RequestUnsuccessfulException, DigitalOceanException {
        Droplet newDroplet = new Droplet();
        newDroplet.setName(config.getHostname());
        newDroplet.setSize(config.getSize());
        newDroplet.setRegion(new Region(config.getRegion()));
        newDroplet.setImage(new Image(config.getImageSlug()));

        // todo make these part of config
        newDroplet.setEnableBackup(TRUE);
        newDroplet.setEnableIpv6(TRUE);
        newDroplet.setEnablePrivateNetworking(FALSE);

        // todo don't set all keys by default
        newDroplet.setKeys(apiClient.getAvailableKeys(1).getKeys());

        if (config.getUserData() != null) {
            newDroplet.setUserData(config.getUserData());
        }

        Droplet droplet = apiClient.createDroplet(newDroplet);
        return droplet.getId();
    }

    public String waitForDropletCreation(int id)
            throws RequestUnsuccessfulException, DigitalOceanException, InterruptedException, IOException {
        while (true) {
            Droplet dropletInfo = apiClient.getDropletInfo(id);
            DropletStatus status = dropletInfo.getStatus();
            if ("active".equals(status.toString())) {
                return dropletInfo.getNetworks().getVersion4Networks().get(0).getIpAddress();
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
        double totalImages = (double) images.getMeta().getTotal();
        Double numPages = Math.ceil(totalImages / imagesPerPage);

        // iterate through pages 2 thru numPages and fetch the rest of the images
        for (int i = 2; i <= numPages.intValue(); i++) {
            images = apiClient.getAvailableImages(i);
            allImages.addAll(images.getImages());
        }

        return allImages;
    }
}

