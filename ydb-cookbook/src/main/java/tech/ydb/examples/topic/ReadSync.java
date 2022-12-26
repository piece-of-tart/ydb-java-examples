package tech.ydb.examples.topic;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.examples.SimpleExample;
import tech.ydb.topic.TopicClient;
import tech.ydb.topic.read.Message;
import tech.ydb.topic.read.Reader;
import tech.ydb.topic.settings.ReaderSettings;
import tech.ydb.topic.settings.TopicReadSettings;

/**
 * @author Nikolay Perfilov
 */
public class ReadSync extends SimpleExample {
    private static final Logger logger = LoggerFactory.getLogger(ReadSync.class);

    @Override
    protected void run(GrpcTransport transport, String pathPrefix) {
        String topicPath = pathPrefix + "test_topic";
        String consumerName = "consumer1";

        TopicClient topicClient = TopicClient.newClient(transport).build();

        ReaderSettings settings = ReaderSettings.newBuilder()
                .setConsumerName(consumerName)
                .addTopic(TopicReadSettings.newBuilder()
                        .setPath(topicPath)
                        .setReadFrom(Instant.now().minus(Duration.ofHours(24)))
                        .setMaxLag(Duration.ofMinutes(30))
                        .build())
                .build();

        Reader reader = topicClient.createReader(settings);

        // Init in background ?
        reader.start();

        Message message = reader.receive();

        logger.info("Message received: " + message.getData());

        message.commit();

        reader.close();

    }

    public static void main(String[] args) {
        new ReadSync().doMain(args);
    }
}
