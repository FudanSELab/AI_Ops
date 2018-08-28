package hello;

public class Invocation {
    public String invocation_id;
    public String trace_id;
    public String session_id;
    public String req_duration;
    public String req_service;
    public String req_api;
    public String req_param;
    public String exec_duration;
    public String exec_logs;
    public String res_status_code;
    public String res_body;
    public String res_duration;
    public String is_error;

    public Invocation() {
        this.invocation_id = "0";
        this.session_id = "0";

        this.req_duration="0";
        this.req_service="0";


        this.req_param ="0";
        this.exec_logs ="0";

        this.res_status_code = "200";
        this.res_body="0";
        this.res_duration="0";


        this.is_error="0";
    }

    public Invocation(String invocation_id, String trace_id, String session_id, String req_duration, String req_service, String req_api, String req_param, String exec_duration, String exec_logs, String res_status_code, String res_body, String res_duration, String is_error) {
        this.invocation_id = invocation_id;
        this.trace_id = trace_id;
        this.session_id = session_id;
        this.req_duration = req_duration;
        this.req_service = req_service;
        this.req_api = req_api;
        this.req_param = req_param;
        this.exec_duration = exec_duration;
        this.exec_logs = exec_logs;
        this.res_status_code = res_status_code;
        this.res_body = res_body;
        this.res_duration = res_duration;
        this.is_error = is_error;
    }

    public String getInvocation_id() {
        return invocation_id;
    }

    public void setInvocation_id(String invocation_id) {
        this.invocation_id = invocation_id;
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getReq_duration() {
        return req_duration;
    }

    public void setReq_duration(String req_duration) {
        this.req_duration = req_duration;
    }

    public String getReq_service() {
        return req_service;
    }

    public void setReq_service(String req_service) {
        this.req_service = req_service;
    }

    public String getReq_api() {
        return req_api;
    }

    public void setReq_api(String req_api) {
        this.req_api = req_api;
    }

    public String getReq_param() {
        return req_param;
    }

    public void setReq_param(String req_param) {
        this.req_param = req_param;
    }

    public String getExec_duration() {
        return exec_duration;
    }

    public void setExec_duration(String exec_duration) {
        this.exec_duration = exec_duration;
    }

    public String getExec_logs() {
        return exec_logs;
    }

    public void setExec_logs(String exec_logs) {
        this.exec_logs = exec_logs;
    }

    public String getRes_status_code() {
        return res_status_code;
    }

    public void setRes_status_code(String res_status_code) {
        this.res_status_code = res_status_code;
    }

    public String getRes_body() {
        return res_body;
    }

    public void setRes_body(String res_body) {
        this.res_body = res_body;
    }

    public String getRes_duration() {
        return res_duration;
    }

    public void setRes_duration(String res_duration) {
        this.res_duration = res_duration;
    }

    public String getIs_error() {
        return is_error;
    }

    public void setIs_error(String is_error) {
        this.is_error = is_error;
    }

    @Override
    public String toString() {
        return "Invocation{" +
                "invocation_id='" + invocation_id + '\'' +
                ", trace_id='" + trace_id + '\'' +
                ", session_id='" + session_id + '\'' +
                ", req_duration='" + req_duration + '\'' +
                ", req_service='" + req_service + '\'' +
                ", req_api='" + req_api + '\'' +
                ", req_param='" + req_param + '\'' +
                ", exec_duration='" + exec_duration + '\'' +
                ", exec_logs='" + exec_logs + '\'' +
                ", res_status_code='" + res_status_code + '\'' +
                ", res_body='" + res_body + '\'' +
                ", res_duration='" + res_duration + '\'' +
                ", is_error='" + is_error + '\'' +
                '}';
    }
}
