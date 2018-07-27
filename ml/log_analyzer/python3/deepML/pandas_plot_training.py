

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt


CSV_COLUMN_NAMES = ['SepalLength', 'SepalWidth',
                    'PetalLength', 'PetalWidth', 'Species']
print(CSV_COLUMN_NAMES[:-1])
SPECIES = ['Setosa', 'Versicolor', 'Virginica']
y_name='Species'

train_path, test_path = "./data/iris_training.csv", "./data/iris_test.csv";

train = pd.read_csv(train_path, names=CSV_COLUMN_NAMES, header=0)
train_x, train_y = train.loc[:,CSV_COLUMN_NAMES[:-1]], train[y_name]
print(train_x, train_y)



train_x.plot(subplots=True, figsize=(8, 8))

plt.figure();
train_y.plot.kde()

# print(train_x)
# print("---------------")
# print(dict(train_x))


print(train)
train.plot.scatter(x='SepalLength', y='Species')



plt.show()