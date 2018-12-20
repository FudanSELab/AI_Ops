import pandas as pd
from pandas import DataFrame
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV

input_path = "../mock/mock_seq_5.csv"
label_col_name_result = "result"
label_col_name_ms = "ms"


def print_best_score(gsearch, param_test):
    print("Best score: %0.3f" % gsearch.best_score_)
    print("Best parameters set:")
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


def print_cols():
    # Read Two File with assigned index column.
    df = pd.read_csv(input_path,
                     header=0,
                     index_col=0)
    f = open("mock_seq_column.txt", 'w+')

    for col in df.keys():
        print(col, file=f)


def drop_convert():
    file_path = input_path
    df_raw = pd.read_csv(file_path,
                         header=0,
                         index_col=0)
    for col in df_raw.keys():
        if col.endswith("_caller") or col.endswith("ms"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
    df_raw.to_csv("mock_seq_5_converted.csv")


def train_dt():
    file_path = "mock_seq_5_converted.csv"
    y_name = label_col_name_ms
    df = pd.read_csv(file_path,
                     header=0,
                     index_col=0)
    df.pop(label_col_name_result)
    X, Y = df, df.pop(y_name)

    print("Feature Name in X:", X.keys())

    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None, 10, 20, 30, 100, 200, 400],
    }

    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=10)
    grid_search_cv.fit(X=X, y=Y)
    print_best_score(grid_search_cv, param_test)


def train_dt_single():
    df2 = pd.read_csv("mock_seq_5_converted.csv",
                      header=0,
                      index_col=0)
    df2.pop(label_col_name_result)

    train = df2.sample(frac=0.8)
    test = df2.drop(train.index)
    train_x, train_y = train, train.pop(label_col_name_ms)
    test_x, test_y = test, test.pop(label_col_name_ms)
    print("Feature name in X:")
    for key in train_x.keys():
        print(key)
    clf2 = DecisionTreeClassifier()
    clf2.fit(X=train_x, y=train_y)
    result = clf2.predict(test_x)
    count_success = 0
    for i in range(len(result)):
        print(str(result[i]) + " - " + str(test_y.values[i]))
        if result[i] == test_y.values[i]:
            count_success += 1
    print("Predict Success:", str(count_success) + " : " + str(len(result)))


if __name__ == "__main__":
    print_cols()
    drop_convert()
    train_dt()
    train_dt_single()
