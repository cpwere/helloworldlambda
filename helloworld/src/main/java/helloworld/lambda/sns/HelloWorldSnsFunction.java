package helloworld.lambda.sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class HelloWorldSnsFunction {

    public void handler(SNSEvent event, Context ctx) {
        event.getRecords().forEach(snsRecord -> {
            System.out.println(String.format("Received this event: [%s]", snsRecord));
        });
    }
}
