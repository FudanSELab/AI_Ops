package com.train.test.services.impl;

import com.train.test.entity.TestFlowReporter;
import com.train.test.entity.TestResponse;
import com.train.test.csvutil.CsvFileTest;
import com.train.test.domain.Testcase;
import com.train.test.services.TestCaseService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Service;
import org.testng.ITestNGListener;
import org.testng.TestNG;

import java.io.IOException;

@Service
public class TestCaseServiceImpl implements TestCaseService {

    private Boolean flag = true;
    private static Long countNum = 0L;

    @Override
    public void startCopyTestCaseToHdfs() {
        while (flag) {
            try {
                System.out.println("=============copy testcase - " + countNum + " -times=============");
                countNum = countNum + 1;
                writeToHDFS();
                Thread.sleep(60000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public String stopCollectResourceData() {
        System.out.println("===========stop copy file to hdfs ========");
        flag = false;
        return "[copy test case file stoped]";
    }

    @Override
    public TestResponse testBookErrorConfig() throws IOException, ClassNotFoundException {
        // String url = "http://10.141.211.161:31380/login";
        String entry_service = "ts-login-service";
        String entry_api = "/login";
        String scenario_desc = "TestBookErrorConfig";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testFlowOne() throws IOException, ClassNotFoundException {
        // String url = "http://10.141.211.161:31380/login";
        String entry_service = "ts-login-service";
        String entry_api = "/login";
        String scenario_desc = "TestFlowOne";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testFlowTwoPay() throws IOException, ClassNotFoundException {
        // String url = "http://10.141.211.161:31380/login";
        String entry_service = "ts-login-service";
        String entry_api = "/login";
        String scenario_desc = "TestFlowTwoPay";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testFlowTwoRebook() throws IOException, ClassNotFoundException {
        // String url = "http://10.141.211.161:31380/login";
        String entry_service = "ts-login-service";
        String entry_api = "/login";
        String scenario_desc = "TestFlowTwoRebook";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testServiceFood() throws IOException, ClassNotFoundException {
        // String url = "http://10.141.211.161:31380/login";
        String entry_service = "ts-login-service";
        String entry_api = "/login";
        String scenario_desc = "TestServiceFood";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testFlowFour() throws IOException, ClassNotFoundException {
        // String url = "http://10.141.211.161:31380/login";
        String entry_service = "ts-login-service";
        String entry_api = "/login";
        String scenario_desc = "TestFlowFour";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testServiceSso() throws IOException, ClassNotFoundException {
        //  http://10.141.211.161:31380/account/findAll
        String entry_service = "ts-sso-service";
        String entry_api = "/account/findAll";
        String scenario_desc = "TestServiceSSO";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testServiceRoute() throws IOException, ClassNotFoundException {
        //  http://10.141.211.161:31380/route/queryAll
        String entry_service = "ts-route-service";
        String entry_api = "/route/queryAll";
        String scenario_desc = "TestServiceRoute";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }

    @Override
    public TestResponse testServiceVoucher() throws IOException, ClassNotFoundException {
        //  http://10.141.211.161:31380/route/queryAll
        String entry_service = "ts-route-service";
        String entry_api = "/route/queryAll";
        String scenario_desc = "TestServiceRoute";
        TestResponse response = testResult(scenario_desc, entry_service, entry_api);
        System.out.println("[test-result]==========" + response.getMessage());
        return response;
    }


    //  write testcase result to csv
    private TestResponse testResult(String scenario_desc, String entry_service, String entry_api) throws IOException, ClassNotFoundException {
        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[]{Class.forName("testcase." + scenario_desc)});

        TestFlowReporter tfr = new TestFlowReporter();
        testNG.addListener((ITestNGListener) tfr);
        testNG.setOutputDirectory("./test-output");

        Long entry_timeStamp = System.currentTimeMillis();
        testNG.run();

        TestResponse response = new TestResponse();
        response.setResultList(tfr.getResultList());
        Integer[] count = tfr.getResultCount();
        response.setResultCount(count);

        if (count[1] == 0 && count[2] == 0 && count[3] == 0) {
            response.setStatus(true);
            response.setMessage("All Passed");
        } else {
            response.setStatus(false);
            response.setMessage("Failed");
        }

        Testcase tc = new Testcase("0", "0", "0", scenario_desc, entry_service, entry_api, entry_timeStamp + "", response.getMessage() + "", response.isStatus() + "");
        CsvFileTest.writeCSV(tc);
        return response;
    }

    // copy csv to hdfs
    public static void writeToHDFS() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://10.141.211.173:8020");
        FileSystem fs = FileSystem.get(conf);
        System.out.println("================  begin create test_case.csv =============");
        Path newFile = new Path("hdfs://10.141.211.173:8020/user/admin/test_case.csv");
        if (fs.exists(newFile)) {
            fs.delete(newFile, false);
        }
        fs.copyFromLocalFile(new Path("/home/test_case.csv"), newFile);
        System.out.println("==========`======  end create test_case.csv =============");
    }
}
