import pandas as pd

input_after_fill_empty_path = "transform/final_after_fill_empty.csv"
output_after_transform_path = "transform/final_after_transform.csv"


def transform_cpu(value):
    value_new = value / 1000.0
    if value_new <= 0.1:
        return 1
    elif value_new <= 0.3:
        return 2
    elif value_new <= 0.6:
        return 3
    elif value_new > 0.6:
        return 4
    else:
        return 0


def transform_memory(value):
    if value <= 200:
        return 1
    elif value <= 500:
        return 2
    elif value <= 1000:
        return 3
    elif value > 1000:
        return 4
    else:
        return 0


def transform_span(value):
    if value <= 1:
        return 1
    elif value <= 2:
        return 2
    elif value <= 6:
        return 3
    elif value > 6:
        return 4
    else:
        return 0


data_after_feature_extraction = pd.read_csv(input_after_fill_empty_path,
                                            header=0,
                                            index_col=0)

print(input_after_fill_empty_path, "has", len(data_after_feature_extraction.keys()), "columns")

keys = data_after_feature_extraction.keys()
for key in keys:
    if key.endswith("service_id") \
            or key.endswith("node_id") \
            or key.endswith("inst_id") \
            or key.endswith("test_case_id") \
            or key.endswith("entry_service") \
            or key.endswith("entry_api") \
            or key.endswith("entry_req_type") \
            or key.endswith("_service_version") \
            or key.endswith("service_inst_1_id"):
        data_after_feature_extraction = pd.get_dummies(data_after_feature_extraction,
                                                       prefix=[key],
                                                       columns=[key])
    elif key.endswith("_cpu_limit") \
            or key.endswith("_cpu"):
        data_after_feature_extraction[key] = data_after_feature_extraction[[key]].applymap(transform_cpu)
    elif key.endswith("_memory_limit") \
            or key.endswith("_memory"):
        data_after_feature_extraction[key] = data_after_feature_extraction[[key]].applymap(transform_memory)
    elif key.endswith("span"):
        data_after_feature_extraction[key] = data_after_feature_extraction[[key]].applymap(transform_span)

# print(output_after_transform_path, "has", len(data_after_feature_extraction.keys()), "columns")

# Output the result file.
data_after_feature_extraction.to_csv(output_after_transform_path)
