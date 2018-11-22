package hello.controller;

import hello.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@RestController
public class ZipkinKafkaReceiverController {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;


    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping(value = "api/v1/spans", method = {RequestMethod.POST, RequestMethod.GET})
    public String handle_collect(@RequestBody String info) {
        System.out.println("[===] HelloController - handle_collect");
        System.out.println("==========HelloController - handle_collect============");
        System.out.println(info);

        System.out.println("==========================");

        ListenableFuture future = kafkaTemplate.send("app_log", info);
        future.addCallback(o -> System.out.println("send-success: " + "--"),
                throwable -> System.out.println("failed: " + "--"));

        return "---------post------------";
    }


    @RequestMapping(value = {"api/**", "**/spans"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String handle_collect_2(@RequestBody String info) {
        System.out.println("[===] HelloController - handle_collect_2");
        System.out.println("==========HelloController - handle_collect_2============");
        System.out.println(info);
        System.out.println("==========================");
        return "---------post span any------------";

    }


    @RequestMapping(value = "**", method = {RequestMethod.POST, RequestMethod.GET})
    public String handle_collect_any(@RequestBody String info) {
        System.out.println("[===] HelloController - handle_collect_any");
        System.out.println("==========HelloController - handle_collect_any============");
        System.out.println(info);
        System.out.println("==========================");
        return "---------post any------------";
    }
}
