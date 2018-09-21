import numpy as np
import pandas as pd
import uuid
import time

train_columns = [
    "trace_id",
    "session_id",
    "testcase_id",
    "scenario_id",
    "entry_service",
    "entry_api",
    "entry_timestamp",
    "service_1_inst_delta",
    "service_1_conf_mem_limit_delta",
    "service_1_conf_cpu_limit_delta",
    "service_2_inst_delta",
    "service_2_conf_mem_limit_delta",
    "service_2_conf_cpu_limit_delta",
    "service_3_inst_delta",
    "service_3_conf_mem_limit_delta",
    "service_3_conf_cpu_limit_delta",
    "service_4_inst_delta",
    "service_4_conf_mem_limit_delta",
    "service_4_conf_cpu_limit_delta",
    "service_5_inst_delta",
    "service_5_conf_mem_limit_delta",
    "service_5_conf_cpu_limit_delta",
    "y_is_error_lazy",
    "y_is_error_predict",
    "y_is_error",
    "y_issue_ms",
    "y_issue_dimension"
]

data_size = 10

trace_ids = []
session_ids = []
scenario_ids = []
for i in range(data_size):
    trace_ids.append(uuid.uuid1())
    session_ids.append(uuid.uuid1())
    scenario_ids.append(uuid.uuid1())

testcase_ids = []
for i in range(data_size):
    testcase_ids.append(uuid.uuid1())

entry_service_set = ["service_1", "service_2", "service_3", "service_4", "service_5"]
entry_api_set = [
    ["service_1_api_1", "service_1_api_2"],
    ["service_2_api_1", "service_2_api_2", "service_2_api_3", "service_2_api_4"],
    ["service_3_api_1", "service_3_api_2", "service_3_api_3"],
    ["service_4_api_1", "service_4_api_2"],
    ["service_5_api_1", "service_5_api_2", "service_5_api_3", "service_5_api_4"]
]

entry_services = []
entry_apis = []
entry_timestamp = []
for i in range(data_size):
    service = np.random.randint(0, 5)
    entry_services.append(entry_service_set[service])
    api_len = len(entry_api_set[service])
    service_api = np.random.randint(0, api_len)
    entry_apis.append(entry_api_set[service][service_api])
    t = int(round(time.time() * 1000))
    entry_timestamp.append(t)


service_1_inst_delta = pd.Series(np.random.randint(1, 10, data_size))
service_2_inst_delta = pd.Series(np.random.randint(1, 10, data_size))
service_3_inst_delta = pd.Series(np.random.randint(1, 10, data_size))
service_4_inst_delta = pd.Series(np.random.randint(1, 10, data_size))
service_5_inst_delta = pd.Series(np.random.randint(1, 10, data_size))

service_1_conf_mem_limit_delta = pd.Series(np.random.randint(200, 1024, data_size))
service_2_conf_mem_limit_delta = pd.Series(np.random.randint(200, 1024, data_size))
service_3_conf_mem_limit_delta = pd.Series(np.random.randint(200, 1024, data_size))
service_4_conf_mem_limit_delta = pd.Series(np.random.randint(200, 1024, data_size))
service_5_conf_mem_limit_delta = pd.Series(np.random.randint(200, 1024, data_size))

service_1_conf_cpu_limit_delta = pd.Series(np.random.randint(30, 300, data_size))
service_2_conf_cpu_limit_delta = pd.Series(np.random.randint(30, 300, data_size))
service_3_conf_cpu_limit_delta = pd.Series(np.random.randint(30, 300, data_size))
service_4_conf_cpu_limit_delta = pd.Series(np.random.randint(30, 300, data_size))
service_5_conf_cpu_limit_delta = pd.Series(np.random.randint(30, 300, data_size))

ms_sf_set = ["Success", "Fail"]
y_is_error_lazys = []
y_is_error_predicts = []
y_is_error = []
for i in range(data_size):
    lazys_ran = np.random.randint(0, 2)
    predicts_ran = np.random.randint(0, 2)
    error_ran = np.random.randint(0, 2)
    y_is_error_lazys.append(ms_sf_set[lazys_ran])
    y_is_error_predicts.append(ms_sf_set[predicts_ran])
    y_is_error.append(ms_sf_set[error_ran])


issue_mss = []
issue_dimension = []
# for i in range(data_size):



train_datas = [
    trace_ids,
    session_ids,
    testcase_ids,
    scenario_ids,
    entry_services,
    entry_apis,
    entry_timestamp,
    service_1_inst_delta,
    service_1_conf_mem_limit_delta,
    service_1_conf_cpu_limit_delta,
    service_2_inst_delta,
    service_2_conf_mem_limit_delta,
    service_2_conf_cpu_limit_delta,
    service_3_inst_delta,
    service_3_conf_mem_limit_delta,
    service_3_conf_cpu_limit_delta,
    service_4_inst_delta,
    service_4_conf_mem_limit_delta,
    service_4_conf_cpu_limit_delta,
    service_5_inst_delta,
    service_5_conf_mem_limit_delta,
    service_5_conf_cpu_limit_delta,
    y_is_error_lazys,
    y_is_error_predicts,
    y_is_error,
]

