from sklearn.tree import DecisionTreeClassifier

import preprocessing_set
import model
import pandas as pd
from pandas import DataFrame


def print_best_score(gsearch, param_test):
    # f = open("log.txt", 'w+')
    print("Best score: %0.3f" % gsearch.best_score_)
    print("Best parameters set:")
    best_parameters = gsearch.best_params_
    for param_name in sorted(param_test.keys()):
        print("\t%s: %r" % (param_name, best_parameters[param_name]))


if __name__ == "__main__":
    trace_csv = "17/trace_verified_instance.csv"
    trace_index_col = "trace_verified_instance_1_7.trace_id"
    seq_csv = "17/seq_seq_instance.csv"
    seq_index_col = "seq_seq_instance2.trace_id"
    seq_caller_csv = "17/seq_caller_instance.csv"
    seq_caller_index_col = "seq_caller_instance2.trace_id"

    df_trace = pd.read_csv(trace_csv,
                           header=0,
                           index_col=trace_index_col)
    df_seq = pd.read_csv(seq_csv,
                         header=0,
                         index_col=seq_index_col)
    df_seq_caller = pd.read_csv(seq_caller_csv,
                                header=0,
                                index_col=seq_caller_index_col)

    df_trace = preprocessing_set.drop_na_data(df_trace)
    df_trace = preprocessing_set.drop_all_same_data(df_trace)
    df_trace = preprocessing_set.select_data(df_trace)

    df_seq = preprocessing_set.drop_na_data(df_seq)
    df_seq = preprocessing_set.drop_all_same_data(df_seq)
    df_seq = preprocessing_set.select_data(df_seq)

    df_seq_caller = preprocessing_set.drop_na_data(df_seq_caller)
    df_seq_caller = preprocessing_set.drop_all_same_data(df_seq_caller)
    df_seq_caller = preprocessing_set.select_data(df_seq_caller)

    df = preprocessing_set.merge_data(df_trace=df_trace,
                                      df_seq=df_seq,
                                      df_seq_caller=df_seq_caller)

    df = preprocessing_set.fill_empty_data(df)


    # df = preprocessing_set.convert_data(df)



    # df = df.loc[df["trace_verified_instance_1_7.y_issue_ms"] != "Success"]

    df = preprocessing_set.convert_data(df)
    df, y_multi_label = preprocessing_set.convert_y_multi_label(df, "trace_verified_instance_1_7.y_issue_ms")
    # df = preprocessing_set.convert_y_multi_label(df, "trace_verified_instance_1_7.y_issue_dim_type")

    df.pop("trace_verified_instance_1_7.y_final_result")
    # df.pop("trace_verified_instance_1_7.y_issue_ms")
    df.pop("trace_verified_instance_1_7.y_issue_dim_type")

    cv, parm = model.dt_multi_label(df, y_multi_label)
    print_best_score(cv, parm)

    # You must save the preprocessing result.
    # df.to_csv("test_run.csv")


    # cv, parm = model.dt(df, "trace_verified_instance_1_7.y_final_result")
    # cv, parm = model.dt(df, "trace_verified_instance_1_7.y_issue_ms")
    # # cv, parm = model.dt(df, "trace_verified_instance_1_7.y_issue_dim_type")
    # print_best_score(cv, parm)
