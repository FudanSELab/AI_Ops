from pandas import DataFrame
import numpy as np
import random


# 生成数据表的表头
def get_col(svc_num):
    col_num = []
    for i in range(0,svc_num):
        for j in range(i+1, svc_num):
            col_num.append("svc" + str(i) + "_" + "svc" + str(j))
            col_num.append("svc" + str(i) + "_" + "svc" + str(j) + "_caller")
            col_num.append("svc" + str(j) + "_" + "svc" + str(i))
            col_num.append("svc" + str(j) + "_" + "svc" + str(i) + "_caller")
    col_num.append("result")
    col_num.append("ms")
    print(col_num)
    return col_num



def mock(size, svc_num):
    cols = get_col(svc_num)
    rand_seq = [-1, 0, 1]

    data = []
    for i in range(0, size):
        temp_row_data = []
        for j in range(0, cols.__len__() - 2, 2):
            temp_value = random.choice(rand_seq)
            temp_row_data.append(temp_value)
            if temp_value != -1:
                new_rand_svc = []
                for k in range(svc_num):
                    new_rand_svc.append("svc" + str(k))
                exist_pairs = cols[j].split(sep="_")
                new_rand_svc.remove(exist_pairs[0])
                new_rand_svc.remove(exist_pairs[1])
                temp_row_data.append(random.choice(new_rand_svc))
            else:
                temp_row_data.append("No")
        temp_row_data.append(1)
        temp_row_data.append("Success")
        data.append(temp_row_data)
    data_final = DataFrame(data, columns=cols)

    data_final.loc[(data_final["svc0_svc2"] == 1) & (data_final["svc0_svc2_caller"] == "svc4"), ["result", "ms"]] \
        = {0, "svc4"}
    data_final.loc[(data_final["svc1_svc4"] == 1) & (data_final["svc1_svc4_caller"] == "svc3"), ["result", "ms"]] \
        = {0, "svc3"}
    data_final.loc[(data_final["svc3_svc2"] == 1) & (data_final["svc3_svc2_caller"] == "svc0"), ["result", "ms"]] \
        = {0, "svc0"}
    data_final.loc[(data_final["svc3_svc2"] == 1) & (data_final["svc3_svc2_caller"] == "svc1"), ["result", "ms"]] \
        = {0, "svc1"}
    data_final.loc[(data_final["svc3_svc1"] == 1) & (data_final["svc3_svc1_caller"] == "svc2"), ["result", "ms"]] \
        = {0, "svc2"}

    data_final.to_csv("mock_seq_5.csv")
    return 0


if __name__ == "__main__":
    mock(500, 5)

