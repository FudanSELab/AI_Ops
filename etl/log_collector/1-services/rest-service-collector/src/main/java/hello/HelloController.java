package hello;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Gson;
import hello.domain.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AsyncRestTemplate asyncRestTemplate;


	@Autowired
	private KafkaTemplate kafkaTemplate;

	@RequestMapping(value = "api/v1/spans", method ={RequestMethod.POST,RequestMethod.GET})
	public String handle_collect(@RequestBody String info) {
		System.out.println("[===] HelloController - handle_collect");
		System.out.println("==========HelloController - handle_collect============");
		System.out.println(info);

		System.out.println("==========================");


		ListenableFuture future = kafkaTemplate.send("app_log", info);
		future.addCallback(o -> System.out.println("send-success: " + "--"),
				throwable -> System.out.println("failed: " + "--"));

		System.out.println("============app_log  ==============");
		return "---------post------------";
		
	}


	@RequestMapping(value = {"api/**", "**/spans"}, method ={RequestMethod.POST,RequestMethod.GET})
	public String handle_collect_2(@RequestBody String info) {
		System.out.println("[===] HelloController - handle_collect_2");
		System.out.println("==========HelloController - handle_collect_2============");
		System.out.println(info);
		System.out.println("==========================");
		return "---------post span any------------";
		
	}



	@RequestMapping(value = "**", method ={RequestMethod.POST,RequestMethod.GET})
	public String handle_collect_any(@RequestBody String info) {
		System.out.println("[===] HelloController - handle_collect_any");
		System.out.println("==========HelloController - handle_collect_any============");
		System.out.println(info);
		System.out.println("==========================");
		return "---------post any------------";
		
	}
}
