import pandas as pd
from pandas import DataFrame

input_path = "../input/trace_instance_3_verifyd.csv"
index_col_name = "trace_verified.trace_id"
label_col_name = "trace_verified.y_final_result"


def fetch_data():
    df_raw = pd.read_csv(input_path,
                         header=0,
                         index_col=index_col_name)
    df_new = DataFrame()
    for col in df_raw.keys():
        if col.endswith("_readynumber") \
                or col.endswith(".trace_service") \
                or col.endswith(".trace_api") \
                or col.endswith("_diff") \
                or col.endswith(".y_issue_ms") \
                or col.endswith(".y_final_result") \
                or col.endswith(".y_issue_dim_type"):
            df_new[col] = df_raw[col]
            print("Fetch:", col)
    df_new.to_csv("trace_all_all.csv")


def join_data():
    df_feature = pd.read_csv("trace_all_all.csv",
                             header=0,
                             index_col=index_col_name)
    df_seq = pd.read_csv("../input/seq_instance_3_1_newest.csv",
                         header=0,
                         index_col="seq_instance_3_1.trace_id1")
    df_seq.pop("seq_instance_3_1.test_trace_id1")
    df_seq.pop("seq_instance_3_1.test_case_id1")

    df_joined = df_feature.join(df_seq, how="left")

    df_joined.to_csv("trace_seq_all_all.csv")


if __name__ == "__main__":
    fetch_data()
    join_data()
