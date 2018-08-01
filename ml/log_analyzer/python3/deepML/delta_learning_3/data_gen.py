

import numpy as np
import pandas as pd



train_columns = ["service1_inst", "service2_inst", "service3_inst", "service1_mem", "service2_mem", "service3_mem", "time_span", "result"];



data_size = 300

data1 = pd.Series(np.random.randint(0,10,data_size));
data2 = pd.Series(np.random.randint(0,10,data_size));
data3 = pd.Series(np.random.randint(0,10,data_size));

data4 = pd.Series(np.random.randint(0,600,data_size));
data5 = pd.Series(np.random.randint(0,600,data_size));
data6 = pd.Series(np.random.randint(0,600,data_size));

# time span
time_list = ["below 1","below 2","below 3","below 6","below 10","above 10"];
time_span = [time_list[idx] for idx in np.random.randint(0,6,data_size)]
# print(time_span)
data7 = pd.Series(time_span);
# print(data7)

result = pd.Series(np.random.randint(0,2,data_size));
# print(result)

train_datas = [data1, data2, data3, data4, data5, data6, data7, result];




# print(dict(zip([1,2], [3,4])))
# dates = pd.date_range('1/1/2000', periods=8)
# data = pd.DataFrame(np.random.randn(8, 4), index=dates, columns=train_columns)

# pd.Series(np.random.randint(0,10,6).astype("str"));
# data1 = pd.Series(np.random.randint(0,10,6).astype("str"));
# print(data1.map(lambda x: "service" + x))

# data gen
train_data_final = pd.DataFrame(dict(zip(train_columns, train_datas)));
print(train_data_final)

# train_data_final = train_data_final.index.reset_index(drop=True);
train_data_final = train_data_final.loc[:,train_columns]



# write csv
train_data_final.to_csv("train.csv");







