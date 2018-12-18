import pandas as pd
from pandas import DataFrame
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV

input_path = "../input/trace_instance_3_verifyd.csv"
index_col_name = "trace_verified.trace_id"
label_col_name = "trace_verified.y_final_result"


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
                or col.endswith(".y_final_result"):
            df_new[col] = df_raw[col]
            print("Fetch:", col)
    df_new.to_csv("trace_instance_3_verifyd_fetch_inst_result.csv")


def drop_convert():
    file_path = "trace_instance_3_verifyd_fetch_inst_result.csv"
    df_raw = pd.read_csv(file_path,
                         header=0,
                         index_col=index_col_name)

    mapping_keys = df_raw["trace_verified.trace_service"].drop_duplicates().values
    mapping = {}
    for i in range(len(mapping_keys)):
        mapping[mapping_keys[i]] = i
    df_raw["trace_verified.trace_service"] = df_raw["trace_verified.trace_service"].map(mapping)

    mapping_keys = df_raw["trace_verified.trace_api"].drop_duplicates().values
    mapping = {}
    for i in range(len(mapping_keys)):
        mapping[mapping_keys[i]] = i
    df_raw["trace_verified.trace_api"] = df_raw["trace_verified.trace_api"].map(mapping)

    df_raw.to_csv("trace_instance_3_verifyd_fetch_inst_convert_result.csv")


def train_dt():
    file_path = "trace_instance_3_verifyd_fetch_inst_convert_result.csv"
    y_name = "trace_verified.y_final_result"
    df = pd.read_csv(file_path,
                     header=0,
                     index_col=index_col_name)
    X, Y = df, df.pop(y_name)
    print(X.keys())

    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None, 10, 20, 30, 100, 200, 400],
    }

    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=10)
    grid_search_cv.fit(X=X, y=Y)
    print_best_score(grid_search_cv, param_test)


if __name__ == "__main__":
    fetch_data()
    drop_convert()
    train_dt()

