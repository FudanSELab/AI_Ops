import preprocessing_set
import model
import multi_label_model
from pandas import DataFrame
import pandas as pd
from sklearn.utils import shuffle


# 为向麒麟准备Mock的数据
def for_model_2():
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
    df_one_1 = pd.read_csv(trace_csv_one[1], header=0, index_col=index_col)
    df_one_1.pop("test_trace_id")
    df_one_1.pop("test_case_id")
    df_one_2 = pd.read_csv(trace_csv_one[2], header=0, index_col=index_col)
    # 将各个部分的数据JOIN起来
    df_total1 = preprocessing_set.merge_data(df_trace=df_one_0,
                                             df_seq=df_one_1,
                                             df_seq_caller=df_one_2)
    df_total1.to_csv("110/model_1_seq_total")

    df_two_0 = pd.read_csv(trace_csv_two[0], header=0, index_col=index_col)
    df_two_1 = pd.read_csv(trace_csv_two[1], header=0, index_col=index_col)
    df_two_1.pop("test_trace_id")
    df_two_1.pop("test_case_id")
    df_two_2 = pd.read_csv(trace_csv_two[2], header=0, index_col=index_col)
    # 将各个部分的数据JOIN起来
    df_total2 = preprocessing_set.merge_data(df_trace=df_two_0,
                                             df_seq=df_two_1,
                                             df_seq_caller=df_two_2)
    df_total2.to_csv("110/model_1_inst_total")

    df_three_0 = pd.read_csv(trace_csv_three[0], header=0, index_col=index_col)
    df_three_1 = pd.read_csv(trace_csv_three[1], header=0, index_col=index_col)
    df_three_1.pop("test_trace_id")
    df_three_1.pop("test_case_id")
    df_three_2 = pd.read_csv(trace_csv_three[2], header=0, index_col=index_col)
    # 将各个部分的数据JOIN起来
    df_total3 = preprocessing_set.merge_data(df_trace=df_three_0,
                                             df_seq=df_three_1,
                                             df_seq_caller=df_three_2)
    df_total3.to_csv("110/model_1_config_total")

########################以下方法仅限Model_1#########################
# 尽可能缩小数据集以防内存占满
def get_min_data(df_raw: DataFrame):
    # 丢弃全列为NA的数据
    df_raw = preprocessing_set.drop_na_data(df_raw)
    # 丢弃全列值相同的数据
    df_raw = preprocessing_set.drop_all_same_data(df_raw)
    # 丢弃不需要的列
    df_raw = preprocessing_set.select_data(df_raw)
    return df_raw


def preprocessing_model_2():
    ts_model_2_data = ["data_for_big_model/train_ticket_model2/model2_config.csv",
                       "data_for_big_model/train_ticket_model2/model2_inst.csv",
                       "data_for_big_model/train_ticket_model2/model2_seq.csv",]
    ts_model2_config = pd.read_csv(ts_model_2_data[0], header=0, index_col=None)
    ts_model2_inst = pd.read_csv(ts_model_2_data[1], header=0, index_col=None)
    ts_model2_seq = pd.read_csv(ts_model_2_data[2], header=0, index_col=None)
    ts_temp_append = preprocessing_set.append_data(ts_model2_config, ts_model2_inst)
    ts_model2_total = preprocessing_set.append_data(ts_temp_append, ts_model2_seq)

    ts_model2_total = preprocessing_set.drop_na_data(ts_model2_total)

    print(ts_model2_total.keys())

    # ts_model2_total = preprocessing_set.drop_all_same_data(ts_model2_total)

    ts_model2_total.pop("issue_content")

    ts_model2_total = preprocessing_set.fill_empty_data_model2(ts_model2_total)
    ts_model2_total = preprocessing_set.convert_data_model2(ts_model2_total)
    ts_model2_total.to_csv("ts_model2_total.csv")


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
    # df_total = preprocessing_set.sampling(df_total, "y_final_result")
    # 过采样后打乱数据
    df_total = shuffle(df_total)
    # 输出数据
    df_total.to_csv("ready_use_max_without_sampling.csv")


# 读取准备好的数据，丢弃无用列，分割训练集和测试集合，对训练集过采样保持分类平衡，并开始训练
def train_version_2():
    # 读入之前准备好的数据
    df = pd.read_csv("ready_use_max_without_sampling.csv", header=0, index_col="trace_id")
    df.pop("y_final_result")
    df.pop("y_issue_ms")
    df.pop("trace_api")
    df.pop("trace_service")
    df_train_raw, df_test = preprocessing_set.split_data(df, 0.8)
    df_train = preprocessing_set.sampling(df_train_raw, "y_issue_dim_type")
    multi_label_model.knn_multi_label_provided_train_test(df_train,df_test,"y_issue_dim_type")
    # model.dt_rf_multi_label_single_privided_train_test(df_train, df_test, "y_issue_dim_type")
    # model.dt_rf_multi_label_single_privided_train_test_no_multi_label(df_train,df_test,"y_final_result")


def train():
    # 读入之前准备好的数据
    df = pd.read_csv("ready_use_max.csv", header=0, index_col=0)

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
    df.pop("trace_api")
    df.pop("trace_service")

    # 选择训练和测试数据集
    df_train = df.loc[(df["y_issue_ms"] != "ts-preserve-service")
                       | (df["y_issue_dim_type"] != "seq")]

    #df_train = preprocessing_set.sampling(df_train, "y_issue_ms")

    df_test = df.loc[(df["y_issue_ms"] == "ts-preserve-service")
                     & (df["y_issue_dim_type"] == "seq")]
                     # & (df["y_issue_dim_type"] == "config")]
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
    df_train = df.loc[(df["y_issue_ms"] != "ts-travel-service")]
    df_test = df.loc[(df["y_issue_ms"] == "ts-travel-service") ]
    print("故障服务不是该服务但Trace途径该服务")
    print(df_train["y_issue_ms"].value_counts())
    print(df_train["y_issue_dim_type"].value_counts())
    print("故障服务是该服务")
    print(df_test["y_issue_dim_type"].value_counts())


########################以下方法仅限Model_2#########################



if __name__ == "__main__":
    preprocessing_model_2()












    # df = pd.read_csv("ready_use_max_without_sampling.csv", header=0, index_col="trace_id")
    # df.pop("y_final_result")
    # df.pop("y_issue_ms")
    # df.pop("trace_api")
    # df.pop("trace_service")
    # multi_label_model.knn_total(df=df,
    #                            y_name="y_issue_dim_type",
    #                            test_ratio=0.2,
    #                            n_neighbors_list=[5,30,100])
