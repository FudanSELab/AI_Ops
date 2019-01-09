from imblearn.over_sampling import RandomOverSampler
from pandas import DataFrame
from sklearn.decomposition import PCA
from sklearn.preprocessing import MinMaxScaler
from sklearn.utils import shuffle
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2
import data_convert_set


def merge_data(df_trace: DataFrame, df_seq: DataFrame, df_seq_caller: DataFrame):
    df_merged_seq = df_seq.join(df_seq_caller, how="inner")
    print("Seq-Caller Merged")
    df_trace_seq_joined = df_trace.join(df_merged_seq, how="left")
    print("Trace-Seq Merged")
    return df_trace_seq_joined


def select_data(df_raw: DataFrame):
    for col in df_raw.keys():
        if not(#col.endswith(".trace_service")
                col.endswith(".trace_api")
                # col.endswith("_readynumber")
                # col.endswith("_diff")
                # col.endswith("_variable")
                # col.endswith("_included")
                # col.endswith("_seq")
                # col.endswith("_caller")
                or col.endswith(".y_issue_ms")
                or col.endswith(".y_final_result")
                or col.endswith(".y_issue_dim_type")):
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
        if col.endswith("_diff"):
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
        if col.endswith("trace_verified_instance.y_issue_ms") \
                or col.endswith("trace_verified_instance.y_issue_dim_type"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
        elif col.endswith(".trace_service") \
                or col.endswith(".trace_api") \
                or col.endswith("_caller"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
            # df_raw = pd.get_dummies(df_raw, columns=[col])
        elif col.endswith("_mem_diff"):
            df_raw[col] = df_raw[[col]].applymap(data_convert_set.transform_memory_diff)
        elif col.endswith("_cpu_diff"):
            df_raw[col] = df_raw[[col]].applymap(data_convert_set.transform_cpu_diff)

    return df_raw

    # TODO: DROP SOME USELESS COLUMNS AFTER EXTRACTION
    # return df_raw


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
