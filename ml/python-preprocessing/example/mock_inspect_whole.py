import pandas as pd
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV

input_path = "../mock/mock_whole_20.csv"
label_col_result = "result"
label_col_ms = "ms"


def print_best_score(gsearch, param_test):
    # f = open("log.txt", 'w+')
    print("Best score: %0.3f" % gsearch.best_score_)
    print("Best parameters set:")
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


def drop_convert():
    file_path = input_path
    df_raw = pd.read_csv(file_path,
                         header=0,
                         index_col=0)
    for col in df_raw.keys():
        if col.endswith("_caller") \
                or col.endswith("entry_svc"):
            # df_raw = pd.get_dummies(df_raw, columns=[col])
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
        elif col.endswith("ms"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)

    df_raw.to_csv("mock_whole_20_converted.csv")


def train_dt():
    file_path = "mock_whole_20_converted.csv"
    y_name = label_col_result
    df = pd.read_csv(file_path,
                     header=0,
                     index_col=0)
    df.pop(label_col_ms)
    X, Y = df, df.pop(y_name)

    print("Feature Name in X:")
    for col in X.keys():
        print(col)

    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None, 10, 20, 30, 100, 200],
    }
    print("Label Name in Y:", y_name)

    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=10)
    grid_search_cv.fit(X=X, y=Y)
    print_best_score(grid_search_cv, param_test)


if __name__ == "__main__":
    drop_convert()
    train_dt()
