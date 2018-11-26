from sklearn.decomposition import PCA, IncrementalPCA
from sklearn.model_selection import train_test_split
import pandas as pd

CSV_COLUMN_NAMES = ["service1_inst",
                    "service2_inst",
                    "service3_inst",
                    "service1_mem",
                    "service2_mem",
                    "service3_mem",
                    "time_span",
                    "result"]

train = pd.read_csv("train.csv", names=CSV_COLUMN_NAMES, header=0)
# 样本特征X
X = train[["service1_inst",
           "service2_inst",
           "service3_inst",
           "service1_mem",
           "service2_mem",
           "service3_mem",
           "time_span"]]
# 样本输出Y
Y = train[['result']]
# 划分训练集和测试集，将数据集的80%划入训练集，20%划入测试集

train_X, test_X, train_Y, test_Y = train_test_split(X, Y, test_size=0.2, random_state=1)



print("=====")
print("先不降维, 只对数据进行投影, 看看投影后各个维度的方差分布")
pca = PCA(n_components=7)
pca.fit(train_X,train_Y)
print('各维度的方差: ', pca.explained_variance_)
print('各维度的方差值占总方差值的比例: ', pca.explained_variance_ratio_, '\n')

print("=====")
print("降维, 指定降维后的维度数目")
pca = PCA(n_components=3)
pca.fit(train_X,train_Y)
print('各维度的方差: ', pca.explained_variance_)
print('各维度的方差值占总方差值的比例: ', pca.explained_variance_ratio_)
print('降维后的维度数量: ', pca.n_components_, '\n')

print("=====")
print("降维, 指定主成分的方差和所占的最小比例阈值")
pca = PCA(n_components=0.99)
pca.fit(train_X,train_Y)
print('各维度的方差: ', pca.explained_variance_)
print('各维度的方差值占总方差值的比例: ', pca.explained_variance_ratio_)
print('占总方差值90%的维度数量: ', pca.n_components_, '\n')

print("=====")
print("降维, 使用MLE算法计算降维后维度数量")
pca = PCA(n_components='mle')
pca.fit(train_X,train_Y)
print('各维度的方差: ', pca.explained_variance_)
print('各维度的方差值占总方差值的比例: ', pca.explained_variance_ratio_)
print('降维后的维度数量: ', pca.n_components_, '\n')


# print("=====")
# print("IncrementalPCA")
# ipca = IncrementalPCA(n_components=7, batch_size=2)
# ipca.partial_fit(train_X)
# print('各维度的方差: ', ipca.explained_variance_)
# print('各维度的方差值占总方差值的比例: ', ipca.explained_variance_ratio_)
