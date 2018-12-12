import pandas as pd


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