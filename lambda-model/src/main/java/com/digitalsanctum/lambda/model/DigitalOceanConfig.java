package com.digitalsanctum.lambda.model;

public class DigitalOceanConfig {

    private final String apiVersion;
    private final String token;
    private final String hostname;
    private final String imageSlug;
    private final String size;
    private final String region;
    private final String userData;

    private DigitalOceanConfig(Builder builder) {
        this.apiVersion = builder.apiVersion;
        this.region = builder.region;
        this.size = builder.size;
        this.token = builder.token;
        this.hostname = builder.hostname;
        this.userData = builder.userData;
        this.imageSlug = builder.imageSlug;
    }

    public static class Builder {
        private String apiVersion = "v2";
        private String token;
        private String hostname;
        private String size = "512mb";
        private String region;
        private UserDataProvider userDataProvider;
        private String userData;
        private String imageSlug = "docker";

        public Builder fromEnv() {
            this.token = System.getenv("DO_TOKEN");
            return this;
        }

        public Builder imageSlug(String imageSlug) {
            this.imageSlug = imageSlug;
            return this;
        }

        public Builder apiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder userDataProvider(UserDataProvider userDataProvider) {
            this.userDataProvider = userDataProvider;
            return this;
        }

        public Builder userData(String userData) {
            this.userData = userData;
            return this;
        }

        public DigitalOceanConfig build() {
            if (this.userDataProvider != null) {
                this.userData = this.userDataProvider.getUserData();
            }
            return new DigitalOceanConfig(this);
        }
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getRegion() {
        return region;
    }

    public String getSize() {
        return size;
    }

    public String getToken() {
        return token;
    }

    public String getHostname() {
        return hostname;
    }

    public String getUserData() {
        return userData;
    }

    public String getImageSlug() {
        return imageSlug;
    }
}
