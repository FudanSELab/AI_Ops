import pandas as pd
from sklearn.ensemble import RandomForestClassifier

import preprocessing_set
import big_model

def collect_data():

    print("准备读取production数据")
    df_f1 = pd.read_csv("production/f1/trace_verified_sequence_f1_combined.csv", header=0, index_col="trace_id")
    df_f2 = pd.read_csv("production/f2/trace_verified_sequence_f2_combined.csv", header=0, index_col="trace_id")
    df_f3 = pd.read_csv("production/f3/trace_verified_config_f3_combined.csv", header=0, index_col="trace_id")
    df_f4 = pd.read_csv("production/f4/trace_verified_config_f4_combined.csv", header=0, index_col="trace_id")
    df_f5 = pd.read_csv("production/f5/trace_verified_config_f5_combined.csv", header=0, index_col="trace_id")
    df_f7 = pd.read_csv("production/f7/trace_verified_config_f7_combined.csv", header=0, index_col="trace_id")
    df_f8 = pd.read_csv("production/f8/trace_verified_instance_f8_combined.csv", header=0, index_col="trace_id")
    df_f11 = pd.read_csv("production/f11/trace_verified_instance_f11_combined.csv", header=0, index_col="trace_id")
    df_f12 = pd.read_csv("production/f12/trace_verified_instance_f12_combined.csv", header=0, index_col="trace_id")
    df_f13 = pd.read_csv("production/f13/trace_verified_sequence_f13_combined.csv", header=0, index_col="trace_id")

    print("获取production数据的index")
    df_f1_index = df_f1.index
    df_f2_index = df_f2.index
    df_f3_index = df_f3.index
    df_f4_index = df_f4.index
    df_f5_index = df_f5.index
    df_f7_index = df_f7.index
    df_f8_index = df_f8.index
    df_f11_index = df_f11.index
    df_f12_index = df_f12.index
    df_f13_index = df_f13.index

    print("连接Production数据")
    df_f_all = preprocessing_set.append_data(df_f1, df_f2)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f3)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f4)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f5)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f7)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f8)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f11)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f12)
    df_f_all = preprocessing_set.append_data(df_f_all, df_f13)

    df_f_all.pop("test_case_id")
    df_f_all.pop("test_trace_id")

    print("读取训练数据")
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

    df_one_0.pop("test_case_id")
    df_one_0.pop("test_trace_id")
    df_two_0.pop("test_case_id")
    df_two_0.pop("test_trace_id")
    df_three_0.pop("test_case_id")
    df_three_0.pop("test_trace_id")

    df_total_0 = preprocessing_set.append_data(df_one_0, df_two_0)
    df_total_0 = preprocessing_set.append_data(df_total_0, df_three_0)
    # Read SEQUENCE-SEQ
    df_one_1 = pd.read_csv(trace_csv_one[1], header=0, index_col=index_col)
    df_two_1 = pd.read_csv(trace_csv_two[1], header=0, index_col=index_col)
    df_three_1 = pd.read_csv(trace_csv_three[1], header=0, index_col=index_col)

    df_one_1.pop("test_case_id")
    df_one_1.pop("test_trace_id")
    df_two_1.pop("test_case_id")
    df_two_1.pop("test_trace_id")
    df_three_1.pop("test_case_id")
    df_three_1.pop("test_trace_id")

    df_total_1 = preprocessing_set.append_data(df_one_1, df_two_1)
    df_total_1 = preprocessing_set.append_data(df_total_1, df_three_1)
    # Read SEQUENCE - CALLER
    df_one_2 = pd.read_csv(trace_csv_one[2], header=0, index_col=index_col)
    df_two_2 = pd.read_csv(trace_csv_two[2], header=0, index_col=index_col)
    df_three_2 = pd.read_csv(trace_csv_three[2], header=0, index_col=index_col)

    # df_one_2.pop("test_case_id")
    # df_one_2.pop("test_trace_id")
    # df_two_2.pop("test_case_id")
    # df_two_2.pop("test_trace_id")
    # df_three_2.pop("test_case_id")
    # df_three_2.pop("test_trace_id")

    df_total_2 = preprocessing_set.append_data(df_one_2, df_two_2)
    df_total_2 = preprocessing_set.append_data(df_total_2, df_three_2)
    # 将各个部分的数据JOIN起来
    df_total = preprocessing_set.merge_data(df_trace=df_total_0,
                                            df_seq=df_total_1,
                                            df_seq_caller=df_total_2)

    print("连接训练数据与Production数据")
    df_total_with_f = preprocessing_set.append_data(df_f_all, df_total)

    df_total_with_f.to_csv("production/df_total_with_f.csv")




def collect_data_2():

    df_total_with_f = pd.read_csv("production/df_total_with_f.csv", header=0, index_col="trace_id")
    # 丢弃全列为NA的数据
    print("丢弃NA")
    df_total_with_f = preprocessing_set.drop_na_data(df_total_with_f)

    # 丢弃全列值相同的数据
    print("丢弃All Same")
    df_total_with_f = preprocessing_set.drop_all_same_data(df_total_with_f)

    # 丢弃不需要的列
    print("选择需要的列")
    df_total_with_f = preprocessing_set.select_data(df_total_with_f)

    # 填补空缺值
    print("填补空缺值")
    df_total_with_f = preprocessing_set.fill_empty_data(df_total_with_f)

    # 把不规则的值转换成数字
    print("数据转换")
    df_total_with_f = preprocessing_set.convert_data(df_total_with_f)

    df_total_with_f.to_csv("production/df_total_with_f_min.csv")


production_paths = [
    "production/f1/trace_verified_sequence_f1_combined.csv",
    "production/f2/trace_verified_sequence_f2_combined.csv",
    "production/f3/trace_verified_config_f3_combined.csv",
    "production/f4/trace_verified_config_f4_combined.csv",
    "production/f5/trace_verified_config_f5_combined.csv",
    "production/f7/trace_verified_config_f7_combined.csv",
    "production/f8/trace_verified_instance_f8_combined.csv",
    "production/f11/trace_verified_instance_f11_combined.csv",
    "production/f12/trace_verified_instance_f12_combined.csv",
    "production/f13/trace_verified_sequence_f13_combined.csv",
]

production_min_paths = [
    "production/f1/f1.csv",
    "production/f2/f2.csv",
    "production/f3/f3.csv",
    "production/f4/f4.csv",
    "production/f5/f5.csv",
    "production/f7/f7.csv",
    "production/f8/f8.csv",
    "production/f11/f11.csv",
    "production/f12/f12.csv",
    "production/f13/f13.csv",
]


def extract():
    df_total_with_f = pd.read_csv("production/df_total_with_f_min.csv", header=0, index_col="trace_id")

    for i in range(production_paths.__len__()):
        print("第", i, "份输出")
        file_path = production_paths[i]
        temp_f = pd.read_csv(file_path, header=0, index_col="trace_id")
        indexs = temp_f.index
        df_total_with_f.drop(index=indexs, inplace=True)

    df_total_with_f.to_csv("production/train_total.csv")

    df_total_with_f = df_total_with_f.loc[df_total_with_f["y_final_result"] == 1]

    df_total_with_f.to_csv("production/train_fault.csv")


def evaluation_3():
    # extract()

    file_list = [
        "production/f1/f1.csv",
        "production/f2/f2.csv",
        "production/f3/f3.csv",
        "production/f4/f4.csv",
        "production/f5/f5.csv",
        "production/f7/f7.csv",
        "production/f8/f8.csv",
        "production/f11/f11.csv",
        "production/f12/f12.csv",
        "production/f13/f13.csv"
    ]

    df_train = pd.read_csv("production/train_total.csv", header=0, index_col="trace_id")
    df_train = df_train.loc[(df_train["y_final_result"] == 0) | (df_train["y_final_result"] == 1)]
    df_train.pop("y_issue_ms")
    df_train.pop("y_issue_dim_type")


    print("train")
    df_train = preprocessing_set.sampling(df_train, "y_final_result")
    x, y = df_train, df_train.pop("y_final_result")

    clf = RandomForestClassifier(min_samples_leaf=6000, n_estimators=3)
    clf.fit(x, y)

    for i in range(len(file_list)):
        print("F", i)
        file_name = file_list[i]
        df_test = pd.read_csv(file_name, header=0, index_col="trace_id")
        df_test = df_test.loc[(df_test["y_final_result"] == 0) | (df_test["y_final_result"] == 1)]

        df_test.pop("y_issue_ms")
        df_test.pop("y_issue_dim_type")


        print("predict")
        df_test = preprocessing_set.sampling(df_test, "y_final_result")

        real_x, real_y = df_test, df_test.pop("y_final_result")

        pred_y = clf.predict(real_x)

        calculate(real_y, pred_y)

        # count_f = len(df[df["y_final_result"] == 1])
        # count_t = len(df)
        # print("F",i, (count_f/count_t))
        # print("=====", i, "=====")
        # big_model.big_model(
        #     tf_file_path="production/train_total.csv",
        #     fault_file_path="production/train_fault.csv",
        #     model_2_file_path="ts_model2_total.csv",
        #     # test_trace_file_path="production/f1/f1.csv",
        #     test_trace_file_path=file_name,
        #     test_spans_file_path="ts_model2_total.csv",
        #     ml_name="rf")

def calculate(y_real, y_pred):

    TP = 1  # 预测为正，实际为正
    FP = 1  # 预测为正，实际为负
    TN = 1  # 预测为负，实际为负
    FN = 1  # 预测为负，实际为正
    for j in range(len(y_real)):
        if y_real[j] == 1 and y_pred[j] == 1:
            TP += 1
        elif y_real[j] == 0 and y_pred[j] == 1:
            FP += 1
        elif y_real[j] == 0 and y_pred[j] == 0:
            TN += 1
        else:
            FN += 1
    print(TP, FP, TN, FN)
    precision = TP / (TP + FP)
    recall = TP / (TP + FN)
    fpr = FP / (FP + TN)
    F1 = (2 * precision * recall) / (precision + recall)
    print("Recall", recall, "Precision", precision, "F1", F1, "FPR", fpr)
    return precision, recall, F1




if __name__ == "__main__":
    evaluation_3()
