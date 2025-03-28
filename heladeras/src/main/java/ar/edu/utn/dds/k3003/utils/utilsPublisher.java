package ar.edu.utn.dds.k3003.utils;

import ar.edu.utn.dds.k3003.app.WebApp;
import com.rabbitmq.client.Channel;
import java.io.IOException;

public class utilsPublisher{
  private static String QUEUE_NAME;

  static {
    QUEUE_NAME = System.getenv("QUEUE_NAME");
  }
  public static void pushMessageQueue(String message) throws IOException {
    Channel channel = WebApp.channel;
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
  }

}
