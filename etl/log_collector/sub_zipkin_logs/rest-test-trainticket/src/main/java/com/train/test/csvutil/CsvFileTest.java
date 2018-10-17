package com.train.test.csvutil;

import com.Ostermiller.util.CSVPrint;
import com.Ostermiller.util.CSVPrinter;
import com.train.test.domain.Testcase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvFileTest {


    private CSVPrint csvPrint;

    public CsvFileTest(String fileName, boolean append) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
            init();
        } else {
            csvPrint = new CSVPrinter(new FileWriter(fileName, append));
            if (!append) {
                init();
            }
        }

    }

    public void init() throws IOException {
        write(new String[]{"testcase_id", "scenario_id", "session_id","scenario_desc", "entry_service", "entry_api", "entry_timestamp", "result", "is_error"});
    }

    public void write(String[] values) throws IOException {
        csvPrint.writeln(values);
    }


    public static void writeCSV(Testcase tc) throws IOException {
        String annoCsvFile = "/home/test_case.csv";
        CsvFileTest testCase = new CsvFileTest(annoCsvFile, true);
        //写入 anno csv
        testCase.write(new String[]{
                tc.getTestcase_id(),
                tc.getScenario_id(),
                tc.getSession_id(),
                tc.getScenario_desc(),
                tc.getEntry_service(),
                tc.getEntry_api(),
                tc.getEntry_timestamp(),
                tc.getResult(),
                tc.getIs_error()
        });
    }

}
