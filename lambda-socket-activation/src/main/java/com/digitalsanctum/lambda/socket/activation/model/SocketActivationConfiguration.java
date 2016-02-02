package com.digitalsanctum.lambda.socket.activation.model;

import java.io.File;

/**
 * @author shane
 */
public class SocketActivationConfiguration {

    //step1
    private String name;
    private String dockerImage;

    //step2
    private int socketListenStreamPort;

    //step4
    private String proxyServiceHost;
    private int proxyServicePort;

    //step5
    private String dockerEnginePath;
    private String waitPortPath;

    //step6
    private int waitPortRetries;
    private String netcatPath;
    private float waitPortSleepPeriod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public String getSocketDescriptorFilePath() {
        return this.name + "-proxy.socket";
    }

    public int getSocketListenStreamPort() {
        return socketListenStreamPort;
    }

    public void setSocketListenStreamPort(int socketListenStreamPort) {
        this.socketListenStreamPort = socketListenStreamPort;
    }

    public String getProxyServiceFilePath() {
        return this.name + "-proxy.service";
    }

    public String getProxyServiceHost() {
        return proxyServiceHost;
    }

    public void setProxyServiceHost(String proxyServiceHost) {
        this.proxyServiceHost = proxyServiceHost;
    }

    public int getProxyServicePort() {
        return proxyServicePort;
    }

    public void setProxyServicePort(int proxyServicePort) {
        this.proxyServicePort = proxyServicePort;
    }

    public String getDockerServiceFilePath() {
        return this.name + "-docker.service";
    }

    public String getDockerEnginePath() {
        return dockerEnginePath;
    }

    public void setDockerEnginePath(String dockerEnginePath) {
        this.dockerEnginePath = dockerEnginePath;
    }

    public String getWaitPortPath() {
        return waitPortPath;
    }

    public void setWaitPortPath(String waitPortPath) {
        this.waitPortPath = waitPortPath;
    }

    public int getWaitPortRetries() {
        return waitPortRetries;
    }

    public void setWaitPortRetries(int waitPortRetries) {
        this.waitPortRetries = waitPortRetries;
    }

    public String getNetcatPath() {
        return netcatPath;
    }

    public void setNetcatPath(String netcatPath) {
        this.netcatPath = netcatPath;
    }

    public float getWaitPortSleepPeriod() {
        return waitPortSleepPeriod;
    }

    public void setWaitPortSleepPeriod(float waitPortSleepPeriod) {
        this.waitPortSleepPeriod = waitPortSleepPeriod;
    }

    public String getSocketDescriptorFileContent() {
        return "[Socket]\n" +
                "ListenStream=" + this.socketListenStreamPort + "\n" +
                "\n" +
                "[Install]\n" +
                "WantedBy=sockets.target\n";
    }

    public String getProxyServiceFileContent() {
        return "[Unit]\n" +
                "Requires=" + getDockerServiceFilePath() + "\n" +
                "After=" + getDockerServiceFilePath() + "\n" +
                "\n" +
                "[Service]\n" +
                "ExecStart=/lib/systemd/systemd-socket-proxyd " + this.proxyServiceHost + ":" + this.proxyServicePort + "\n";
    }

    public String getDockerServiceFileContent(String waitPortDir) {
        return "[Unit]\n" +
                "Description=" + this.name + " container\n" +
                "\n" +
                "[Service]\n" +
                "ExecStart=" + this.dockerEnginePath + " start -a " + this.name + "\n" +
                "ExecStartPost=" + waitPortDir + File.separator + this.waitPortPath + " " + this.proxyServiceHost + " " + this.proxyServicePort + "\n" +
                "\n" +
                "ExecStop=" + this.dockerEnginePath + " stop " + this.name + "\n";
    }

    public String getWaitportFileContent() {
        return "#!/bin/bash\n" +
                "\n" +
                "host=$1\n" +
                "port=$2\n" +
                "tries=" + this.waitPortRetries + "\n" +
                "\n" +
                "for i in `seq $tries`; do\n" +
                "    if " + this.netcatPath + " -z $host $port > /dev/null ; then\n" +
                "      # Ready\n" +
                "      exit 0\n" +
                "    fi\n" +
                "\n" +
                "    /bin/sleep " + this.waitPortSleepPeriod + "\n" +
                "done\n" +
                "\n" +
                "# FAIL\n" +
                "exit -1\n";
    }
}
