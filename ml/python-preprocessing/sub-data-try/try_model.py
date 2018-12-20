import pandas as pd
from sklearn.tree import DecisionTreeClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import GridSearchCV
from sklearn.preprocessing import MinMaxScaler
from sklearn.decomposition import PCA


def print_best_score(gsearch, param_test):
    f = open("log.txt", 'w+')
    print("Best score: %0.3f" % gsearch.best_score_,
          file=f)
    print("Best parameters set:",
          file=f)
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]),
              file=f)


def try_dt_mock(data_file_path):
    y_name = "result"
    df = pd.read_csv(data_file_path,
                     header=0,
                     index_col=0)
    X, Y = df, df.pop(y_name)

    print(X.keys())

    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None, 10, 20, 30, 100, 200, 400],
    }

    # clf = RandomForestClassifier()
    # param_test = {
    #     "max_depth": [None, 10, 20, 30, 100],
    #     "n_estimators": [5, 10, 20, 50]
    # }

    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=10)
    grid_search_cv.fit(X=X, y=Y)
    print_best_score(grid_search_cv, param_test)

    # data_file_path2 = "mock_new_one_hot_map.csv"
    # df2 = pd.read_csv(data_file_path2,
    #                   header=0,
    #                   index_col=0)
    # train = df2.sample(frac=0.8)
    # test = df2.drop(train.index)
    # train_x, train_y = train, train.pop(y_name)
    # test_x, test_y = test, test.pop(y_name)
    #
    # clf2 = DecisionTreeClassifier()
    # clf2.fit(X=train_x, y=train_y)
    # result = clf2.predict(test_x)
    # count_success = 0
    # count_y_positive = 0
    # count_result_positive = 0
    # for i in range(len(result)):
    #     print(str(result[i]) + " - " + str(test_y.values[i]))
    #     if result[i] == test_y.values[i]:
    #         count_success += 1
    #     if test_y.values[i] == 1:
    #         count_y_positive += 1
    #     if result[i] == 1:
    #         count_result_positive += 1
    # print("Predict Success:", str(count_success) + " : " + str(len(result)))
    # print("Y Positive:", str(count_y_positive) + " : " + str(len(result)))
    # print("Predict Positive:", str(count_result_positive) + " : " + str(len(result)))


if __name__ == "__main__":
    try_dt_mock("mock_new_one_hot_10_st.csv")
