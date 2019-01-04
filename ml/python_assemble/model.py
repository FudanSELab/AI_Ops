from pandas import DataFrame
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