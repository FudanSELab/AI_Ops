import pandas as pd


# 用于将指示列转换成指示列对应的数据列
def svc2int():
    data_file_path = "../mock/mock.csv"
    df = pd.read_csv(data_file_path,
                     header=0,
                     index_col=0)
    df_values = df.values
    df_keys = df.keys()
    for i in range(500):
        df_values[i][41] = df_values[i][df_values[i][41]]
    df[df_keys] = df_values
    df.to_csv("mock_new.csv")


def svc2onehot():
    data_file_path = "../mock/mock_map_10_st.csv"
    df = pd.read_csv(data_file_path,
                     header=0,
                     index_col=0)

    df = df.dropna(axis=0, how="any")

    df = pd.get_dummies(df, prefix=["svc"], columns=["svc"])
    df.to_csv("mock_new_one_hot_10_st.csv")


if __name__ == "__main__":
    svc2onehot()
