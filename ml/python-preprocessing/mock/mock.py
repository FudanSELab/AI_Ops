from pandas import DataFrame
import numpy as np
import pandas as pd

# Key Service: S0

train_columns = []
train_data = []
for i in range(41):
    temp_col_data = []
    for j in range(500):
        temp_col_data.append(np.random.randint(0, 4))
    train_data.append(temp_col_data)
    train_columns.append("svc" + str(i) + "_num")

temp_col_data1 = []
temp_col_data2 = []
for j in range(500):
    svc_ran_num = np.random.randint(0, 40)
    temp_col_data1.append(svc_ran_num)
    if train_data[svc_ran_num][j] >= 2:
        temp_col_data2.append(1)
    else:
        temp_col_data2.append(0)

train_data.append(temp_col_data1)
train_data.append(temp_col_data2)
train_columns.append("svc")
train_columns.append("result")


dateset = []

for i in range(500):
    temp = []
    for j in range(43):
        temp.append(train_data[j][i])
    print(temp)
    dateset.append(temp)

train_data_final = DataFrame(dateset, columns=train_columns)

train_data_final.to_csv("mock.csv")

