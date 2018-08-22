package hello;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvocationController {

    @RequestMapping("/hello1")
    public String hello1() {
        System.out.println("================hello==============");
        return "hello test success";
    }

    @RequestMapping("/hdfsapi")
    public void testHDFS(){
        System.out.println("===============begin hdfs ==============");
        new HDFSApiDemo().testHDFS();
        System.out.println("=============== hdfs  end==============");
    }
}
