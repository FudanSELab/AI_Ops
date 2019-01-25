import pandas as pd
import preprocessing_set


if __name__ == "__main__":
    df_verified_1 = pd.read_csv("verified_1.csv", header=0, index_col="trace_id")
    df_verified_2 = pd.read_csv("verified_2.csv", header=0, index_col="trace_id")

    df_verified = preprocessing_set.append_data(df_verified_1,df_verified_2)

    df_seq = pd.read_csv("seq.csv", header=0, index_col="trace_id")

    # 万一属性冲突
    df_seq.pop("test_trace_id")

    df_caller = pd.read_csv("caller.csv", header=0, index_col="trace_id")

    df_1 = preprocessing_set.merge_data(df_trace=df_verified, df_seq=df_seq, df_seq_caller=df_caller)

    df_1.to_csv("result.csv")