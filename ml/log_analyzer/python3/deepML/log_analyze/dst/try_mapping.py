import pandas as pd
import numpy as np
from sklearn import tree
from sklearn.externals.six import StringIO
import pydotplus

CSV_COLUMN_NAMES = [
    "invocation_id",
    "trace_id",
    "session_id",
    "req_duration",
    "req_service",
    "req_api",
    "req_params",
    "exec_duration",
    "exec_logs",
    "res_status_code",
    "res_status_desc",
    "res_exception",
    "res_body",
    "res_duration",
    "is_error"
]

# column_dtype = {
#     "invocation_id": np.str,
#     "trace_id": np.str,
#     "session_id": np.str,
#     "req_duration": np.int32,
#     "req_service": np.str,
#     "req_api": np.str,
#     "req_params": np.str,
#     "exec_duration": np.int32,
#     "exec_logs": np.str,
#     "res_status_code": np.int32,
#     "res_body": np.str,
#     "res_duration": np.int32,
#     "is_error": np.int32
# }

train_path = "invocation(1).csv"
train = pd.read_csv(train_path,
                    names=CSV_COLUMN_NAMES,
                    # dtype=column_dtype,
                    header=0)

# 1.丢弃invocation_id
print("==1==")
invocations_id = train.pop("invocation_id")
# 2.丢弃trace_id
print("==2==")
traces_id = train.pop("trace_id")
# 3.丢弃session_id
print("==3==")
sessions_id = train.pop("session_id")
# 4.处理req_duration
print("==4==")
# 5.处理req_service
print("==5==")
mapping_keys = train["req_service"].drop_duplicates().values
mapping = {}
for i in range(len(mapping_keys)):
    mapping[mapping_keys[i]] = i
train["req_service"] = train["req_service"].map(mapping)
# 6.处理req_api
print("==6==")
mapping_keys = train["req_api"].drop_duplicates().values
mapping = {}
for i in range(len(mapping_keys)):
    mapping[mapping_keys[i]] = i
train["req_api"] = train["req_api"].map(mapping)
# 7.丢弃req_param
print("==7==")
train.pop("req_params")
# 8.处理exec_duration
print("==8==")
mapping_keys = train["exec_duration"].drop_duplicates().values
mapping = {}
for i in range(len(mapping_keys)):
    if "#" in str(mapping_keys[i]):
        mapping[mapping_keys[i]] = -1
    else:
        mapping[mapping_keys[i]] = mapping_keys[i]
train["exec_duration"] = train["exec_duration"].map(mapping)
# 9.丢弃exec_logs
print("==9==")
train.pop("exec_logs")
# 10.处理res_status
print("==10==")
mapping_keys = train["res_status_code"].drop_duplicates().values
mapping = {}
for i in range(len(mapping_keys)):
    mapping[mapping_keys[i]] = i
train["res_status_code"] = train["res_status_code"].map(mapping)
# 11.处理res_status_desc
print("==11==")
mapping_keys = train["res_status_desc"].drop_duplicates().values
mapping = {}
for i in range(len(mapping_keys)):
    mapping[mapping_keys[i]] = i
train["res_status_desc"] = train["res_status_desc"].map(mapping)
# 12.处理res_exception
print("==12==")
mapping_keys = train["res_exception"].drop_duplicates().values
mapping = {}
for i in range(len(mapping_keys)):
    mapping[mapping_keys[i]] = i
train["res_exception"] = train["res_exception"].map(mapping)
# 13.丢弃res_body
print("==13==")
train.pop("res_body")
# 14.处理res_duration
print("==14==")
# 15.处理is_error
print("==15==")

train_x, train_y = train, train.pop("is_error")

print(train_x)

clf = tree.DecisionTreeClassifier(min_samples_split=15)
clf = clf.fit(train_x, train_y)
feature_name = [
    "req_duration",
    "req_service",
    "req_api",
    "exec_duration",
    "res_status_code",
    "res_status_desc",
    "res_exception",
    "res_duration",
]
target_name = ['Success', 'Failure']
dot_data = StringIO()
tree.export_graphviz(clf,
                     out_file=dot_data,
                     feature_names=feature_name,
                     class_names=target_name,
                     filled=True,
                     rounded=True,
                     special_characters=True)
graph = pydotplus.graph_from_dot_data(dot_data.getvalue())
graph.write_pdf("dst_cut.pdf")







