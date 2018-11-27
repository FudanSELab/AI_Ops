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
    "res_body",
    "res_duration",
    "is_error"
]

column_dtype = {
    "invocation_id": np.str,
    "trace_id": np.str,
    "session_id": np.str,
    "req_duration": np.int32,
    "req_service": np.str,
    "req_api": np.str,
    "req_params": np.str,
    "exec_duration": np.int32,
    "exec_logs": np.str,
    "res_status_code": np.int32,
    "res_body": np.str,
    "res_duration": np.int32,
    "is_error": np.int32
}


train_path = "sample.csv"
train = pd.read_csv(train_path,
                    names=CSV_COLUMN_NAMES,
                    dtype=column_dtype,
                    header=0)

# 这3个id暂时没用
invocations_id = train.pop("invocation_id")
traces_id = train.pop("trace_id")
sessions_id = train.pop("session_id")

# 抛去空数据
train.pop("req_params")
train.pop("exec_logs")
train.pop("res_body")

print(train.head())

# 按列处理数据
## 第一列: req_duration 直接用数字不做处理
print("==1==")
## 第二列: req_service  服务种类，无大小之分，采用独热编码
print("==2==")
train = pd.get_dummies(train, prefix=["svc"], columns=["req_service"])


## 第三列: req_api 不含参数使用独热编码
print("==3==")
train = pd.get_dummies(train, prefix=["api"], columns=["req_api"])

## 第四列: exec_duration 直接使用数字，但有些数字看上去不太合理
print("==4==")
# ## 第五列: res_status_code 状态吗，使用独热编码
print("==5==")
train = pd.get_dummies(train, prefix=["status"], columns=["res_status_code"])
# train = pd.get_dummies(train, prefix='res_code', columns="res_status_code")
print(train.keys())

## 第六列: res_duration 直接使用数字，但有些数字看上去不太合理
print("==6==")

# # 开始训练决策树
# clf = tree.DecisionTreeClassifier()
#
# train_x, train_y = train, train.pop("is_error")
#
# clf = clf.fit(train_x, train_y)
#
# feature_name = [
#     "req_duration",
#     "req_service",
#     "req_api",
#     "exec_duration",
#     "res_status_code",
#     "res_duration"]
# target_name = ['Success', 'Failure']
# dot_data = StringIO()
# tree.export_graphviz(clf,
#                      out_file = dot_data,
#                      feature_names=feature_name,
#                      class_names=target_name,
#                      filled=True,
#                      rounded=True,
#                      special_characters=True)
# graph = pydotplus.graph_from_dot_data(dot_data.getvalue())
# graph.write_pdf("sample_tree.pdf")





