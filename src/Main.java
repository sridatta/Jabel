import java.io.IOException;

import security.ScriptSecurity;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import java.util.concurrent.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Main {

	private final static String QUEUE_NAME = "exec";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Sets Rhino's context handler to deny any attempts into Java code
		ScriptSecurity.initSecurity();

		// Establish RabbitMQ networking
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, consumer);

			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println(" [x] Received '" + message + "'");

				//EL DANGEROUSO. Don't just split naively. Don't just trust any filename or classname.
				JSONObject obj = parse(message);
				String submissionId = (String) obj.get("id");
				String fileName = (String) obj.get("file");
				String solverClass = (String) obj.get("puzzle");

				// Use fixed thread pool so we don't get overloaded at high request rates
				ExecutorService threadService = Executors.newFixedThreadPool(40);
				threadService.execute(new ScriptRunner(fileName, solverClass, submissionId));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			System.err.println("RabbitMQ error");
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static JSONObject parse(String jsonString){
		Object obj=JSONValue.parse(jsonString);
		JSONObject rtn=(JSONObject) obj;
		return rtn;
	}
}