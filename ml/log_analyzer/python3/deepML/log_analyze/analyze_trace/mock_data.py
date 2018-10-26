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
    "y_is_error_lazy",
    "y_is_error_predict",
    "y_is_error",
    "y_issue_ms",
    "y_issue_dimension"
]

data_size = 3000

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

entry_service_set = ["service_1", "service_2", "service_3"]
entry_api_set = [
    ["service_1_api_1", "service_1_api_2"],
    ["service_2_api_1", "service_2_api_2", "service_2_api_3", "service_2_api_4"],
    ["service_3_api_1", "service_3_api_2", "service_3_api_3"],
]
issue_dimension_set = [
    "instance",
    "memory",
    "cpu"
]

entry_services = []
entry_apis = []
entry_timestamp = []

service_1_inst_deltas = []
service_1_conf_mem_limit_deltas = []
service_1_conf_cpu_limit_deltas = []
service_2_inst_deltas = []
service_2_conf_mem_limit_deltas = []
service_2_conf_cpu_limit_deltas = []
service_3_inst_deltas = []
service_3_conf_mem_limit_deltas = []
service_3_conf_cpu_limit_deltas = []

issue_mss = []
issue_dimensions = []

for i in range(data_size):
    # Service
    service = np.random.randint(0, 3)
    entry_services.append(entry_service_set[service])
    api_len = len(entry_api_set[service])
    # Service API
    service_api = np.random.randint(0, api_len)
    entry_apis.append(entry_api_set[service][service_api])
    # Service Timestamp
    t = int(round(time.time() * 1000))
    entry_timestamp.append(t)
    # issue_ms, dimention and dimension value
    issue_ms = np.random.randint(0, 3)
    issue_mss.append(entry_service_set[issue_ms])

    issue_dimension = np.random.randint(0, 3)
    issue_dimensions.append(issue_dimension_set[issue_dimension])

    if issue_ms == 0:
        if issue_dimension == 0:
            service_1_inst_deltas.append(np.random.randint(5, 10))
            service_1_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
            service_1_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        elif issue_dimension == 1:
            service_1_inst_deltas.append(np.random.randint(1, 5))
            service_1_conf_mem_limit_deltas.append(np.random.randint(0, 200))
            service_1_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        elif issue_dimension == 2:
            service_1_inst_deltas.append(np.random.randint(1, 5))
            service_1_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
            service_1_conf_cpu_limit_deltas.append(np.random.randint(10, 30))

        service_2_inst_deltas.append(np.random.randint(1, 5))
        service_3_inst_deltas.append(np.random.randint(1, 5))
        service_2_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
        service_3_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
        service_2_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        service_3_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
    elif issue_ms == 1:
        if issue_dimension == 0:
            service_2_inst_deltas.append(np.random.randint(5, 10))
            service_2_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
            service_2_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        elif issue_dimension == 1:
            service_2_inst_deltas.append(np.random.randint(1, 5))
            service_2_conf_mem_limit_deltas.append(np.random.randint(0, 200))
            service_2_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        elif issue_dimension == 2:
            service_2_inst_deltas.append(np.random.randint(1, 5))
            service_2_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
            service_2_conf_cpu_limit_deltas.append(np.random.randint(10, 30))

        service_1_inst_deltas.append(np.random.randint(1, 5))
        service_3_inst_deltas.append(np.random.randint(1, 5))
        service_1_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
        service_3_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
        service_1_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        service_3_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
    elif issue_ms == 2:
        if issue_dimension == 0:
            service_3_inst_deltas.append(np.random.randint(5, 10))
            service_3_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
            service_3_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        elif issue_dimension == 1:
            service_3_inst_deltas.append(np.random.randint(1, 5))
            service_3_conf_mem_limit_deltas.append(np.random.randint(0, 200))
            service_3_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        elif issue_dimension == 2:
            service_3_inst_deltas.append(np.random.randint(1, 5))
            service_3_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
            service_3_conf_cpu_limit_deltas.append(np.random.randint(10, 30))

        service_1_inst_deltas.append(np.random.randint(1, 5))
        service_2_inst_deltas.append(np.random.randint(1, 5))
        service_1_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
        service_2_conf_mem_limit_deltas.append(np.random.randint(200, 1024))
        service_1_conf_cpu_limit_deltas.append(np.random.randint(30, 300))
        service_2_conf_cpu_limit_deltas.append(np.random.randint(30, 300))


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

train_datas = [
    trace_ids,
    session_ids,
    testcase_ids,
    scenario_ids,
    entry_services,
    entry_apis,
    entry_timestamp,
    service_1_inst_deltas,
    service_1_conf_mem_limit_deltas,
    service_1_conf_cpu_limit_deltas,
    service_2_inst_deltas,
    service_2_conf_mem_limit_deltas,
    service_2_conf_cpu_limit_deltas,
    service_3_inst_deltas,
    service_3_conf_mem_limit_deltas,
    service_3_conf_cpu_limit_deltas,
    y_is_error_lazys,
    y_is_error_predicts,
    y_is_error,
    issue_mss,
    issue_dimensions
]

# print(dict(zip([1,2], [3,4])))
# dates = pd.date_range('1/1/2000', periods=8)
# data = pd.DataFrame(np.random.randn(8, 4), index=dates, columns=train_columns)

# pd.Series(np.random.randint(0,10,6).astype("str"));
# data1 = pd.Series(np.random.randint(0,10,6).astype("str"));
# print(data1.map(lambda x: "service" + x))

# data gen
train_data_final = pd.DataFrame(dict(zip(train_columns, train_datas)))


# train_data_final = train_data_final.index.reset_index(drop=True);
train_data_final = train_data_final.loc[:,train_columns]

print(train_data_final)

# write csv
train_data_final.to_csv("mock.csv")

