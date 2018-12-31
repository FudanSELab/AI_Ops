from pandas import DataFrame
import numpy as np
import random


# 生成数据表的表头
# 这里生成的表头包括:
#   入口服务entry_svc
#   每个服务的实例svci
#   服务调用顺序svci_svcj与其svci_svcj_caller
#   最终结果result
#   最终故障服务ms
def get_col(svc_num):
    col_num = ["entry_svc"]
    for i in range(0, svc_num):
        col_num.append("svc" + str(i))
    for i in range(0, svc_num):
        for j in range(i+1, svc_num):
            col_num.append("svc" + str(i) + "_" + "svc" + str(j))
            col_num.append("svc" + str(i) + "_" + "svc" + str(j) + "_caller")
            col_num.append("svc" + str(j) + "_" + "svc" + str(i))
            col_num.append("svc" + str(j) + "_" + "svc" + str(i) + "_caller")
    col_num.append("result")
    col_num.append("ms")
    print(col_num)
    return col_num


# 这里Mock size条数据，这些数据根据svc_num个微服务来mock
def mock_data(size, svc_num):
    # 生成表头
    cols = get_col(svc_num)
    # Seq的取值范围 -1 0 1
    rand_seq = [-1, 0, 1]
    # instance number的取值范围0 1 2 3
    rand_inst = [0, 1, 2, 3]
    # mock的数据将会放在这里
    data = []
    # 开始mock size条数据
    for i in range(0, size):
        # mock入口服务 - 随机选一个服务
        temp_row_data = ["svc" + str(np.random.randint(0, svc_num))]
        # mock每个服务的实例数量
        for j in range(0, svc_num):
            temp_row_data.append(random.choice(rand_inst))
        # mock seq及其caller
        for j in range(1+svc_num, cols.__len__() - 2, 2):
            temp_value = random.choice(rand_seq)
            temp_row_data.append(temp_value)
            # 如果mock出的seq是-1，说明这个顺序没出现过，所以caller就是"not exist"
            # 如果不是-1，再Mock一个caller出来
            if temp_value != -1:
                # caller肯定不是seq里两个服务中的某一个，所以要在剩下的几个服务里抽一个作为caller
                new_rand_svc = []
                for k in range(svc_num):
                    new_rand_svc.append("svc" + str(k))
                exist_pairs = cols[j].split(sep="_")
                new_rand_svc.remove(exist_pairs[0])
                new_rand_svc.remove(exist_pairs[1])
                temp_row_data.append(random.choice(new_rand_svc))
            else:
                temp_row_data.append("Not Exist")
        # 先全部把结果设置为 result=1 ms=Success
        temp_row_data.append(1)
        temp_row_data.append("Success")
        data.append(temp_row_data)
    # 把mock好的数据和表头组合起来形成DataFrame
    data_final = DataFrame(data, columns=cols)
    # 接下来按照一定的规则来把一些mock出的数据改成result=0, ms=svci
    # 首先mock实例数量错误
    # 下面这行代码的意思是 如果入口服务是svc0并且svc3的实例数量是3，那么认为这条trace错误，ms为svc3
    data_final.loc[(data_final["entry_svc"] == "svc0") & (data_final["svc3"] == 3), ["result", "ms"]] \
        = {0, "svc3"}
    data_final.loc[(data_final["entry_svc"] == "svc1") & (data_final["svc2"] == 3), ["result", "ms"]] \
        = {0, "svc2"}
    data_final.loc[(data_final["entry_svc"] == "svc2") & (data_final["svc1"] == 3), ["result", "ms"]] \
        = {0, "svc1"}
    # 首先mock调用顺序错误
    # 下面这行代码的意思是 如果svc0_svc2这个顺序出现过而且是逆序(值为1),并且其caller为svc4，那么认为这条trace错误，ms为svc4
    data_final.loc[(data_final["svc0_svc2"] == 1) & (data_final["svc0_svc2_caller"] == "svc4"), ["result", "ms"]] \
        = {0, "svc4"}
    data_final.loc[(data_final["svc1_svc4"] == 1) & (data_final["svc1_svc4_caller"] == "svc3"), ["result", "ms"]] \
        = {0, "svc3"}
    data_final.loc[(data_final["svc3_svc2"] == 1) & (data_final["svc3_svc2_caller"] == "svc0"), ["result", "ms"]] \
        = {0, "svc0"}
    data_final.loc[(data_final["svc3_svc2"] == 1) & (data_final["svc3_svc2_caller"] == "svc1"), ["result", "ms"]] \
        = {0, "svc1"}
    # data_final.loc[(data_final["svc3_svc1"] == 1) & (data_final["svc3_svc1_caller"] == "svc2"), ["result", "ms"]] \
    #     = {0, "svc2"}
    # 接下来mock配置错误
    # 待完成

    # 将结果输出
    data_final.to_csv("mock_whole.csv")


# 产生数据: mock(数据条数，服务数量）
if __name__ == "__main__":
    mock_data(500, 20)
