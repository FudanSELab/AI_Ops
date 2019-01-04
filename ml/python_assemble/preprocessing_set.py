import pandas as pd
from pandas import DataFrame
from sklearn.preprocessing import MinMaxScaler


def merge_data(df_trace: DataFrame, df_seq: DataFrame, df_seq_caller: DataFrame):
    df_merged_seq = df_seq.join(df_seq_caller, how="inner")
    print("Seq-Caller Merged")
    df_trace_seq_joined = df_trace.join(df_merged_seq, how="left")
    print("Trace-Seq Merged")
    return df_trace_seq_joined


def select_data(df_raw: DataFrame):
    for col in df_raw.keys():
        if not(col.endswith(".trace_service")
                or col.endswith(".trace_api")
                or col.endswith("_readynumber")
                or col.endswith("_seq")
                or col.endswith("_caller")
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
    # df_ms_raw = df_raw.loc[df_raw["trace_verified_instance.y_issue_ms"] != "Success"]\


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
        elif col.endswith("trace_verified_instance.trace_service") \
                or col.endswith("trace_verified_instance.trace_api") \
                or col.endswith("_caller"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
            # df_raw = pd.get_dummies(df_raw, columns=[col])
    return df_raw

    # TODO: DROP SOME USELESS COLUMNS AFTER EXTRACTION
    # return df_raw


def dimensionless(df_raw: DataFrame):
    scaler = MinMaxScaler()
    # TODO: NOT ALL COLUMNS NEEDS TO BE DIMENSIONLESS
    df_raw[df_raw.columns] = scaler.fit_transform(df_raw[df_raw.columns])
    return df_raw
