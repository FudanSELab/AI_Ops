package com.train.test.csvutil;

import org.apache.commons.beanutils.BeanUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVUtils {

    private static final String LINE_SEPARATOR = "line.separator";
    /**
     * create CVS file
     *
     * @param exportData source data List
     * @param headerMap        the header of csv file
     * @param outPutPath file path to export
     * @param fileName   file name to export
     * @return the csv file
     */
    @SuppressWarnings("rawtypes")
    public static File createCSVFile(List<LinkedHashMap<String, String>> exportData, LinkedHashMap headerMap, String outPutPath,
                                     String fileName, boolean contentFlag) {
        File csvFile = null;
        FileWriter fileWriter = null;

        try {
            // if the path exists
            File file = new File(outPutPath);
            if (!file.exists()) {
                file.mkdir();
            }

            // define the file name and create file
            csvFile = new File(outPutPath + "/" + fileName + ".csv");
            if (!csvFile.exists()) {
                csvFile.createNewFile();
            }

            System.out.println("Create the csvFile：" + csvFile);

            // true: append to the ending of the file; false: write at the beginning of the file
            fileWriter = new FileWriter(csvFile, true);

            // write the header into file
            writeHeaderToFile(csvFile, fileWriter, headerMap);

            // turn to the new line
            fileWriter.write(System.getProperty(LINE_SEPARATOR));

            // write the content into file
            writeContentToFile(fileWriter, exportData, headerMap, contentFlag);


            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileWriter){
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return csvFile;
    }

    private static void writeHeaderToFile(File csvFile, FileWriter fileWriter, LinkedHashMap headerMap) throws Exception {
        if (0 == csvFile.length()) {
            for (Iterator propertyIterator = headerMap.entrySet().iterator(); propertyIterator.hasNext(); ) {
                Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                fileWriter.write("\"" + (propertyEntry.getKey() != null ? (String) propertyEntry.getKey() : "") + "\"");
                if (propertyIterator.hasNext()) {
                    fileWriter.write(",");
                }
            }
        }
    }

    /**
     *
     * @param fileWriter the file writer of CSV file
     * @param exportData the data of service returned by call k8s API
     * @param headerMap the header map of CSV table
     * @param contentFlag true: one service data from one remote call write in one row
     *                    false: all service data from one remote call write in one row
     * @throws Exception Exception
     */
    private static void writeContentToFile(FileWriter fileWriter, List<LinkedHashMap<String, String>> exportData, LinkedHashMap headerMap,
                                           boolean contentFlag) throws Exception {

        if (contentFlag) {
            for (Iterator iterator = exportData.iterator(); iterator.hasNext(); ) {
                Object row = iterator.next();
                for (Iterator propertyIterator = headerMap.entrySet().iterator(); propertyIterator.hasNext(); ) {
                    Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                    fileWriter.write(BeanUtils.getProperty(row, (String) propertyEntry.getKey()));
                    if (propertyIterator.hasNext()) {
                        fileWriter.write(",");
                    }
                }
                if (iterator.hasNext()) {
                    fileWriter.write(System.getProperty(LINE_SEPARATOR));
                }
            }
        }
        else {
                int index;
                int sourceCount = exportData.get(0).size();
                Iterator propertyIterator = headerMap.entrySet().iterator();

                for (int i = 0; i < headerMap.size(); i++ ) {
                    index = i / sourceCount;
                    Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                    fileWriter.write(BeanUtils.getProperty(exportData.get(index), (String) propertyEntry.getValue()));
                    if (propertyIterator.hasNext()) {
                        fileWriter.write(",");
                    }
                }
            }

    }
}

