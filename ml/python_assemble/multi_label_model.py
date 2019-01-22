from pandas import DataFrame
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
import preprocessing_set


def compare_multi_label(x, y):
    result_setted = False
    result = False
    targeted = False
    if len(x) != len(y):
        return False, targeted
    for i in range(len(x)):
        if x[i] == y[i] == 1:
            targeted = True
        if x[i] != y[i] and result_setted is False:
            result_setted = True
            result = False
    if result_setted is False:
        result = True
    return result, targeted


# 输出结果。train_len为训练集大小,test_y为原始结果,result为预测结果,proba为置信度,log-file-name为日志名称
def print_result(train_len, test_y, result, proba, log_file_name):
    f = open(log_file_name, 'w+')
    count = 0
    targeted_count = 0
    for i in range(len(result)):
        print("=====", file=f)
        print("Result:", result[i], file=f)
        print("Origin:", test_y[i], file=f)
        print("Proba:", end='', file=f)
        for j in range(3):
            print(str(j), proba[j][i], end=' ', file=f)
        print("", file=f)
        result_temp, targeted = compare_multi_label(result[i], test_y[i])
        if result_temp:
            count = count + 1
        if targeted:
            targeted_count += 1
    print("Training Dataset:", train_len)
    print("Testing Dataset:", test_y.__len__())
    print("Predict:", len(result), " Success:", count)
    print("Targeted:", targeted_count)


# 多标签预测随机森林。使用既有的训练集和测试集合，并提供Label的属性名(列名)
def rf_multi_label_provided_train_test(df_train: DataFrame, df_test: DataFrame, y_name):
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(df_train, y_name)
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(df_test, y_name)
    clf = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    clf.fit(X=train_x, y=train_y)
    result = clf.predict(test_x)
    proba = clf.predict_proba(test_x)
    print_result(train_y.__len__(), test_y, result, proba, "log/rf-multi-label.txt")


# 多标签预测梯度上升分类器。使用既有的训练集和测试集合，并提供Label的属性名(列名)
def mlp_multi_label_provided_train_test(df_train: DataFrame, df_test: DataFrame, y_name):
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(df_train, y_name)
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(df_test, y_name)
    clf = MLPClassifier()
    clf.fit(X=train_x, y=train_y)
    result = clf.predict(test_x)
    proba = clf.predict_proba(test_x)
    print_result(train_y.__len__(), test_y, result, proba, "log/mlp-multi-label.txt")


# 多标签预测K近邻。使用既有的训练集和测试集合，并提供Label的属性名(列名)
def knn_multi_label_provided_train_test(df_train: DataFrame, df_test: DataFrame, y_name):
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(df_train, y_name)
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(df_test, y_name)
    clf = KNeighborsClassifier(n_neighbors=100)
    clf.fit(X=train_x, y=train_y)
    result = clf.predict(test_x)
    proba = clf.predict_proba(test_x)
    print_result(train_y.__len__(), test_y, result, proba, "log/knn-multi-label.txt")