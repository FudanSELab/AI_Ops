from pandas import DataFrame
from sklearn.ensemble import RandomForestClassifier, ExtraTreesClassifier, GradientBoostingClassifier, VotingClassifier
from sklearn.multiclass import OneVsRestClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.neural_network import MLPClassifier
from sklearn.svm import SVC
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV
import model_persistence
import preprocessing_set

f = open("log.txt", 'w+')


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


# 决策树和GridSearch
def dt(df: DataFrame, y_name):
    x, y = df, df.pop(y_name)
    x_val = x.values
    y_val = y.values
    for key in x.keys():
        print("Feature name in X:", key)
    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None, 10, 30, 100],
    }
    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test


# 多标签预测决策树和GridSearch
def dt_multi_label(df: DataFrame, y_multi_label):
    x_val = df.values
    y_val = y_multi_label
    for key in df.keys():
        print("Feature name in X:", key)
    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None, 10, 30, 100],
    }
    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test


# 决策树
def dt_single(df: DataFrame, y_name):
    train = df.sample(frac=0.8)
    test = df.drop(train.index)
    train_x, train_y = train, train.pop(y_name)
    test_x, test_y = test, test.pop(y_name)
    clf2 = DecisionTreeClassifier()
    clf2.fit(X=train_x, y=train_y)

    model_persistence.model_save(clf2, "model/dt.m")
    clf2 = model_persistence.model_load("model/dt.m")

    result = clf2.predict(test_x)
    count_success = 0
    for i in range(len(result)):
        print(str(result[i]) + " - " + str(test_y.values[i]))
        if result[i] == test_y.values[i]:
            count_success += 1
    print("Predict Success:", str(count_success) + " : " + str(len(result)))


# 多标签预测决策树
def dt_multi_label_single(df: DataFrame, y_name):
    train = df.sample(frac=0.8)
    test = df.drop(train.index)
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(train, y_name)
    print(train_x.keys())
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(test, y_name)
    clf2 = DecisionTreeClassifier(min_samples_leaf=5000)
    clf2.fit(X=train_x, y=train_y)
    result = clf2.predict(test_x)
    pred = clf2.predict_proba(test_x)
    count = 0
    print("Len Pred:", len(pred))
    print("Len Pred[0]:", len(pred[0]))
    print("Len Result:", len(result))
    for i in range(len(result)):
        print("=====")
        print("Result:", result[i])
        print("Origin:", test_y[i])
        print("Proba:", end='')
        for j in range(42):
            print(pred[j][i], end=' ')
        print("")
        result_temp, targeted = compare_multi_label(result[i], test_y[i])
        if result_temp:
            count = count + 1
    print("Predict:", len(result), " Success:", count)


# 多标签预测随机森林
def dt_rf_multi_label_single(df: DataFrame, y_name):
    train = df.sample(frac=0.8)
    test = df.drop(train.index)
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(train, y_name)
    print(train_x.keys())
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(test, y_name)
    clf2 = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    # clf2 = RandomForestClassifier(min_samples_leaf=4000, n_estimators=50)
    clf2.fit(X=train_x, y=train_y)
    result = clf2.predict(test_x)
    pred = clf2.predict_proba(test_x)
    count = 0
    print("Len Pred:", len(pred))
    print("Len Pred[0]:", len(pred[0]))
    print("Len Result:", len(result))
    for i in range(len(result)):
        print("=====")
        print("Result:", result[i])
        print("Origin:", test_y[i])
        print("Proba:", end='')
        for j in range(42):
            print(pred[j][i], end=' ')
        print("")
        result_temp, targeted = compare_multi_label(result[i], test_y[i])
        if result_temp:
            count = count + 1
    print("Predict:", len(result), " Success:", count)


#
def dt_rf_multi_label_single_privided_train_test_no_multi_label(df_train: DataFrame, df_test: DataFrame, y_name):
    train_x, train_y = df_train, df_train.pop("y_final_result")
    test_x, test_y = df_test, df_test.pop("y_final_result")
    clf2 = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    clf2.fit(X=train_x, y=train_y)
    result = clf2.predict(test_x)
    pred = clf2.predict_proba(test_x)
    count = 0
    for i in range(len(result)):
        print("=====")
        print("Result:", result[i])
        print("Origin:", test_y[i])
        print("Proba:", pred[i])
        if result[i] == test_y[i]:
            count = count + 1
    print("Predict:", len(result), " Success:", count)
    print(train_x.__len__())
    print(test_x.__len__())


# 多标签预测决策树，使用提供好的训练集和测试集
def dt_rf_multi_label_single_privided_train_test(df_train: DataFrame, df_test: DataFrame, y_name):
    train_x, train_y = preprocessing_set.convert_y_multi_label_by_name(df_train, y_name)
    print(train_x.keys(), file=f)
    test_x, test_y = preprocessing_set.convert_y_multi_label_by_name(df_test, y_name)
    clf2 = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    clf2.fit(X=train_x, y=train_y)
    result = clf2.predict(test_x)
    pred = clf2.predict_proba(test_x)
    count = 0
    targeted_count = 0
    print("Len Pred:", len(pred), file=f)
    print("Len Pred[0]:", len(pred[0]), file=f)
    print("Len Result:", len(result), file=f)
    for i in range(len(result)):
        print("=====", file=f)
        print("Result:", result[i], file=f)
        print("Origin:", test_y[i], file=f)
        print("Proba:", end='', file=f)
        for j in range(2):
            print(str(j), "-", pred[j][i], end=' ', file=f)
        print("", file=f)
        result_temp, targeted = compare_multi_label(result[i], test_y[i])
        if result_temp:
            count = count + 1

        if targeted:
            targeted_count += 1
    print("Predict:", len(result), " Success:", count)
    print("Targeted:", targeted_count, "次")
    print(train_x.__len__())
    print(test_x.__len__())


def rf(df: DataFrame, y_name):
    x, y = df, df.pop(y_name)
    x_val = x.values
    y_val = y.values
    for key in x.keys():
        print("Feature name in X:", key)
    clf = RandomForestClassifier()
    param_test = {
        "max_depth": [None, 10, 20, 30],
        "n_estimators": [5, 10, 20, 50, 100]
    }
    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test


def et(df: DataFrame, y_name):
    x, y = df, df.pop(y_name)
    x_val = x.values
    y_val = y.values
    for key in x.keys():
        print("Feature name in X:", key)
    clf = ExtraTreesClassifier(max_depth=None,
                               random_state=0)
    param_test = {
        "n_estimators": [5, 10, 20, 30, 50, 100, 500],
        "max_depth": [None, 10, 20, 30]
    }
    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test


def gbc(df: DataFrame, y_name):
    x, y = df, df.pop(y_name)
    x_val = x.values
    y_val = y.values
    for key in x.keys():
        print("Feature name in X:", key)
    clf = GradientBoostingClassifier()
    param_test = {
        "n_estimators": [5, 20, 50, 100],
        "learning_rate": [0.01, 0.1, 1],
        "max_depth": [1, 5, 10]
    }
    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test


def svc(df: DataFrame, y_name):
    x, y = df, df.pop(y_name)
    x_val = x.values
    y_val = y.values
    for key in x.keys():
        print("Feature name in X:", key)
    clf = SVC(gamma='auto')
    ovr = OneVsRestClassifier(clf)
    param_test = {
        "estimator__C": [1, 2, 4, 8],
        "estimator__kernel": ["poly", "rbf", "sigmoid"],
        "estimator__degree": [1, 2, 3, 4],
    }
    grid_search_cv = GridSearchCV(ovr,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test


def mlp(df: DataFrame, y_name):
    x, y = df, df.pop(y_name)
    x_val = x.values
    y_val = y.values
    for key in x.keys():
        print("Feature name in X:", key)
    clf = MLPClassifier()
    param_test = {
        "hidden_layer_sizes": [(30, 30), (10, 10), (50, 50)],
        "max_iter": [200, 500, 1000, 2000]
    }
    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test


def vc(df: DataFrame, y_name):
    x, y = df, df.pop(y_name)
    x_val = x.values
    y_val = y.values
    for key in x.keys():
        print("Feature name in X:", key)
    clf1 = MLPClassifier(max_iter=500)
    clf2 = RandomForestClassifier(random_state=1)
    clf3 = GaussianNB()
    clf = VotingClassifier(
        estimators=[
            ('mlp', clf1), ('rf', clf2), ('gnb', clf3)
        ],
        voting='hard')
    param_test = {
        "mlp__activation": ["identity", "logistic", "tanh", "relu"],
        "mlp__solver": ["lbfgs", "sgd", "adam"],
        "mlp__hidden_layer_sizes": [(5, 5), (20, 20), (50, 50)],
        'rf__n_estimators': [10, 30, 100]
    }
    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=x_val, y=y_val)
    return grid_search_cv, param_test
