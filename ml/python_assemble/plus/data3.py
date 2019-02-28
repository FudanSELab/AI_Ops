import pandas as pd

if __name__ == "__main__":
    df = pd.read_csv("ss.csv", header=0, index_col="trace_id")

    df_p1 = df.sample(n=100000, replace=False)
    df.drop(df_p1.index)

    df_p2 = df.sample(n=100000, replace=False)
    df.drop(df_p2.index)

    df_p3 = df.sample(n=43606, replace=False)
    df.drop(df_p3.index)

    df_p1.to_csv("ss_part1.csv")
    df_p2.to_csv("ss_part2.csv")
    df_p3.to_csv("ss_part3.csv")