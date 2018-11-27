package com.train.test.services;

public interface ZipkinLogCollectService {
    void startCopyLogToHDFS();
    String stopCopyLogToHDFS();
}
