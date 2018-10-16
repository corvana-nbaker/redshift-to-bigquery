import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class Utils {

    static AmazonS3 getS3 (final S3Config config) {
        AWSCredentialsProvider creds = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return config.getAccessKeyId();
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return config.getSecretAccessKey();
                    }
                };
            }

            @Override
            public void refresh() {

            }
        };

        return AmazonS3ClientBuilder.standard().withRegion(config.getRegion()).withCredentials(creds).build();
    }
}
