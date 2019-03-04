import pandas as pd
from imblearn.over_sampling import RandomOverSampler
from sklearn.ensemble import RandomForestClassifier
from sklearn.utils import shuffle


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


def fetch():
    df = pd.read_csv("verify.csv", header=0, index_col="trace_id")
    df = df.loc[df["y_final_result"] == 1]
    df.to_csv("fault.csv")


def split(df: pd.DataFrame):
    train = df.sample(frac=0.8)
    test = df.drop(train.index)
    return train, test


# (Model_1 Model_2共用)按照y_name对数据进行过采样操作
def sampling(df_raw: pd.DataFrame, y_name):
    x, y = df_raw, df_raw.pop(y_name)
    x_keys = x.keys()  # Save x-keys.
    x_res, y_res = RandomOverSampler().fit_resample(x, y)
    df_new_x = pd.DataFrame(data=x_res, columns=x_keys)
    df_new_x[y_name] = y_res
    df_new_x = shuffle(df_new_x)
    return df_new_x


def train():
    df = pd.read_csv("verify.csv", header=0, index_col="trace_id")
    df.pop("y_issue_dim_type")
    df.pop("y_issue_ms")



    train, test = split(df)

    train = sampling(train, "y_final_result")

    x, y = train, train.pop("y_final_result")
    clf = RandomForestClassifier(min_samples_leaf=5000, n_estimators=3)
    clf.fit(x, y)

    real_x, real_y = test, test.pop("y_final_result")

    pred_y = clf.predict(real_x)

    count = 0
    for i in range(len(real_y)):
        print(pred_y[i], real_y[i])
        if pred_y[i] == real_y[i]:
            count += 1
    print(count/len(real_y))



if __name__ == "__main__":
    train()