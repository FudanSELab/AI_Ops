import pandas as pd
from pandas import DataFrame
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV

input_path = "../input/trace_instance_3_verifyd.csv"
index_col_name = "trace_verified.trace_id"
label_col_name = "trace_verified.y_final_result"


def print_best_score(gsearch, param_test):
    # f = open("log.txt", 'w+')
    print("Best score: %0.3f" % gsearch.best_score_)
    print("Best parameters set:")
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


def fetch_data():
    df_raw = pd.read_csv(input_path,
                         header=0,
                         index_col=index_col_name)
    df_new = DataFrame()
    for col in df_raw.keys():
        if col.endswith("_readynumber") \
                or col.endswith(".trace_service") \
                or col.endswith(".trace_api") \
                or col.endswith("_diff") \
                or col.endswith(".y_issue_ms") \
                or col.endswith(".y_final_result") \
                or col.endswith(".y_issue_dim_type"):
            df_new[col] = df_raw[col]
            print("Fetch:", col)
    df_new.to_csv("trace_all_all.csv")


def join_data():
    df_feature = pd.read_csv("trace_all_all.csv",
                             header=0,
                             index_col=index_col_name)
    df_seq = pd.read_csv("../input/seq_instance_3_1_newest.csv",
                         header=0,
                         index_col="seq_instance_3_1.trace_id1")
    df_seq.pop("seq_instance_3_1.test_trace_id1")
    df_seq.pop("seq_instance_3_1.test_case_id1")

    df_joined = df_feature.join(df_seq, how="left")

    df_joined.to_csv("trace_seq_all_all.csv")


def extraction():
    df_total_raw = pd.read_csv("trace_seq_all_all.csv", header=0, index_col=index_col_name)

    df_total_raw["trace_verified.y_issue_ms"] = df_total_raw["trace_verified.y_issue_ms"].fillna("Success")
    df_total_raw["trace_verified.y_issue_ms"] = df_total_raw["trace_verified.y_issue_dim_type"].fillna("Success")

    keys = df_total_raw.keys()
    for key in keys:
        if key.endswith("trace_verified.trace_service") \
                or key.endswith("trace_verified.trace_api") \
                or key.endswith("trace_verified.y_issue_ms") \
                or key.endswith("trace_verified.y_issue_dim_type"):
            mapping_keys = df_total_raw[key].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_total_raw[key] = df_total_raw[key].map(mapping)
        elif key.endswith("_diff"):
            df_total_raw[key] = df_total_raw[key].fillna(0)
        elif key.startswith("seq_"):
            df_total_raw[key] = df_total_raw[key].fillna(-1)
        elif key.endswith("trace_verified.y_issue_ms") \
                or key.endswith("trace_verified.y_issue_dim_type"):
            df_total_raw["trace_verified.y_issue_ms"] = df_total_raw["trace_verified.y_issue_ms"].fillna("Success")
    df_total_raw.to_csv("trace_seq_all_all_extraction.csv")


def train_dt():
    file_path = "trace_seq_all_all_extraction.csv"
    y_name = "trace_verified.y_final_result"

    df = pd.read_csv(file_path,
                     header=0,
                     index_col=index_col_name)
    df.pop("trace_verified.y_issue_dim_type")
    df.pop("trace_verified.y_issue_ms")

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
                                  cv=5)
    grid_search_cv.fit(X=X, y=Y)
    print_best_score(grid_search_cv, param_test)


if __name__ == "__main__":
    train_dt()
