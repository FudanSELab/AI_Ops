package hello.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/rest1")
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(Rest1Application.class);
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @RequestMapping("/test1")
    public String getTest1(@RequestHeader HttpHeaders headers){
        log.info(headers.toString());
        return "===" + headers.toString();
    }
    @RequestMapping(value = "/hello1" )
    public Value hello1(@RequestParam(value = "cal", defaultValue = "50") String cal,@RequestHeader HttpHeaders headers ) {
        System.out.println("================ print header  ==============");
        log.info(headers.toString());
        System.out.println("================ print header end ==============");
        double cal2 = Math.log10(Double.valueOf(cal)) * 50;
        log.info(String.valueOf(cal2));

        double rand = Math.random();
        Value value = new Value();


        String str = restTemplate.getForObject("http://rest-service-end:16006/rest2/test?cal=" + cal2, String.class);
        System.out.println(str);

//		ListenableFuture<ResponseEntity<String>> future = asyncRestTemplate
//				.getForEntity("http://rest-service-end:16000/test?cal=" + cal2, String.class);
//		try {
//			// waits for the result
//			ResponseEntity<String> entity = future.get();
//			// prints body source code for the given URL
//			System.out.println(entity.getBody());
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		}

//		if (rand < 0.3) {
//			value = restTemplate.getForObject("http://rest-service-nodejs:16100/greeting?cal=" + cal2, Value.class);
//		} else if (rand >= 0.3 && rand < 0.6) {
//			String result = restTemplate.getForObject("http://rest-service-python:16101/test?cal=" + cal2,
//					String.class);
//			log.info("---------: " + result);
//			value.setContent(result.length() > 0);
//		} else {
//			String result = restTemplate.getForObject("http://rest-service-go:16102/test?cal=" + cal2, String.class);
//			log.info("---------: " + result);
//			value.setContent(result.length() > 0);
//		}

//		log.info(value.toString());
        return value;
    }
}
