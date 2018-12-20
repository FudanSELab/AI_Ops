from pandas import DataFrame
import numpy as np

col_map = {0: 6,
           1: 5,
           2: 2,
           3: 7,
           4: 0,
           5: 4,
           6: 8,
           7: 1,
           8: 9,
           9: 3}


# Key Service: S0
def mock(size, svc_num):
    train_columns = []
    train_data = []
    for i in range(svc_num):
        temp_col_data = []
        for j in range(size):
            temp_col_data.append(np.random.randint(1, 3))
        train_data.append(temp_col_data)
        train_columns.append("svc" + str(i) + "_num")
    temp_col_data1 = []
    temp_col_data2 = []
    for j in range(size):
        svc_ran_num = np.random.randint(0, svc_num)
        temp_col_data1.append(svc_ran_num)
        if train_data[col_map[svc_ran_num]][j] == 2:
        # if train_data[svc_ran_num][j] == 2:
            temp_col_data2.append(1)
        else:
            temp_col_data2.append(0)
    train_data.append(temp_col_data1)
    train_data.append(temp_col_data2)
    train_columns.append("svc")
    train_columns.append("result")
    dataset = []
    for i in range(size):
        temp = []
        for j in range(svc_num+2):
            temp.append(train_data[j][i])
        dataset.append(temp)
    print(dataset)
    train_data_final = DataFrame(dataset, columns=train_columns)
    train_data_final.to_csv("mock_map_10.csv")


# Key Service: S0
def mock_svc(size, svc_num):
    train_columns = []
    train_data = []
    for i in range(svc_num):
        temp_col_data = []
        for j in range(size):
            temp_col_data.append(np.random.randint(1, 3))
        train_data.append(temp_col_data)
        train_columns.append("svc" + str(i) + "_num")
    temp_col_data1 = []
    temp_col_data2 = []
    for j in range(size):
        svc_ran_num = np.random.randint(0, svc_num)
        temp_col_data1.append(svc_ran_num)
        if train_data[col_map[svc_ran_num]][j] == 2:
        # if train_data[svc_ran_num][j] == 2:
            temp_col_data2.append(train_columns[col_map[svc_ran_num]])
        else:
            temp_col_data2.append(None)
    train_data.append(temp_col_data1)
    train_data.append(temp_col_data2)
    train_columns.append("svc")
    train_columns.append("result")
    dataset = []
    for i in range(size):
        temp = []
        for j in range(svc_num+2):
            temp.append(train_data[j][i])
        dataset.append(temp)
    print(dataset)
    train_data_final = DataFrame(dataset, columns=train_columns)
    train_data_final.to_csv("mock_map_10_st.csv")


if __name__ == "__main__":
    print()
    mock(2000, 10)
    mock_svc(2000, 10)
