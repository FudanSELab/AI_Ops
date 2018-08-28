package hello.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

	private static final Logger log = LoggerFactory.getLogger(RestEndApplication.class);

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="cal", defaultValue="50") String cal) throws Exception {

    	Greeting value = new Greeting();

        return value;
    }
    
    @RequestMapping("/test")
    public String test(@RequestParam(value="cal", defaultValue="50") String cal) throws Exception {
    	// log.info(cal);

    	double cal2 = Math.abs(Double.valueOf(cal)-50);
        System.out.println(cal2);
        return String.valueOf(cal2);
    }
}
