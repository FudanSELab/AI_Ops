from pandas import DataFrame
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
import preprocessing_set
from sklearn.model_selection import KFold


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


def calculate_precise(test_y, result):
    total_count = len(test_y)
    count = 0
    for i in range(total_count):
        result_temp, _ = compare_multi_label(result[i], test_y[i])
        if result_temp:
            count = count + 1
    return count / total_count


# 给定参数的多标签MLP
def mlp_multi_label_provided_train_test_given_params(df_train: DataFrame,
                                                     df_test: DataFrame,
                                                     y_name,
                                                     hidden_layer_sizes,
                                                     max_iter):
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(df_train, y_name)
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(df_test, y_name)
    clf = MLPClassifier(hidden_layer_sizes=hidden_layer_sizes,
                        max_iter=max_iter)
    clf.fit(X=train_x, y=train_y)
    result = clf.predict(test_x)
    precise = calculate_precise(test_y, result)
    return precise


# 给定参数KNN
def knn_multi_label_provided_train_test_given_params(df_train: DataFrame,
                                                     df_test: DataFrame,
                                                     y_name,
                                                     n_neighbors):
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(df_train, y_name)
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(df_test, y_name)
    clf = KNeighborsClassifier(n_neighbors=n_neighbors)
    clf.fit(X=train_x, y=train_y)
    result = clf.predict(test_x)
    precise = calculate_precise(test_y, result)
    return precise


# 给定参数的RF
def rf_multi_label_provided_train_test_given_params(df_train: DataFrame,
                                                    df_test: DataFrame,
                                                    y_name,
                                                    n_estimators,
                                                    min_samples_leaf):
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(df_train, y_name)
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(df_test, y_name)
    clf = RandomForestClassifier(min_samples_leaf=min_samples_leaf,
                                 n_estimators=n_estimators)
    clf.fit(X=train_x, y=train_y)
    result = clf.predict(test_x)
    precise = calculate_precise(test_y, result)
    return precise




# 给定参数和总数据集，计算交叉验证准确率
def cross_validation_rf(df: DataFrame,
                        y_name,
                        n_estimators,
                        min_samples_leaf,
                        n_splits):
    fds = KFold(n_splits=n_splits, shuffle=True)
    precise = 0.0
    for train_raw_indices, test_raw_indices in fds.split(df):
        train_raw = df.iloc[train_raw_indices]
        test_raw = df.iloc[test_raw_indices]
        train = train_raw
        train = preprocessing_set.sampling(train_raw, y_name)
        test = test_raw
        test = preprocessing_set.sampling(test_raw, y_name)
        temp_precise = rf_multi_label_provided_train_test_given_params(df_train=train,
                                                                       df_test=test,
                                                                       y_name=y_name,
                                                                       n_estimators=n_estimators,
                                                                       min_samples_leaf=min_samples_leaf)
        print(temp_precise)
        precise = precise + temp_precise
    return precise / n_splits


# 给定参数和总数据集，计算交叉验证准确率
def cross_validation_knn(df: DataFrame,
                         y_name,
                         n_neighbors,
                         n_splits):
    fds = KFold(n_splits=n_splits, shuffle=True)
    precise = 0.0
    for train_raw_indices, test_raw_indices in fds.split(df):
        train_raw = df.iloc[train_raw_indices]
        test_raw = df.iloc[test_raw_indices]
        train = train_raw
        train = preprocessing_set.sampling(train_raw, y_name)
        test = test_raw
        test = preprocessing_set.sampling(test_raw, y_name)
        temp_precise = knn_multi_label_provided_train_test_given_params(df_train=train,
                                                                        df_test=test,
                                                                        y_name=y_name,
                                                                        n_neighbors=n_neighbors)
        print(temp_precise)
        precise = precise + temp_precise
    return precise / n_splits


# 给定参数和总数据集，计算交叉验证准确率
def cross_validation_mlp(df: DataFrame,
                         y_name,
                         hidden_layer_sizes,
                         max_iter,
                         n_splits):
    fds = KFold(n_splits=n_splits, shuffle=True)
    precise = 0.0
    for train_raw_indices, test_raw_indices in fds.split(df):
        train_raw = df.iloc[train_raw_indices]
        test_raw = df.iloc[test_raw_indices]
        train = train_raw
        # train = preprocessing_set.sampling(train_raw, y_name)
        test = test_raw
        temp_precise = mlp_multi_label_provided_train_test_given_params(df_train=train,
                                                                        df_test=test,
                                                                        y_name=y_name,
                                                                        hidden_layer_sizes=hidden_layer_sizes,
                                                                        max_iter=max_iter)
        print(temp_precise)
        precise = precise + temp_precise
    return precise / n_splits


def grid_search_knn(df: DataFrame, y_name, n_neighbors_list):
    n_neighbors_list_len = len(n_neighbors_list)
    max_precise = 0.0
    max_precise_n_neighbors = -1
    for i in range(n_neighbors_list_len):
        temp_n_neighbors = n_neighbors_list[i]
        temp_precise = cross_validation_knn(df, y_name, temp_n_neighbors, 5)
        print("Grid Search Temp Precise:", temp_precise)
        print("N Neighbors:", temp_n_neighbors)
        print("==========================")
        if temp_precise > max_precise:
            max_precise = temp_precise
            max_precise_n_neighbors = temp_n_neighbors
    print("Max Precise:", max_precise)
    print("Max Precise N Neighbors:", max_precise_n_neighbors)
    return max_precise, max_precise_n_neighbors


def grid_search_rf(df: DataFrame, y_name, n_estimators_list, min_samples_leaf_list):
    n_estimators_list_len = len(n_estimators_list)
    min_samples_leaf_list_len = len(min_samples_leaf_list)
    max_precise = 0.0
    max_precise_n_estimators = -1
    max_precise_min_samples_leaf = -1
    for i in range(n_estimators_list_len):
        for j in range(min_samples_leaf_list_len):
            temp_n_estimators = n_estimators_list[i]
            temp_min_samples_leaf = min_samples_leaf_list[j]
            temp_precise = cross_validation_rf(df, y_name, temp_n_estimators, temp_min_samples_leaf, 5)
            print("Grid Search Temp Precise:", temp_precise)
            print("Min Samples Leaf:", temp_min_samples_leaf)
            print("N Estimators:", temp_n_estimators)
            print("==========================")
            if temp_precise > max_precise:
                max_precise = temp_precise
                max_precise_n_estimators = temp_n_estimators
                max_precise_min_samples_leaf = temp_min_samples_leaf
    print("Max Precise:", max_precise)
    print("Max Precise N Estimators:", max_precise_n_estimators)
    print("Max Precise N Samples Leaf:", max_precise_min_samples_leaf)
    return max_precise, max_precise_n_estimators, max_precise_min_samples_leaf


def grid_search_mlp(df: DataFrame, y_name, hidden_layer_sizes_list, max_iter_list):
    hidden_layer_sizes_list_len = len(hidden_layer_sizes_list)
    max_iter_list_len = len(max_iter_list)
    max_precise = 0.0
    max_precise_hidden_layer_sizes = -1
    max_precise_max_iter = -1
    for i in range(hidden_layer_sizes_list_len):
        for j in range(max_iter_list_len):
            temp_hidden_layer_sizes = hidden_layer_sizes_list[i]
            temp_max_iter = max_iter_list[j]
            temp_precise = cross_validation_mlp(df, y_name, temp_hidden_layer_sizes, temp_max_iter, 5)
            print("Grid Search Temp Precise:", temp_precise)
            print("Hidden Layer Size:", temp_hidden_layer_sizes)
            print("Max_Iter:", temp_max_iter)
            print("==========================")
            if temp_precise > max_precise:
                max_precise = temp_precise
                max_precise_hidden_layer_sizes = temp_hidden_layer_sizes
                max_precise_max_iter = temp_max_iter
    print("Max Precise:", max_precise)
    print("Max Precise Hidden Layer:", max_precise_hidden_layer_sizes)
    print("Max Precise Max Iter:", max_precise_max_iter)
    return max_precise, max_precise_hidden_layer_sizes, max_precise_max_iter


# 计算整体准确度：
#     拆分训练集与测试集
#     准备Grid Search参数
#     进行Grid Search训练
#         对于每组参数的训练，进行交叉验证
#             在交叉验证的时候的时候，对传入的训练集再次进行拆分
#     得到GridSearch的参数
#     使用训练集训练模型和给定参数
#     计算测试准确度
def knn_total(df: DataFrame, y_name, test_ratio, n_neighbors_list):
    testing_set = df.sample(frac=test_ratio)
    training_set = df.drop(testing_set.index)

    max_precise, max_precise_n_neighbors = grid_search_knn(training_set, y_name, n_neighbors_list)
    print("==========================")
    print("KNN", y_name, "N-Neighbors参数:", n_neighbors_list)
    print("GridSearch最大准确率:", max_precise)
    print("GridSearch最大准确率对应的N-Neighbors参数:", max_precise_n_neighbors)

    total_precise = knn_multi_label_provided_train_test_given_params(
        df_train=training_set,
        df_test=testing_set,
        y_name=y_name,
        n_neighbors=max_precise_n_neighbors)
    print("使用GirdSearch得到的最佳参数后,测试集准确度:", total_precise)


def rf_total(df: DataFrame, y_name, test_ratio, n_estimators_list, min_samples_leaf_list):
    testing_set = df.sample(frac=test_ratio)
    training_set = df.drop(testing_set.index)

    max_precise, max_precise_n_estimators, max_precise_min_samples_leaf = \
        grid_search_rf(training_set, y_name, n_estimators_list, min_samples_leaf_list)
    print("==========================")
    print("KNN", y_name, "N-Estimators参数:", n_estimators_list, "Min-Samples-Leaf参数:", min_samples_leaf_list)
    print("GridSearch最大准确率:", max_precise)
    print("GridSearch最大准确率对应的N-Estimators参数:", max_precise_n_estimators)
    print("GridSearch最大准确率对应的N-Min-Samples-Leaf参数:", max_precise_min_samples_leaf)

    total_precise = rf_multi_label_provided_train_test_given_params(
        df_train=training_set,
        df_test=testing_set,
        y_name=y_name,
        n_estimators=max_precise_n_estimators,
        min_samples_leaf=max_precise_min_samples_leaf)
    print("使用GirdSearch得到的最佳参数后,测试集准确度:", total_precise)


def mlp_total(df: DataFrame, y_name, test_ratio, hidden_layer_sizes_list, max_iter_list):
    testing_set = df.sample(frac=test_ratio)
    training_set = df.drop(testing_set.index)

    max_precise, max_precise_hidden_layer_sizes, max_precise_max_iter = \
        grid_search_mlp(training_set, y_name, hidden_layer_sizes_list, max_iter_list)
    print("==========================")
    print("KNN", y_name, "Hidden-Layer-Size参数:", hidden_layer_sizes_list, "Max-Iter参数:", max_iter_list)
    print("GridSearch最大准确率:", max_precise)
    print("GridSearch最大准确率对应的Hidden-Layer-Size参数:", max_precise_hidden_layer_sizes)
    print("GridSearch最大准确率对应的Max-Iter参数:", max_precise_max_iter)

    total_precise = mlp_multi_label_provided_train_test_given_params(
        df_train=training_set,
        df_test=testing_set,
        y_name=y_name,
        hidden_layer_sizes=max_precise_hidden_layer_sizes,
        max_iter=max_precise_max_iter
    )
    print("使用GirdSearch得到的最佳参数后,测试集准确度:", total_precise)