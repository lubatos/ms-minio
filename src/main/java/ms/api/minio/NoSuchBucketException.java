package ms.api.minio;


public class NoSuchBucketException extends RuntimeException {
    private final String bucket;
    private final String objectName;

    public NoSuchBucketException(String bucket, String objectName) {
        super("The specified bucket does not exist");
        this.bucket = bucket;
        this.objectName = objectName;
    }

    public String getBucket() {
        return bucket;
    }

    public String getObjectName() {
        return objectName;
    }
}