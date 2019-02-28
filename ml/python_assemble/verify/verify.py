import pandas as pd


def verify():
    df = pd.read_csv("../ready_use_max_without_sampling_mms.csv",
                     header=0, index_col="trace_id")

    for col in df.keys():
        if not(col.endswith("_volume_support")
               or col.endswith("_cpu")
               or col.endswith("_memory")
               or col.endswith("_status_code")
               or col.endswith("_exec_time")
               or col.endswith("_node_instance_count")
               or col.endswith("_readynumber")
               or col.endswith("_diff")
               or col.endswith("_variable")
               or col.endswith("_included")
               or col.endswith("_app_thread_count")
               or col.endswith("_shared_variable")
               or col.endswith("_dependent_db")
               or col.endswith("_dependent_cache")
               or col.endswith("_seq")
               or col.endswith("y_issue_ms")
               or col.endswith("y_final_result")
               or col.endswith("y_issue_dim_type")):
            print("Drop", col)
            df.drop(columns=col, axis=1, inplace=True)
    df.to_csv("verify.csv")


if __name__ == "__main__":
    verify()