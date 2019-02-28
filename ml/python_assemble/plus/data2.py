import pandas as pd
from sklearn.utils import shuffle

if __name__ == "__main__":
    df = pd.read_csv("../sockshop_data/ss_total_mms.csv", header=0, index_col="trace_id")

    df_true = df.loc[df["y_final_result"] == 1]

    df_p1 = df_true.sample(n=32643,replace=True)

    df_false = df.loc[df["y_final_result"] != 1]

    df_p2 = df_true.sample(n=(243606-32643), replace=True)

    print(len(df_p1))

    print(len(df_p2))

    df_total = df_p2.append(df_p1)

    df_total = shuffle(df_total)

    df_total.to_csv("ss.csv")