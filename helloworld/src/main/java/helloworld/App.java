package helloworld;

import java.sql.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;

import static java.lang.System.out;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, String> {

    //private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
    private final AmazonSNS sns = buildClient();
    /**
     * Handles a Lambda Function request
     */
    public String handleRequest(Object input, Context context) {
        System.out.println("Inside hello world!!! " + input);
        System.out.println(String.format("Context [%s]", context));
        System.setProperty("aws.profile", "localstack");

        try {
            queryLifeQuotes();
            //ListTopicsResult topics = sns.listTopics();
            //topics.getTopics().forEach(topic -> {
            //    System.out.println("Topic : " + topic.getTopicArn());

            //});
            //sns.publish( "hello-world-sns-topic", "Records successfully read");
            PublishRequest publishRequest = new PublishRequest();
            publishRequest.setTopicArn("arn:aws:sns:us-east-1:000000000000:hello-world-sns-topic");
            publishRequest.setMessage("Records successfully added");
            sns.publish(publishRequest);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            context.getLogger().log(String.format("An error occurred while processing the request. Caused by: %s", e.getMessage()));
            e.printStackTrace();
        }

        return "Hello world: " + input;
    }

    private void queryLifeQuotes() throws SQLException {

        try (Connection connection = getConnection();
             Statement stmnt = connection.createStatement()) {

            String sqlQuery = getQuery();
            ResultSet response = stmnt.executeQuery(sqlQuery);
            while (response.next()) {
                long quoteNo = response.getLong("quote_no");
                String quoteFileId = response.getString("quote_file_id");

                out.println(String.format("Record details: quote-no=%s quote-file-id=%s", quoteNo, quoteFileId));
            }
        } finally {
            out.println(String.format("Finished executing quotes DB Query ..."));
        }

    }

    private AmazonSNS buildClient() {
        String key = "EXAMPLE"; //"123";
        String secret = "EXAMPLE"; //"123";
        AWSCredentials credentials = new BasicAWSCredentials(key, secret);

        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration("http://localhost:4566"
                        , null);

        //AmazonSNSClientBuilder.standard()
                //.withCredentials(new ProfileCredentialsProvider())
        return AmazonSNSClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfig)
                //.withCredentials(new ProfileCredentialsProvider("localstack"))
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                //.withCredentials(ProfileCredentialsProvider)


                //.enablePathStyleAccess()
                //.withRegion(Regions.fromName("us-east-1"))
                .build();

    }

    private String getQuery() {
        return "SELECT * FROM lv_quote_xml l WHERE l.quote_no > 1300 ";
    }

    private Connection getConnection() throws SQLException {
        String dbUrl = "jdbc:postgresql://postgres-local:5432/msa";
        String dbUsername = "msa";
        String dbPassword = "msa";
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
}
