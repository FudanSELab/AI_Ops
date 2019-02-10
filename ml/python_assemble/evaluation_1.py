import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
import preprocessing_set
import calculation


# 下面部分是实验一的第二部分 - 用ts训练，预测ss
def eval_1_part_2_ft_ts_ss():
    df_ts = pd.read_csv("ss_model2_total.csv", header=0, index_col=0)
    df_ss = pd.read_csv("ts_model2_total.csv", header=0, index_col=0)

    df_ts.pop("trace_id")
    df_ts.pop("test_trace_id")
    df_ts.pop("issue_ms")
    df_ts.pop("final_result")  # 训练过程仅仅需要dim_type,不需要这个
    # df_ts = preprocessing_set.sampling(df_ts, "issue_type")
    train_ft = df_ts.pop("issue_type")

    df_ss.pop("trace_id")
    df_ss.pop("test_trace_id")
    df_ss.pop("issue_ms")
    real_le = df_ss.pop("final_result")
    df_ss = preprocessing_set.sampling(df_ss, "issue_type")
    real_ft = df_ss.pop("issue_type")

    # clf = MLPClassifier(hidden_layer_sizes=[3, 3], max_iter=200)
    clf = KNeighborsClassifier(n_neighbors=2300)
    # clf = RandomForestClassifier(min_samples_leaf=6000, n_estimators=1)

    # 训练与预测
    clf.fit(X=df_ts, y=train_ft)
    pred = clf.predict(X=df_ss)
    real_ft_value = real_ft.values

    # 计算FT的P R F1
    ft_result_set = []
    ft_real_set = []
    for i in range(len(pred)):
        print(real_ft_value[i], pred[i])
        if real_ft_value[i] == "Success":
            ft_real_set.append([0, 0, 0, 1])
        elif real_ft_value[i] == "config":
            ft_real_set.append([0, 0, 1, 0])
        elif real_ft_value[i] == "instance":
            ft_real_set.append([0, 1, 0, 0])
        else:
            ft_real_set.append([1, 0, 0, 0])

        if pred[i] == "Success":
            ft_result_set.append([0, 0, 0, 1])
        elif pred[i] == "config":
            ft_result_set.append([0, 0, 1, 0])
        elif pred[i] == "instance":
            ft_result_set.append([0, 1, 0, 0])
        else:
            ft_result_set.append([1, 0, 0, 0])
    calculation.calculate_a_p_r_f(ft_real_set, ft_result_set, 4)

    # 统计Accuracy
    acc_count = 0
    for i in range(len(pred)):
        if real_ft_value[i] == pred[i]:
            acc_count += 1
    print("Accuracy", acc_count/len(pred))


    # 计算LE的P R F1
    # Success为正,其他值为负
    le_result_set = []
    le_real_set = []
    for i in range(len(pred)):
        if real_ft_value[i] == "Success":
            le_real_set.append([1, 0])
        else:
            le_real_set.append([0, 1])
        if pred[i] == "Success":
            le_result_set.append([1, 0])
        else:
            le_result_set.append([0, 1])
    calculation.calculate_a_p_f_single_label(le_real_set, le_result_set)




if __name__ == "__main__":
    eval_1_part_2_ft_ts_ss()

