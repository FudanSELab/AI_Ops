



import numpy as np
import pandas as pd
import matplotlib.pyplot as plt


CSV_COLUMN_NAMES = ["service1_inst", "service2_inst", "service3_inst", "service1_mem", "service2_mem", "service3_mem", "time_span", "result"];
print(CSV_COLUMN_NAMES[:-1])
y_name='result'

train_path, test_path = "train.csv", "test.csv";

train = pd.read_csv(train_path, names=CSV_COLUMN_NAMES, header=0)
train_x, train_y = train.loc[:,CSV_COLUMN_NAMES[:-1]], train[y_name]
print(train_x, train_y)



train_x.plot(subplots=True, figsize=(8, 8))

train_x.hist(figsize=(8, 8))

plt.figure();
train_x["service3_inst"].plot.kde()

plt.figure();
train_y.plot.hist();

train.plot.scatter(x='service1_inst', y='result')



from pandas.plotting import scatter_matrix
scatter_matrix(train, alpha=0.2, figsize=(6, 6), diagonal='kde')

plt.show()



