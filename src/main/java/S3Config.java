public class S3Config {
    private String region = "us-west-2";
    private String bucket = "corvana-worker-data-load-temp";
    private String prefix = "rob";
    private String accessKeyId;
    private String secretAccessKey;

    public S3Config() {
        this.accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        this.secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
    }

    public S3Config(String region, String bucket, String prefix) {
        this.region = region;
        this.bucket = bucket;
        this.prefix = prefix;
        this.accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        this.secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
    }

    public S3Config(String region, String bucket, String prefix, String accessKeyId, String secretAccessKey) {
        this.region = region;
        this.bucket = bucket;
        this.prefix = prefix;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }
}
