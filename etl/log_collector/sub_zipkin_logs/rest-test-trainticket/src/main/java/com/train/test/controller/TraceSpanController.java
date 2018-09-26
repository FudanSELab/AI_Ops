package com.train.test.controller;


import com.train.test.services.HDFSApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TraceSpanController {

    @GetMapping("/startCopyTraceToHdfs")
    public void startCopyFileToCsv() {
        System.out.println("===========start copy file to hdfs ========");
        HDFSApiService.getResourceData();
    }

    @GetMapping("/stopCopyTraceToHdfs")
    public String stopCollectResourceData() {
        System.out.println("===========stop copy file to hdfs ========");
        return HDFSApiService.stopCopyFileToHDFS();
    }

    @GetMapping("/hello")
    public String hello(){
        System.out.println("============hello ===========");
        return "[hello World!]";
    }
}
