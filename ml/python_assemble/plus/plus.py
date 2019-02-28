import pandas as pd
from sklearn.ensemble import RandomForestClassifier
import preprocessing_set


def data():
    df = pd.read_csv("../ready_use_max_final_result.csv", header=0, index_col="trace_id")
    # df = pd.read_csv("../sockshop_data/ss_total.csv", header=0, index_col="trace_id")
    df = df.loc[(df["y_final_result"] == 0) | (df["y_final_result"] == 1)]
    df.pop("y_issue_ms")
    df.pop("y_issue_dim_type")
    # df.pop("trace_api")
    # df.pop("trace_service")

    print("属性列:", len(df.keys()))
    print("数据数量:", len(df))

    # mapping = {0: 0, 1: 1, 2: 0, 3: 0}
    # df["y_final_result"] = df["y_final_result"].map(mapping)

    print("故障数据数量:", len(df.loc[df["y_final_result"] == 1]))
    print("非故障数据数量:", len(df.loc[df["y_final_result"] == 0]))

    return df


def split(df: pd.DataFrame):
    train = df.sample(frac=0.8)
    test = df.drop(train.index)
    print("测试集大小:", len(test))
    print("测试集故障数据量", len(test.loc[df["y_final_result"] == 1]))
    print("训练集大小:", len(train))
    print("训练集故障数据量", len(test.loc[df["y_final_result"] == 0]))
    return train, test


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
    print("故障为positive")
    print("Recall", recall, "Precision", precision, "F1", F1)
    print("FPR", fpr)

    TP = 1  # 预测为正，实际为正
    FP = 1  # 预测为正，实际为负
    TN = 1  # 预测为负，实际为负
    FN = 1  # 预测为负，实际为正
    for j in range(len(y_real)):
        if y_real[j] == 0 and y_pred[j] == 0:
            TP += 1
        elif y_real[j] == 1 and y_pred[j] == 0:
            FP += 1
        elif y_real[j] == 1 and y_pred[j] == 1:
            TN += 1
        else:
            FN += 1
    print(TP, FP, TN, FN)
    precision = TP / (TP + FP)
    recall = TP / (TP + FN)
    F1 = (2 * precision * recall) / (precision + recall)
    fpr = FP / (FP + TN)
    print("非故障为positive")
    print("Recall", recall, "Precision", precision, "F1", F1)
    print("FPR", fpr)
    return precision, recall, F1


if __name__ == "__main__":
    df = data()
    train, test = split(df)

    # train = preprocessing_set.sampling(train, "y_final_result")
    x, y = train, train.pop("y_final_result")

    clf = RandomForestClassifier(min_samples_leaf=3000, n_estimators=250)
    clf.fit(x, y)

    # test = preprocessing_set.sampling(test, "y_final_result")
    real_x, real_y = test, test.pop("y_final_result")


    pred_y = clf.predict(real_x)

    calculate(real_y, pred_y)

