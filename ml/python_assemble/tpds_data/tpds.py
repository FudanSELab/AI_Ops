import pandas as pd
import preprocessing_set
from sklearn.utils import shuffle


def merge_all():
    ts_part_1 = pd.read_csv("ts/trace_y_config_cpu_extract_1.csv", header=0, index_col=None)
    ts_part_2 = pd.read_csv("ts/trace_y_config_memory_extract_1.csv", header=0, index_col=None)
    ts_part_3 = pd.read_csv("ts/trace_y_instance_extract_1.csv", header=0, index_col=None)
    ts_part_4 = pd.read_csv("ts/trace_y_instance_extract_2.csv", header=0, index_col=None)
    ts_part_5 = pd.read_csv("ts/trace_y_sequence_extract_1.csv", header=0, index_col=None)
    ts_part_6 = pd.read_csv("ts/trace_y_sequence_extract_2.csv", header=0, index_col=None)
    ts_total = preprocessing_set.append_data(ts_part_1,ts_part_2)
    ts_total = preprocessing_set.append_data(ts_total,ts_part_3)
    ts_total = preprocessing_set.append_data(ts_total,ts_part_4)
    ts_total = preprocessing_set.append_data(ts_total,ts_part_5)
    ts_total = preprocessing_set.append_data(ts_total,ts_part_6)

    ts_total["y_issue_ms"].fillna("Success", inplace=True)
    ts_total["y_issue_dim_type"].fillna("Success", inplace=True)

    ts_total = ts_total.loc[ts_total["y_issue_dim_type"] != "Success"]

    ts_total = shuffle(ts_total)
    print("总数据量:", len(ts_total))
    ts_total.to_csv("ts_tpds_total.csv")


def split_test_train():
    ts_tpds_total = pd.read_csv("ts_tpds_total.csv", header=0, index_col=0)
    print("总数据量:", len(ts_tpds_total))
    ts_tpds_test, ts_tpds_train = preprocessing_set.split_data(ts_tpds_total, 0.1)
    print("训练数据量:", len(ts_tpds_train))
    print("测试数据量:", len(ts_tpds_test))
    ts_tpds_test.to_csv("ts_tpds_test.csv")
    ts_tpds_train.to_csv("ts_tpds_train.csv")


def merge_all_sockshop():
    ss_part_1 = pd.read_csv("ss/trace_y_config_cpu_sockshop_extract_1.csv", header=0, index_col=None)
    ss_part_2 = pd.read_csv("ss/trace_y_config_mem_sockshop_extract_1.csv", header=0, index_col=None)
    ss_part_3 = pd.read_csv("ss/trace_y_instance_sockshop_extract_1.csv", header=0, index_col=None)
    ss_part_4 = pd.read_csv("ss/trace_y_sequence_sockshop_extract_1.csv", header=0, index_col=None)
    ss_total = preprocessing_set.append_data(ss_part_1, ss_part_2)
    ss_total = preprocessing_set.append_data(ss_total, ss_part_3)
    ss_total = preprocessing_set.append_data(ss_total, ss_part_4)

    ss_total["y_issue_ms"].fillna("Success", inplace=True)
    ss_total["y_issue_dim_type"].fillna("Success", inplace=True)

    ss_total = ss_total.loc[ss_total["y_issue_dim_type"] != "Success"]

    ss_total = shuffle(ss_total)
    print("总数据量:", len(ss_total))
    ss_total.to_csv("ss_tpds_total.csv")


def split_test_train_sockshop():
    ss_tpds_total = pd.read_csv("ss_tpds_total.csv", header=0, index_col=0)
    print("总数据量:", len(ss_tpds_total))
    ss_tpds_test, ss_tpds_train = preprocessing_set.split_data(ss_tpds_total, 0.1)
    print("训练数据量:", len(ss_tpds_train))
    print("测试数据量:", len(ss_tpds_test))
    ss_tpds_test.to_csv("ss_tpds_test.csv")
    ss_tpds_train.to_csv("ss_tpds_train.csv")


if __name__ == "__main__":
    merge_all_sockshop()
    split_test_train_sockshop()
