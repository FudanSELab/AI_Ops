from math import sqrt
import pandas as pd


def multipl(a,b):
    sumofab=0.0
    for i in range(len(a)):
        temp=a[i]*b[i]
        sumofab+=temp
    return sumofab


def corrcoef(x,y):
    n=len(x)
    #求和
    sum1=sum(x)
    sum2=sum(y)
    #求乘积之和
    sumofxy=multipl(x,y)
    #求平方和
    sumofx2 = sum([pow(i,2) for i in x])
    sumofy2 = sum([pow(j,2) for j in y])
    num=sumofxy-(float(sum1)*float(sum2)/n)
    #计算皮尔逊相关系数
    den=sqrt((sumofx2-float(sum1**2)/n)*(sumofy2-float(sum2**2)/n))
    return num/den

# x = [1,2,1,3,3,5,5,8,8,8,6,2,3,8,2,1,1,1,2]
# y = [0,1,0,1,1,1,1,0,1,1,1,1,1,1,0,0,0,0,0]

# print(corrcoef(x,y))

# 0.8-1.0 极强相关
# 0.6-0.8 强相关
# 0.4-0.6 中等程度相关
# 0.2-0.4 弱相关
# 0.0-0.2 极弱相关或无相关

csv_path = "110\\trace_verified_instance3.csv"

csv_data = pd.read_csv(csv_path, index_col="trace_id", header=0)

x_login_confNum = csv_data['ts_login_service_confnumber']
x_login_readyNum = csv_data['ts_login_service_readynumber']


# x_orderOther_confNum = csv_data['ts_order_other_service_confnumber']
# x_orderOther_readyNum = csv_data['ts_order_other_service_readynumber']
#
# x_travel_confNum = csv_data['ts_travel_service_confnumber']
# x_travel_readyNum = csv_data['ts_travel_service_readynumber']
#
#
# x_travel2_confNum = csv_data['ts_travel2_service_confnumber']
# x_travel2_readyNum = csv_data['ts_travel2_service_readynumber']
#
#
# x_execute_confNum = csv_data['ts_execute_service_confnumber']
# x_execute_readyNum = csv_data['ts_execute_service_readynumber']


y_final_result  = csv_data['y_final_result']


print(corrcoef(x_login_confNum.values.tolist(), y_final_result.values.tolist()))
print(corrcoef(x_login_readyNum.values.tolist(), y_final_result.values.tolist()))

# print(corrcoef(x_orderOther_confNum.values.tolist(), y_final_result.values.tolist()))
# print(corrcoef(x_orderOther_readyNum.values.tolist(), y_final_result.values.tolist()))
#
# print(corrcoef(x_travel_confNum.values.tolist(), y_final_result.values.tolist()))
# print(corrcoef(x_travel_readyNum.values.tolist(), y_final_result.values.tolist()))
#
# print(corrcoef(x_travel2_confNum.values.tolist(), y_final_result.values.tolist()))
# print(corrcoef(x_travel2_readyNum.values.tolist(), y_final_result.values.tolist()))



# print(corrcoef(x_execute_confNum.values.tolist(), y_final_result.values.tolist()))
# print(corrcoef(x_execute_readyNum.values.tolist(), y_final_result.values.tolist()))