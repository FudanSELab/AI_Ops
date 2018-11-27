import numpy as np
import pandas as pd

train_columns = []
column_size = 1700

train_datas = []
datas_length = 50000

for i in range(column_size):
    train_columns.append("column_" + str(i))
train_columns.append("y1")

for i in range(column_size):
    data_column = []
    for j in range(datas_length):
        data_column.append(np.random.randint(10, 100))
    train_datas.append(data_column)
y1_column = []
for i in range(datas_length):
    y1_column.append(1)
train_datas.append(y1_column)

# data gen
train_data_final = pd.DataFrame(dict(zip(train_columns, train_datas)))

train_data_final = train_data_final.loc[:, train_columns]

# write csv
train_data_final.to_csv("mock1700.csv")
