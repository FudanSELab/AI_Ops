import pandas as pd
from pandas import DataFrame
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV

input_path = "../input/trace_instance_3_verifyd.csv"
index_col_name = "trace_verified.trace_id"
label_col_name = "trace_verified.y_issue_ms"


def print_best_score(gsearch, param_test):
    # f = open("log.txt", 'w+')
    print("Best score: %0.3f" % gsearch.best_score_)
    print("Best parameters set:")
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


def print_cols():
    # Read Two File with assigned index column.
    df = pd.read_csv(input_path,
                     header=0,
                     index_col=index_col_name)
    f = open("column.txt", 'w+')

    for col in df.keys():
        print(col, file=f)


def fetch_data():
    df_raw = pd.read_csv(input_path,
                         header=0,
                         index_col=index_col_name)
    df_new = DataFrame()
    for col in df_raw.keys():
        if col.endswith("_readynumber") \
                or col.endswith(".trace_service") \
                or col.endswith(".trace_api") \
                or col.endswith(".y_issue_ms"):
            df_new[col] = df_raw[col]
        # elif col.endswith("_diff"):
        #     df_new[col] = df_raw[col].fillna("0")
    df_new.to_csv("trace_instance_3_verifyd_fetch_inst_ms.csv")


def drop_convert():
    file_path = "trace_instance_3_verifyd_fetch_inst_ms.csv"
    df_raw = pd.read_csv(file_path,
                         header=0,
                         index_col=index_col_name)
    df_raw[label_col_name] = df_raw[label_col_name].fillna("Success")
    df_raw = df_raw.loc[df_raw[label_col_name] != "Success"]

    # mapping_keys = df_raw["trace_verified.trace_service"].drop_duplicates().values
    # mapping = {}
    # for i in range(len(mapping_keys)):
    #     mapping[mapping_keys[i]] = i
    # df_raw["trace_verified.trace_service"] = df_raw["trace_verified.trace_service"].map(mapping)
    #
    #
    # mapping_keys = df_raw["trace_verified.trace_api"].drop_duplicates().values
    # mapping = {}
    # for i in range(len(mapping_keys)):
    #     mapping[mapping_keys[i]] = i
    # df_raw["trace_verified.trace_api"] = df_raw["trace_verified.trace_api"].map(mapping)

    df_raw = pd.get_dummies(df_raw, columns=["trace_verified.trace_service"])
    df_raw = pd.get_dummies(df_raw, columns=["trace_verified.trace_api"])

    mapping_keys = df_raw["trace_verified.y_issue_ms"].drop_duplicates().values
    mapping = {}
    for i in range(len(mapping_keys)):
        mapping[mapping_keys[i]] = i
    df_raw["trace_verified.y_issue_ms"] = df_raw["trace_verified.y_issue_ms"].map(mapping)

    df_raw.to_csv("trace_instance_3_verifyd_fetch_inst_convert_ms.csv")


def train_dt():
    file_path = "trace_instance_3_verifyd_fetch_inst_convert_ms.csv"
    y_name = "trace_verified.y_issue_ms"
    df = pd.read_csv(file_path,
                     header=0,
                     index_col=index_col_name)
    X, Y = df, df.pop(y_name)

    print("Feature name in X:")
    for key in X.keys():
        print(key)

    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None, 10, 20, 30, 100, 200, 400],
    }

    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=10)
    grid_search_cv.fit(X=X, y=Y)
    print_best_score(grid_search_cv, param_test)

    df2 = pd.read_csv(file_path,
                      header=0,
                      index_col=0)
    train = df2.sample(frac=0.8)
    test = df2.drop(train.index)
    train_x, train_y = train, train.pop(y_name)
    test_x, test_y = test, test.pop(y_name)
    print("Feature name in X:")
    for key in train_x.keys():
        print(key)
    clf2 = DecisionTreeClassifier()
    clf2.fit(X=train_x, y=train_y)
    result = clf2.predict(test_x)
    count_success = 0
    count_y_positive = 0
    count_result_positive = 0
    for i in range(len(result)):
        print(str(result[i]) + " - " + str(test_y.values[i]))
        if result[i] == test_y.values[i]:
            count_success += 1
        if test_y.values[i] == 1:
            count_y_positive += 1
        if result[i] == 1:
            count_result_positive += 1
    print("Predict Success:", str(count_success) + " : " + str(len(result)))
    print("Y Positive:", str(count_y_positive) + " : " + str(len(result)))
    print("Predict Positive:", str(count_result_positive) + " : " + str(len(result)))


if __name__ == "__main__":
    fetch_data()
    drop_convert()
    train_dt()
