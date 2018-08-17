package hello;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvocationController {

    @RequestMapping("/hello1")
    public void hello1() {
//        (new String[]{"invocation_id", "trace_id", "session_id", "req_duration", "req_service", "req_api",
//                "req_param_*", "exec_duration", "exec_logs", "rest_status_code", "res_body_*", "res_duration", "error_or_not"});



    }
}
