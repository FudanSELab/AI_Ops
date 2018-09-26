package com.train.test.domain;

public class Testcase {
    public String testcase_id;
    public String scenario_id;
    public String session_id;
    public String scenario_desc;
    public String entry_service;
    public String entry_api;
    public String entry_timestamp;
    public String result;
    public String is_error;

    public Testcase() {
    }

    public Testcase(String testcase_id, String scenario_id, String session_id, String scenario_desc,String entry_service, String entry_api, String entry_timestamp, String result, String is_error) {
        this.testcase_id = testcase_id;
        this.scenario_id = scenario_id;
        this.session_id = session_id;
        this.scenario_desc = scenario_desc;
        this.entry_service = entry_service;
        this.entry_api = entry_api;
        this.entry_timestamp = entry_timestamp;
        this.result = result;
        this.is_error = is_error;
    }

    public String getScenario_desc() {
        return scenario_desc;
    }

    public void setScenario_desc(String scenario_desc) {
        this.scenario_desc = scenario_desc;
    }

    public String getTestcase_id() {
        return testcase_id;
    }

    public void setTestcase_id(String testcase_id) {
        this.testcase_id = testcase_id;
    }

    public String getScenario_id() {
        return scenario_id;
    }

    public void setScenario_id(String scenario_id) {
        this.scenario_id = scenario_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getEntry_service() {
        return entry_service;
    }

    public void setEntry_service(String entry_service) {
        this.entry_service = entry_service;
    }

    public String getEntry_api() {
        return entry_api;
    }

    public void setEntry_api(String entry_api) {
        this.entry_api = entry_api;
    }

    public String getEntry_timestamp() {
        return entry_timestamp;
    }

    public void setEntry_timestamp(String entry_timestamp) {
        this.entry_timestamp = entry_timestamp;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getIs_error() {
        return is_error;
    }

    public void setIs_error(String is_error) {
        this.is_error = is_error;
    }
}
