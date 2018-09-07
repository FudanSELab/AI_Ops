import pandas as pd
import numpy as np

CSV_COLUMN_NAMES = [
    "service1_inst",
    "service2_inst",
    "service3_inst",
    "service1_mem",
    "service2_mem",
    "service3_mem",
    "time_span",
    "result"
]

CSV_COLUNV_TYPE = {
    "service1_inst": np.int32,
    "service2_inst": np.int32,
    "service3_inst": np.int32,
    "service1_mem": np.int32,
    "service2_mem": np.int32,
    "service3_mem": np.int32,
    "time_span": pd.Categorical,
    "result": np.int32


}

train_path = "train_origin.csv"
train = pd.read_csv(train_path,
                    names=CSV_COLUMN_NAMES,
                    header=0)

train["time_span"] = train["time_span"].astype("category")

train["time_span"] = train["time_span"].cat.set_categories([
    "below 1",
    "below 2",
    "below 3",
    "below 6",
    "below 10",
    "above 10"])

# 特征有大小意义的采用映射编码

time_span_mapping = {
           "below 1": 1,
           "below 2": 2,
           "below 3": 3,
           "below 6": 4,
           "below 10": 5,
           "above 10": 6
}

train["time_span"] = train["time_span"].map(time_span_mapping)

print(train)


train = pd.read_csv(train_path,
                    names=CSV_COLUMN_NAMES,
                    header=0)

df = pd.get_dummies(train, prefix=['time_span'])

df.to_csv('dummy_train.csv')

bin = [0, 100, 200, 300, 1500]

train = pd.read_csv(train_path,
                    names=CSV_COLUMN_NAMES,
                    header=0)


train["service1_mem"]=  pd.cut(train["service1_mem"], bin)

print(train["service1_mem"])
