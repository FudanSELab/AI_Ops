from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import chi2
import load_data


(train_x, train_y), (test_x, test_y) = load_data.load_data()
train_x = train_x.values
train_y = train_y.values


model_cq = SelectKBest(chi2, k=2)  # 选择k个最佳特征
after_data = model_cq.fit_transform(train_x, train_y)  # iris.data是特征数据，iris.target是标签数据，该函数可以选择出k个特征

print(after_data)

print(model_cq.scores_)

print(model_cq.pvalues_)