from pandas import DataFrame
from sklearn.ensemble import RandomForestClassifier, ExtraTreesClassifier, GradientBoostingClassifier, VotingClassifier
from sklearn.multiclass import OneVsRestClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.neural_network import MLPClassifier
from sklearn.svm import SVC
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV


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


def dt_single(df: DataFrame, y_name):
    train = df.sample(frac=0.8)
    test = df.drop(train.index)
    train_x, train_y = train, train.pop(y_name)
    test_x, test_y = test, test.pop(y_name)
    clf2 = DecisionTreeClassifier()
    clf2.fit(X=train_x, y=train_y)
    result = clf2.predict(test_x)
    count_success = 0
    for i in range(len(result)):
        print(str(result[i]) + " - " + str(test_y.values[i]))
        if result[i] == test_y.values[i]:
            count_success += 1
    print("Predict Success:", str(count_success) + " : " + str(len(result)))


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
