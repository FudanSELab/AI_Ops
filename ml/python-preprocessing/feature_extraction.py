import pandas as pd

input_after_sampling_path = "transform/final_after_sampling.csv"
output_after_feature_extraction_path = "transform/final_after_feature_extraction.csv"

data_after_sampling = pd.read_csv(input_after_sampling_path,
                                  header=0,
                                  index_col=0)

keys = data_after_sampling.keys()
print(input_after_sampling_path, "has", len(keys), "keys")

for key in keys:
    if key.endswith("_servicename") \
            or key.endswith("timestamp") \
            or key.endswith("_time"):
        # TODO：How to process _servicename
        print("Pop: " + key)
        data_after_sampling.pop(key)
    elif key.endswith("service_id") \
            or key.endswith("node_id") \
            or key.endswith("inst_id") \
            or key.endswith("test_case_id") \
            or key.endswith("entry_service") \
            or key.endswith("entry_api") \
            or key.endswith("entry_req_type") \
            or key.endswith("_service_version") \
            or key.endswith("service_inst_1_id")\
            or key.endswith("new_trace_y.y_issue_ms") \
            or key.endswith("new_trace_y.y_issue_dim_type") \
            or key.endswith("new_trace_y.y_issue_dim_content"):
        print("Mapping: " + key)
        mapping_keys = data_after_sampling[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_sampling[key] = data_after_sampling[key].map(mapping)
    elif key.endswith("_cpu_limit") \
            or key.endswith("_cpu"):
        print("Fetch: " + key)
        data_after_sampling[key] = data_after_sampling[key].str[:-1]  # 7m → 7
    elif key.endswith("_memory_limit") \
            or key.endswith("_memory"):
        print("Fetch: " + key)
        data_after_sampling[key] = data_after_sampling[key].str[:-2]  # 22181452Ki → 22181452
    else:
        print("Reserved:" + key)

print(output_after_feature_extraction_path, "has", len(data_after_sampling.keys()), "columns")

# Output the result file.
data_after_sampling.to_csv(output_after_feature_extraction_path)
