package hello.domain;

public class TraceAnnotation {
    public String trace_id;
    public String span_name;
    public String span_id;
    public String parent_id;
    public String span_timestamp;
    public String span_duration;

    public String anno_a1_timestamp;
    public String anno_a1_value;
    public String anno_a1_ipv4;
    public String anno_a1_port;
    public String anno_a1_servicename;

    public String anno_a2_timestamp;
    public String anno_a2_value;
    public String anno_a2_ipv4;
    public String anno_a2_port;
    public String anno_a2_servicename;

    public String bnno_component;
    public String bnno_node_id;
    public String bnno_xrequest_id;
    public String bnno_httpurl;
    public String bnno_http_method;
    public String bnno_downstream_cluster;
    public String test_trace_id;
    public String test_case_id;

    public String bnno_http_protocol;
    public String bnno_request_size;
    public String bnno_upstream_cluster;
    public String bnno_status_code;
    public String bnno_response_size;
    public String bnno_response_flags;


    public TraceAnnotation() {
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
    }

    public String getSpan_name() {
        return span_name;
    }

    public void setSpan_name(String span_name) {
        this.span_name = span_name;
    }

    public String getSpan_id() {
        return span_id;
    }

    public void setSpan_id(String span_id) {
        this.span_id = span_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getSpan_timestamp() {
        return span_timestamp;
    }

    public void setSpan_timestamp(String span_timestamp) {
        this.span_timestamp = span_timestamp;
    }

    public String getSpan_duration() {
        return span_duration;
    }

    public void setSpan_duration(String span_duration) {
        this.span_duration = span_duration;
    }

    public String getAnno_a1_timestamp() {
        return anno_a1_timestamp;
    }

    public void setAnno_a1_timestamp(String anno_a1_timestamp) {
        this.anno_a1_timestamp = anno_a1_timestamp;
    }

    public String getAnno_a1_value() {
        return anno_a1_value;
    }

    public void setAnno_a1_value(String anno_a1_value) {
        this.anno_a1_value = anno_a1_value;
    }

    public String getAnno_a1_ipv4() {
        return anno_a1_ipv4;
    }

    public void setAnno_a1_ipv4(String anno_a1_ipv4) {
        this.anno_a1_ipv4 = anno_a1_ipv4;
    }

    public String getAnno_a1_port() {
        return anno_a1_port;
    }

    public void setAnno_a1_port(String anno_a1_port) {
        this.anno_a1_port = anno_a1_port;
    }

    public String getAnno_a1_servicename() {
        return anno_a1_servicename;
    }

    public void setAnno_a1_servicename(String anno_a1_servicename) {
        this.anno_a1_servicename = anno_a1_servicename;
    }

    public String getAnno_a2_timestamp() {
        return anno_a2_timestamp;
    }

    public void setAnno_a2_timestamp(String anno_a2_timestamp) {
        this.anno_a2_timestamp = anno_a2_timestamp;
    }

    public String getAnno_a2_value() {
        return anno_a2_value;
    }

    public void setAnno_a2_value(String anno_a2_value) {
        this.anno_a2_value = anno_a2_value;
    }

    public String getAnno_a2_ipv4() {
        return anno_a2_ipv4;
    }

    public void setAnno_a2_ipv4(String anno_a2_ipv4) {
        this.anno_a2_ipv4 = anno_a2_ipv4;
    }

    public String getAnno_a2_port() {
        return anno_a2_port;
    }

    public void setAnno_a2_port(String anno_a2_port) {
        this.anno_a2_port = anno_a2_port;
    }

    public String getAnno_a2_servicename() {
        return anno_a2_servicename;
    }

    public void setAnno_a2_servicename(String anno_a2_servicename) {
        this.anno_a2_servicename = anno_a2_servicename;
    }

    public String getBnno_component() {
        return bnno_component;
    }

    public void setBnno_component(String bnno_component) {
        this.bnno_component = bnno_component;
    }

    public String getBnno_node_id() {
        return bnno_node_id;
    }

    public void setBnno_node_id(String bnno_node_id) {
        this.bnno_node_id = bnno_node_id;
    }

    public String getBnno_xrequest_id() {
        return bnno_xrequest_id;
    }

    public void setBnno_xrequest_id(String bnno_xrequest_id) {
        this.bnno_xrequest_id = bnno_xrequest_id;
    }

    public String getBnno_httpurl() {
        return bnno_httpurl;
    }

    public void setBnno_httpurl(String bnno_httpurl) {
        this.bnno_httpurl = bnno_httpurl;
    }

    public String getBnno_http_method() {
        return bnno_http_method;
    }

    public void setBnno_http_method(String bnno_http_method) {
        this.bnno_http_method = bnno_http_method;
    }

    public String getBnno_downstream_cluster() {
        return bnno_downstream_cluster;
    }

    public void setBnno_downstream_cluster(String bnno_downstream_cluster) {
        this.bnno_downstream_cluster = bnno_downstream_cluster;
    }

    public String getTest_trace_id() {
        return test_trace_id;
    }

    public void setTest_trace_id(String test_trace_id) {
        this.test_trace_id = test_trace_id;
    }

    public String getTest_case_id() {
        return test_case_id;
    }

    public void setTest_case_id(String test_case_id) {
        this.test_case_id = test_case_id;
    }

    public String getBnno_http_protocol() {
        return bnno_http_protocol;
    }

    public void setBnno_http_protocol(String bnno_http_protocol) {
        this.bnno_http_protocol = bnno_http_protocol;
    }

    public String getBnno_request_size() {
        return bnno_request_size;
    }

    public void setBnno_request_size(String bnno_request_size) {
        this.bnno_request_size = bnno_request_size;
    }

    public String getBnno_upstream_cluster() {
        return bnno_upstream_cluster;
    }

    public void setBnno_upstream_cluster(String bnno_upstream_cluster) {
        this.bnno_upstream_cluster = bnno_upstream_cluster;
    }

    public String getBnno_status_code() {
        return bnno_status_code;
    }

    public void setBnno_status_code(String bnno_status_code) {
        this.bnno_status_code = bnno_status_code;
    }

    public String getBnno_response_size() {
        return bnno_response_size;
    }

    public void setBnno_response_size(String bnno_response_size) {
        this.bnno_response_size = bnno_response_size;
    }

    public String getBnno_response_flags() {
        return bnno_response_flags;
    }

    public void setBnno_response_flags(String bnno_response_flags) {
        this.bnno_response_flags = bnno_response_flags;
    }
}
