import pandas as pd

input_after_wash_path = "transform/final_after_wash.csv"
output_after_feature_extraction_path = "transform/final_after_feature_extraction.csv"

data_after_wash = pd.read_csv(input_after_wash_path,
                              header=0,
                              index_col=0)

keys = data_after_wash.keys()
print(input_after_wash_path, "has", len(keys), "keys")

for key in keys:
    if key.endswith("_servicename"):
        # TODO：How to process _servicename
        print("Pop: " + key)
        data_after_wash.pop(key)
    elif key.endswith("test_case_id"):
        # TODO: Type
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("entry_service"):
        # TODO: Type
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("_service_version"):
        # TODO: Type
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("_service_id"):
        # TODO: Type
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("service_inst_1_id"):
        # TODO: Type
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("entry_api"):
        # TODO: Type
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("entry_req_type"):
        # TODO: Type
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("_inst_id"):
        # TODO:
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("inst_service_id"):
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("_node_id"):
        print("Mapping: " + key)
        mapping_keys = data_after_wash[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        data_after_wash[key] = data_after_wash[key].map(mapping)
    elif key.endswith("_cpu_limit"):
        print("Fetch: " + key)
        data_after_wash[key] = data_after_wash[key].str[:-1]  # 7m → 7
    elif key.endswith("_memory_limit"):
        print("Fetch: " + key)
        data_after_wash[key] = data_after_wash[key].str[:-2]  # 22181452Ki → 22181452
    elif key.endswith("_cpu"):
        print("Fetch: " + key)
        data_after_wash[key] = data_after_wash[key].str[:-1]  # 7m → 7
    elif key.endswith("_memory"):
        print("Fetch: " + key)
        data_after_wash[key] = data_after_wash[key].str[:-2]  # 22181452Ki → 22181452
    else:
        print("Reserved:" + key)

print(output_after_feature_extraction_path, "has", len(data_after_wash.keys()), "columns")

# Output the result file.
data_after_wash.to_csv(output_after_feature_extraction_path)
