import pandas as pd
from pandas import DataFrame
from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import GridSearchCV

trace_path = "../input/trace_verified_instance2.csv"
trace_index_col = "trace_verified_instance.trace_id"

seq_path = "../input/seq_seq_instance2.csv"
seq_index_col = "seq_seq_instance.trace_id"

seq_caller_path = "../input/seq_caller_instance2.csv"
seq_caller_index_col = "seq_caller_instance.trace_id"

seq_merged_path = "./data/seq_merged_2.csv"

trace_seq_joined = "./data/trace_seq_joined.csv"

trace_selected = "./data/trace_all_2.csv"

trace_dropped = "./data/trace_all_dropped_2.csv"

trace_filled = "./data/trace_all_filled_2.csv"

trace_converted = "./data/trace_all_converted_2.csv"


def print_best_score(gsearch, param_test):
    # f = open("log.txt", 'w+')
    print("Best score: %0.3f" % gsearch.best_score_)
    print("Best parameters set:")
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


def merge_data():
    df_trace = pd.read_csv(trace_path, header=0, index_col=trace_index_col)
    df_seq = pd.read_csv(seq_path, header=0, index_col=seq_index_col)
    df_seq_caller = pd.read_csv(seq_caller_path, header=0, index_col=seq_caller_index_col)

    df_merged_seq = df_seq.join(df_seq_caller, how="inner")
    df_merged_seq.to_csv(seq_merged_path)

    df_trace_seq_joined = df_trace.join(df_merged_seq, how="left")
    df_trace_seq_joined.to_csv(trace_seq_joined)


def select_data():
    df_raw = pd.read_csv(trace_seq_joined,
                         header=0,
                         index_col=trace_index_col)
    df_new = DataFrame()
    for col in df_raw.keys():
        if col.endswith(".trace_service") \
                or col.endswith(".trace_api")\
                or col.endswith("_readynumber")\
                or col.endswith("_seq")\
                or col.endswith("_caller")\
                or col.endswith(".y_issue_ms") \
                or col.endswith(".y_final_result") \
                or col.endswith(".y_issue_dim_type"):
            df_new[col] = df_raw[col]
            print("Fetch:", col)
    df_new.to_csv(trace_selected)


def drop_data():
    df_raw = pd.read_csv(trace_selected,
                         header=0,
                         index_col=trace_index_col)
    df_raw = df_raw.dropna(axis=1, how='all')
    df_raw.to_csv(trace_dropped)


def fill_empty_data():
    df_raw = pd.read_csv(trace_dropped,
                         header=0,
                         index_col=trace_index_col)
    keys = df_raw.keys()
    for col in keys:
        if col.endswith("_diff"):
            df_raw[col] = df_raw[col].fillna(0)
        elif col.endswith("_seq"):
            df_raw[col] = df_raw[col].fillna(-1)
        elif col.endswith("_caller"):
            df_raw[col] = df_raw[col].fillna("No")
        elif col.endswith("y_issue_ms"):
            df_raw[col] = df_raw[col].fillna("Success")
    df_raw.to_csv(trace_filled)

    df_ms_raw = df_raw.loc[df_raw["trace_verified_instance.y_issue_ms"] != "Success"]
    df_ms_raw.to_csv("./data/trace_wrong_raw.csv")


def convert_data():
    df_raw = pd.read_csv(trace_filled,
                         header=0,
                         index_col=trace_index_col)
    keys = df_raw.keys()
    for col in keys:
        if col.endswith("trace_verified_instance.y_issue_ms") \
                or col.endswith("trace_verified_instance.y_issue_dim_type"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
        elif col.endswith("trace_verified_instance.trace_service") \
                or col.endswith("trace_verified_instance.trace_api") \
                or col.endswith("_caller"):
            mapping_keys = df_raw[col].drop_duplicates().values
            mapping = {}
            for i in range(len(mapping_keys)):
                mapping[mapping_keys[i]] = i
            df_raw[col] = df_raw[col].map(mapping)
    df_raw.to_csv(trace_converted)


def train_dt():
    file_path = trace_converted
    y_name = "trace_verified_instance.y_final_result"

    df = pd.read_csv(file_path,
                     header=0,
                     index_col=trace_index_col)

    df.pop("trace_verified_instance.y_issue_dim_type")
    df.pop("trace_verified_instance.y_issue_ms")

    X, Y = df, df.pop(y_name)

    X_val = X.values
    Y_val = Y.values

    print("Feature name in X:")
    for key in X.keys():
        print(key)

    clf = DecisionTreeClassifier()
    param_test = {
        "max_depth": [None],
    }

    grid_search_cv = GridSearchCV(clf,
                                  param_grid=param_test,
                                  cv=5)
    grid_search_cv.fit(X=X_val, y=Y_val)
    print_best_score(grid_search_cv, param_test)


def train_dt_single():
    file_path = trace_converted
    y_name = "trace_verified_instance.y_final_result"

    df = pd.read_csv(file_path,
                     header=0,
                     index_col=trace_index_col)

    df.pop("trace_verified_instance.y_issue_dim_type")
    df.pop("trace_verified_instance.y_issue_ms")

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


if __name__ == "__main__":
    # merge_data()
    # select_data()
    # drop_data()
    # fill_empty_data()
    # convert_data()
    train_dt()
    train_dt_single()