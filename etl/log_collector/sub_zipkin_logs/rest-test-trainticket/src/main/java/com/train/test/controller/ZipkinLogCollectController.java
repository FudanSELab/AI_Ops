package com.train.test.controller;


import com.train.test.services.ZipkinLogCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZipkinLogCollectController {

    @Autowired
    ZipkinLogCollectService zipkinLogCollectService;

    @GetMapping("/hello")
    public String hello() {
        System.out.println("============hello ===========");
        return "[hello World!]";
    }

    @GetMapping("/startCopyTraceToHdfs")
    public void startCopyFileToCsv() {
        System.out.println("===========start copy file to hdfs ========");
        zipkinLogCollectService.startCopyLogToHDFS();
    }

    @GetMapping("/stopCopyTraceToHdfs")
    public String stopCollectResourceData() {
        System.out.println("===========stop copy file to hdfs ========");
        return zipkinLogCollectService.stopCopyLogToHDFS();
    }
}
