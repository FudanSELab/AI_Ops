import pandas as pd
from pandas import DataFrame


input_file_path = "../transform/final_after_join.csv"
output_file_path = "fetch_instance.csv"

df_raw = pd.read_csv(input_file_path,
                     header=0,
                     index_col=0)
df_inst = DataFrame()

df_inst["new_trace_y.entry_service"] = df_raw["new_trace_y.entry_service"]
df_inst["new_trace_y.entry_api"] = df_raw["new_trace_y.entry_api"]
df_inst["new_trace_y.y_exec_result"] = df_raw["new_trace_y.y_exec_result"]

for col in df_raw.keys():
    if col.endswith("_confnumber"):
            df_inst[col] = df_raw[col]
            print("Fetch:", col)

arr = df_inst["new_trace_y.y_exec_result"].values
count1 = 0
count0 = 0
for i in range(len(arr)):
    if arr[i] == 0:
        count0 += 1
    else:
        count1 += 1
print(count0/(count1+count0))


# df_inst["new_trace_y.y_issue_dim_type"] = df_inst["new_trace_y.y_issue_dim_type"].fillna("Success")

# mapping_keys = df_inst["new_trace_y.y_issue_dim_type"].drop_duplicates().values
# mapping = {}
# for i in range(len(mapping_keys)):
#     mapping[mapping_keys[i]] = i
# df_inst["new_trace_y.y_issue_dim_type"] = df_inst["new_trace_y.y_issue_dim_type"].map(mapping)

# df_inst.to_csv(output_file_path)
