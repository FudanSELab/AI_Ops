from imblearn.over_sampling import RandomOverSampler
from pandas import DataFrame
from sklearn.decomposition import PCA
from sklearn.preprocessing import MinMaxScaler, MultiLabelBinarizer
from sklearn.utils import shuffle
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.model_selection import KFold


service_index_map = {
    "ts-admin-basic-info-service": 0,
    "ts-admin-order-service": 1,
    "ts-admin-route-service": 2,
    "ts-admin-travel-service": 3,
    "ts-admin-user-service": 4,
    "ts-assurance-service": 5,
    "ts-basic-service": 6,
    "ts-cancel-service": 7,
    "ts-config-service": 8,
    "ts-consign-price-service": 9,
    "ts-consign-service": 10,
    "ts-contacts-service": 11,
    "ts-execute-service": 12,
    "ts-food-map-service": 13,
    "ts-food-service": 14,
    "ts-inside-payment-service": 15,
    "ts-login-service": 16,
    "ts-news-service": 17,
    "ts-notification-service": 18,
    "ts-order-other-service": 19,
    "ts-order-service": 20,
    "ts-payment-service": 21,
    "ts-preserve-other-service": 22,
    "ts-preserve-service": 23,
    "ts-price-service": 24,
    "ts-rebook-service": 25,
    "ts-register-service": 26,
    "ts-route-plan-service": 27,
    "ts-route-service": 28,
    "ts-seat-service": 29,
    "ts-security-service": 30,
    "ts-sso-service": 31,
    "ts-station-service": 32,
    "ts-ticket-office-service": 33,
    "ts-ticketinfo-service": 34,
    "ts-train-service": 35,
    "ts-travel-plan-service": 36,
    "ts-travel-service": 37,
    "ts-travel2-service": 38,
    "ts-ui-dashboard": 39,
    "ts-verification-code-service": 40,
    "ts-voucher-service": 41
}

dim_index_map = {
    "seq": 0,
    "config": 1,
    "instance": 2
}

result_index_map = {
    "0": 0,
    "1": 1,
}


def append_data(df_one: DataFrame, df_two: DataFrame):
    df_total = df_one.append(df_two)
    return df_total


def merge_data(df_trace: DataFrame, df_seq: DataFrame, df_seq_caller: DataFrame):
    df_merged_seq = df_seq.join(df_seq_caller, how="inner")
    print("Seq-Caller Merged")
    df_trace_seq_joined = df_trace.join(df_merged_seq, how="left")
    print("Trace-Seq Merged")
    return df_trace_seq_joined


def select_data(df_raw: DataFrame):
    for col in df_raw.keys():
        if not(col.endswith("trace_service")
               or col.endswith("trace_api")
               or col.endswith("_api")
               or col.endswith("_readynumber")
               or col.endswith("_diff")
               or col.endswith("_variable")
               or col.endswith("_included")
               or col.endswith("_app_thread_count")
               or col.endswith("_shared_variable")
               or col.endswith("_dependent_db")
               or col.endswith("_dependent_cache")
               or col.endswith("_seq")
               or col.endswith("_caller")
               or col.endswith("y_issue_ms")
               or col.endswith("y_final_result")
               or col.endswith("y_issue_dim_type")):
            df_raw.drop(columns=col, axis=1, inplace=True)
            print("Drop:" + col)
    print("Reserved:" + df_raw.keys())
    return df_raw


def drop_na_data(df_raw: DataFrame):
    df_raw.dropna(axis=1, how='all', inplace=True)
    print("After Drop NA:" + df_raw.keys())
    return df_raw


def drop_all_same_data(df_raw: DataFrame):
    df_raw = df_raw.ix[:, (df_raw != df_raw.ix[0]).any()]
    print("After Drop All-Same-Column:" + df_raw.keys())
    return df_raw


def fill_empty_data(df_raw: DataFrame):
    keys = df_raw.keys()
    for col in keys:
        if col.endswith("_diff") \
                or col.endswith("_readynumber")\
                or col.endswith("_app_thread_count"):
            df_raw[col].fillna(0, inplace=True)
            print("Fill Empty: " + col)
        elif col.endswith("_seq"):
            df_raw[col].fillna(-1, inplace=True)
            print("Fill Empty: " + col)
        elif col.endswith("_caller"):
            df_raw[col].fillna("No", inplace=True)
            print("Fill Empty: " + col)
        elif col.endswith("y_issue_ms"):
            df_raw[col].fillna("Success", inplace=True)
            print("Fill Empty: " + col)

    return df_raw


def convert_data(df_raw: DataFrame):
    keys = df_raw.keys()
    for col in keys:
        if col.endswith("y_issue_ms_") \
                or col.endswith("y_issue_dim_type_"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
        elif col.endswith("trace_service") \
                or col.endswith("_api") \
                or col.endswith("_caller"):
            df_raw[col].fillna("No", inplace=True)
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
            # df_raw = pd.get_dummies(df_raw, columns=[col])
        elif col.endswith("_diff") \
                or col.endswith("_cpu") \
                or col.endswith("_memory") \
                or col.endswith("_limit"):
            df_raw[col].fillna(0, inplace=True)
            df_raw[col] = pd.cut(df_raw[col], bins=5, labels=[1, 2, 3, 4, 5])
    return df_raw
    # TODO: DROP SOME USELESS COLUMNS AFTER EXTRACTION
    # return df_raw


# 这个方法将一列y数值自动转换成[0 1 0 0]的形式(不会有额外的标签产生)
def convert_y_multi_label(df_raw: DataFrame, y_name):
    y_list = df_raw[y_name].tolist()
    for i in range(len(y_list)):
        y_list[i] = [y_list[i]]
    y_multilabel = MultiLabelBinarizer().fit_transform(y_list)
    print(y_multilabel)
    df_raw.pop(y_name)
    return df_raw, y_multilabel


# 这个方法将一列y数值手动转换成[0 1 0 0]的形式
def convert_y_multi_label_by_name(df_raw: DataFrame, y_name):
    y_list = df_raw[y_name].tolist()
    y_multi_label = []
    for i in range(len(y_list)):
        y_service_name = y_list[i]
        # TODO: 需要注意，不同类型的列用的index_map不一样
        # y_index = service_index_map.get(y_service_name)
        y_index = dim_index_map.get(y_service_name)
        temp_y_multi_label = np.zeros(3)
        temp_y_multi_label[y_index] = 1
        y_multi_label.append(temp_y_multi_label)
    #     y_list[i] = [y_list[i]]
    # y_multilabel = MultiLabelBinarizer().fit_transform(y_list)

    df_raw.pop(y_name)
    return df_raw, y_multi_label


def dimensionless(df_raw: DataFrame):
    scaler = MinMaxScaler()
    # TODO: NOT ALL COLUMNS NEEDS TO BE DIMENSIONLESS
    df_raw[df_raw.columns] = scaler.fit_transform(df_raw[df_raw.columns])
    return df_raw


def sampling(df_raw: DataFrame, y_name):
    x, y = df_raw, df_raw.pop(y_name)
    x_keys = x.keys()  # Save x-keys.
    x_res, y_res = RandomOverSampler().fit_resample(x, y)
    df_new_x = DataFrame(data=x_res, columns=x_keys)
    df_new_x[y_name] = y_res
    df_new_x = shuffle(df_new_x)
    return df_new_x


def split_data(df_raw: DataFrame, ratio):
    ratio_part = df_raw.sample(frac=ratio)
    rest_part = df_raw.drop(ratio_part.index)
    return ratio_part, rest_part


def data_split_train_test(df: DataFrame, y_name, test_ratio):
    df_X, df_Y = df, df.pop(y_name)
    X_train, X_test, Y_train, Y_test = train_test_split(df_X, df_Y,
                                                        test_size=test_ratio,
                                                        stratify=df_Y)
    X_train_total = X_train
    X_train_total[y_name] = Y_train
    X_test_total = X_test
    X_test_total[y_name] = Y_test
    print("训练集大小：", len(X_train_total))
    print("测试集大小：", len(X_test_total))
    return X_train_total, X_test_total


# 这里需要重新检查
def feature_selection(df_raw: DataFrame, y_name, num_features):
    x, y = df_raw, df_raw.pop(y_name)
    model_cq = SelectKBest(chi2, k=num_features)  # Select k best features
    after_data = model_cq.fit_transform(x.values, y)
    selected_feature_indexs = model_cq.get_support(True)
    selected_column = df_raw.columns[selected_feature_indexs]
    df_new = df_raw[selected_column]
    df_new[y_name] = y
    return df_new


# 需要做归一化处理，在使用本方法之前
def feature_reduction(df_raw: DataFrame, y_name, num_features):
    x, y = df_raw, df_raw.pop(y_name)
    pca = PCA(n_components=num_features, whiten=True)
    pca.fit(x)
    print("PCA Convert Matrix:", pca.components_)
    x_new = pca.transform(x)
    df = DataFrame(x_new)
    df[y_name] = y
    print("Pca Explained Variance Ratio:", pca.explained_variance_ratio_)
    return df
