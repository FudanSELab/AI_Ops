import preprocessing_set
import model
from pandas import DataFrame
import pandas as pd
from sklearn.utils import shuffle
import numpy as np


def print_best_score(gsearch, param_test, log_file_name):
    f = open(log_file_name, 'w+')
    print("Best score: %0.3f" % gsearch.best_score_, file=f)
    print("Best parameters set:", file=f)
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]), file=f)


# 尽可能缩小数据集以防内存占满
def get_min_data(df_raw: DataFrame):
    # 丢弃全列为NA的数据
    df_raw = preprocessing_set.drop_na_data(df_raw)
    # 丢弃全列值相同的数据
    df_raw = preprocessing_set.drop_all_same_data(df_raw)
    # 丢弃不需要的列
    df_raw = preprocessing_set.select_data(df_raw)
    return df_raw


def inspect_data(file_name, index_name):
    df_raw = pd.read_csv(file_name, header=0, index_col=index_name)
    cols = df_raw.keys()
    for col in cols:
        print(col)


# 完成预处理并保存数据集
def preprocessing():
    # First Set of CSV
    trace_csv_one = ["110/trace_verified_sequence.csv",
                     "110/seq_seq_sequence.csv",
                     "110/seq_caller_sequence.csv"]
    # Second Set of CSV
    trace_csv_two = ["110/trace_verified_instance3.csv",
                     "110/seq_seq_instance3.csv",
                     "110/seq_caller_instance3.csv"]
    # Third Set of CSV
    trace_csv_three = ["110/trace_verified_config_2.csv",
                       "110/seq_seq_config.csv",
                       "110/seq_caller_config.csv"]
    # Index Column Name
    index_col = "trace_id"
    # Read CONFIG and INSTANCE
    df_one_0 = pd.read_csv(trace_csv_one[0], header=0, index_col=index_col)
    df_two_0 = pd.read_csv(trace_csv_two[0], header=0, index_col=index_col)
    df_three_0 = pd.read_csv(trace_csv_three[0], header=0, index_col=index_col)
    df_total_0 = preprocessing_set.append_data(df_one_0, df_two_0)
    df_total_0 = preprocessing_set.append_data(df_total_0, df_three_0)
    df_total_0 = get_min_data(df_total_0)
    # Read SEQUENCE-SEQ
    df_one_1 = pd.read_csv(trace_csv_one[1], header=0, index_col=index_col)
    df_two_1 = pd.read_csv(trace_csv_two[1], header=0, index_col=index_col)
    df_three_1 = pd.read_csv(trace_csv_three[1], header=0, index_col=index_col)
    df_total_1 = preprocessing_set.append_data(df_one_1, df_two_1)
    df_total_1 = preprocessing_set.append_data(df_total_1, df_three_1)
    df_total_1 = get_min_data(df_total_1)
    # Read SEQUENCE - CALLER
    df_one_2 = pd.read_csv(trace_csv_one[2], header=0, index_col=index_col)
    df_two_2 = pd.read_csv(trace_csv_two[2], header=0, index_col=index_col)
    df_three_2 = pd.read_csv(trace_csv_three[2], header=0, index_col=index_col)
    df_total_2 = preprocessing_set.append_data(df_one_2, df_two_2)
    df_total_2 = preprocessing_set.append_data(df_total_2, df_three_2)
    df_total_2 = get_min_data(df_total_2)
    # 将各个部分的数据JOIN起来
    df_total = preprocessing_set.merge_data(df_trace=df_total_0,
                                            df_seq=df_total_1,
                                            df_seq_caller=df_total_2)
    # 填补空缺值
    df_total = preprocessing_set.fill_empty_data(df_total)
    # 丢弃没故障数据
    df_total = df_total.loc[df_total["y_issue_ms"] != "Success"]
    # 把不规则的值转换成数字
    df_total = preprocessing_set.convert_data(df_total)
    # 按照某个Label对数据进行过采样以平衡样本数量
    df_total = preprocessing_set.sampling(df_total, "y_issue_ms")
    # 过采样后打乱数据
    df_total = shuffle(df_total)
    # 输出数据
    df_total.to_csv("ready_use_min.csv")


def train():
    # 读入之前准备好的数据
    df = pd.read_csv("ready_use.csv", header=0, index_col=0)

    # 准备做无量纲化操作 - 决策树、随机森林不需要这一步骤
    # temp1 = df.pop("y_issue_dim_type")
    # temp2 = df.pop("y_issue_ms")
    # df = preprocessing_set.dimensionless(df)
    # df["y_issue_dim_type"] = temp1
    # df["y_issue_ms"] = temp2

    # 丢掉不作为feature使用的Label列
    df.pop("y_final_result")
    # df.pop("y_issue_ms")
    # df.pop("y_issue_dim_type")

    # 尝试丢掉一些属性
    # df.pop("trace_api")
    # df.pop("trace_service")

    # 选择训练和测试数据集
    df_train = df.loc[(df["y_issue_ms"] != "ts-contacts-service")
                      & (df["ts_contacts_service_included"] == 1)
                      & (df["y_issue_dim_type"] == "config")]

    #df_train = preprocessing_set.sampling(df_train, "y_issue_ms")

    df_test = df.loc[(df["y_issue_ms"] == "ts-contacts-service")
                     & (df["y_issue_dim_type"] == "config")]
    # df_train = df.loc[(df["y_issue_ms"] != "ts-preserve-other-service")]
    # df_test = df.loc[(df["y_issue_ms"] == "ts-preserve-other-service")]
    df_train.pop("y_issue_dim_type")
    df_test.pop("y_issue_dim_type")



    # 拿去训练
    model.dt_rf_multi_label_single_privided_train_test(df_train=df_train,
                                                       df_test=df_test,
                                                       y_name="y_issue_ms")


def inspect():
    df = pd.read_csv("ready_use.csv", header=0, index_col=0)
    print(df["y_issue_ms"].value_counts())
    df.pop("y_final_result")
    df_train = df.loc[(df["y_issue_ms"] != "ts-contacts-service")
                      & (df["ts_contacts_service_included"] == 1)
                      & (df["y_issue_dim_type"] == "config")]
    df_test = df.loc[(df["y_issue_ms"] == "ts-contacts-service") ]
    print("故障服务不是该服务但Trace途径该服务")
    print(df_train["y_issue_ms"].value_counts())
    print(df_train["y_issue_dim_type"].value_counts())
    print("故障服务是该服务")
    print(df_test["y_issue_dim_type"].value_counts())


def cut():
    df = pd.read_csv("110/trace_verified_instance2.csv", header=0, index_col=0)
    cols = df.keys()
    for col in cols:
        if col.endswith("_diff")\
                or col.endswith("_cpu") \
                or col.endswith("_memory") \
                or col.endswith("_limit"):
            df[col].fillna(0, inplace=True)
            df[col] = pd.cut(df[col], bins=5, labels=[1, 2, 3, 4, 5])
            print("Cut:", col)
        elif col.endswith("_api"):
            mapping_keys = df[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df[col] = df[col].map(mapping)

    df.to_csv("trace_verified_instance2_discretization.csv")
    return df


if __name__ == "__main__":

    # preprocessing()

    cut()

    # train()
    # inspect()


# if __name__ == "__main__":
#     trace_csv = "17/trace_verified_instance.csv"
#     trace_index_col = "trace_verified_instance_1_7.trace_id"
#     seq_csv = "17/seq_seq_instance.csv"
#     seq_index_col = "seq_seq_instance2.trace_id"
#     seq_caller_csv = "17/seq_caller_instance.csv"
#     seq_caller_index_col = "seq_caller_instance2.trace_id"
#
#     df_trace = pd.read_csv(trace_csv,
#                            header=0,
#                            index_col=trace_index_col)
#     df_seq = pd.read_csv(seq_csv,
#                          header=0,
#                          index_col=seq_index_col)
#     df_seq_caller = pd.read_csv(seq_caller_csv,
#                                 header=0,
#                                 index_col=seq_caller_index_col)
#
#     df_trace = preprocessing_set.drop_na_data(df_trace)
#     df_trace = preprocessing_set.drop_all_same_data(df_trace)
#     df_trace = preprocessing_set.select_data(df_trace)
#
#     df_seq = preprocessing_set.drop_na_data(df_seq)
#     df_seq = preprocessing_set.drop_all_same_data(df_seq)
#     df_seq = preprocessing_set.select_data(df_seq)
#
#     df_seq_caller = preprocessing_set.drop_na_data(df_seq_caller)
#     df_seq_caller = preprocessing_set.drop_all_same_data(df_seq_caller)
#     df_seq_caller = preprocessing_set.select_data(df_seq_caller)
#
#     df = preprocessing_set.merge_data(df_trace=df_trace,
#                                       df_seq=df_seq,
#                                       df_seq_caller=df_seq_caller)
#
#     df = preprocessing_set.fill_empty_data(df)
#
#
#     # df = preprocessing_set.convert_data(df)
#
#
#
#     # df = df.loc[df["trace_verified_instance_1_7.y_issue_ms"] != "Success"]
#
#     df = preprocessing_set.convert_data(df)
#     # df, y_multi_label = preprocessing_set.convert_y_multi_label(df, "trace_verified_instance_1_7.y_issue_ms")
#     # df = preprocessing_set.convert_y_multi_label(df, "trace_verified_instance_1_7.y_issue_dim_type")
#
#     df.pop("trace_verified_instance_1_7.y_final_result")
#     # df.pop("trace_verified_instance_1_7.y_issue_ms")
#     df.pop("trace_verified_instance_1_7.y_issue_dim_type")
#
#     model.dt_single(df, "trace_verified_instance_1_7.y_issue_ms")
#
#
#     # cv, parm = model.dt_multi_label(df, y_multi_label)
#     # print_best_score(cv, parm)
#
#     # You must save the preprocessing result.
#     # df.to_csv("test_run.csv")
#
#
#     # cv, parm = model.dt(df, "trace_verified_instance_1_7.y_final_result")
#     # cv, parm = model.dt(df, "trace_verified_instance_1_7.y_issue_ms")
#     # # cv, parm = model.dt(df, "trace_verified_instance_1_7.y_issue_dim_type")
#     # print_best_score(cv, parm)
