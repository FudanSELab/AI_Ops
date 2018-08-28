package hello;

import hello.storage.ParquetUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.Method;

@RestController
public class TestParquet {

    @RequestMapping(value = "/testparquet", method = RequestMethod.GET)
    public String handle_collect_any(@RequestBody String info) throws IOException {
        System.out.print("df----------------------------------");
        ParquetUtil.parquetReader("/parquet/traces.parquet");
        return "---------post any------------";
    }
}
