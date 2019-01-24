# 实验二主要进行的是依据TraceType进行泛化性能的预测
# 将数据平均分成十份，将最后一份作为测试集合。
# 依次增量添加训练集，测试泛化性
import pandas as pd
from pandas import DataFrame
from sklearn.model_selection import KFold
import preprocessing_set
import multi_label_model

data_total_list = [
    "evaluation_2/evaluation_total_part0.csv",
    "evaluation_2/evaluation_total_part1.csv",
    "evaluation_2/evaluation_total_part2.csv",
    "evaluation_2/evaluation_total_part3.csv",
    "evaluation_2/evaluation_total_part4.csv",
    "evaluation_2/evaluation_total_part5.csv",
    "evaluation_2/evaluation_total_part6.csv",
    "evaluation_2/evaluation_total_part7.csv",
    "evaluation_2/evaluation_total_part8.csv",
    "evaluation_2/evaluation_total_part9.csv"
]


data_fault_list = [
    "evaluation_2/evaluation_fault_part0.csv",
    "evaluation_2/evaluation_fault_part1.csv",
    "evaluation_2/evaluation_fault_part2.csv",
    "evaluation_2/evaluation_fault_part3.csv",
    "evaluation_2/evaluation_fault_part4.csv",
    "evaluation_2/evaluation_fault_part5.csv",
    "evaluation_2/evaluation_fault_part6.csv",
    "evaluation_2/evaluation_fault_part7.csv",
    "evaluation_2/evaluation_fault_part8.csv",
    "evaluation_2/evaluation_fault_part9.csv"
]


# 这里只负责数据拆分，需要提前处理好某些属性的删除问题
def split_data_to_10_parts(df: DataFrame, file_list):
    fds = KFold(n_splits=10, shuffle=True)
    part_index = 0
    for train_raw_indices, test_raw_indices in fds.split(df):
        test_raw = df.iloc[test_raw_indices]
        file_name = file_list[part_index]
        test_raw.to_csv(file_name)
        part_index = part_index + 1
        print("完成", file_name, "数量", len(test_raw_indices))
    print("数据拆分完成")


# 在使用前需要注意准备好数据
def calculate_parts(n_parts, file_list, y_name):
    df_train = pd.read_csv(file_list[0], header=0, index_col=0)
    df_test = pd.read_csv(file_list[9], header=0, index_col=0)
    i = 0
    while i < n_parts:
        df_train_new = pd.read_csv(file_list[i], header=0, index_col=0)
        df_train = preprocessing_set.append_data(df_train, df_train_new)
        i += 1
    precise = multi_label_model.rf_multi_label_provided_train_test_given_params(
        df_train=df_train,
        df_test=df_test,
        y_name=y_name,
        n_estimators=3,
        min_samples_leaf=2000
    )
    print("目标类型", y_name, "比例:", str((n_parts+1)/10.0), precise)


if __name__ == "__main__":
    df = pd.read_csv("ready_use_max_final_result.csv", header=0, index_col="trace_id")
    df = df.sample(frac=0.1)
    df.pop("y_issue_dim_type")
    df.pop("y_issue_ms")
    df.pop("trace_api")
    df.pop("trace_service")
    df = preprocessing_set.sampling(df, "y_final_result")
    split_data_to_10_parts(df, data_total_list)
    for i in range(0, 9):
        calculate_parts(i, data_total_list, "y_final_result")
