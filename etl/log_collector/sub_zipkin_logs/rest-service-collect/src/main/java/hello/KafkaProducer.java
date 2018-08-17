package hello;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * timer
 */
@Component
@EnableScheduling
public class KafkaProducer {

//	@Autowired
//	private KafkaTemplate kafkaTemplate;
//
//	/**
//	 * timer
//	 */
//	@Scheduled(cron = "00/1 * * * * ?")
//	public void send() {
//
//		System.out.println("[===] KafkaProducer - send");
//
//		String message = UUID.randomUUID().toString();
//		ListenableFuture future = kafkaTemplate.send("app_log", message);
//		future.addCallback(o -> System.out.println("send-success: " + message),
//				throwable -> System.out.println("failed: " + message));
//	}

//	@Scheduled(fixedRate = 1000 * 60)
//	public void testKafka() throws Exception {
//		kafkaTemplate.send("app_log", "TEST kafka");
//	}

}