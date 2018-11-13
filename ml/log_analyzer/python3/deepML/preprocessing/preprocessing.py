import pandas as pd


# 这里的难点在于怎么把参数抠掉
def cut_api(full_api):
    find_index_head = full_api.find('/', 7)
    return full_api[find_index_head:]


features = pd.read_csv("input/sample_trace.csv", header=0, index_col=None)

features.pop("real_trace2.test_trace_id")
features.pop("real_trace2.trace_id")

# 把全是NULL的列丢了
features = features.dropna(axis=1, how='all')

keys = features.keys()

for key in keys:
    if key.endswith("_servicename"):
        print("Drop: " + key)
        features.pop(key)
    elif key.endswith("test_case_id"):
        print("Mapping: " + key)
        mapping_keys = features[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        features[key] = features[key].map(mapping)
    elif key.endswith("entry_service"):
        print("Mapping: " + key)
        mapping_keys = features[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        features[key] = features[key].map(mapping)
        mapping_keys = features[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        features[key] = features[key].map(mapping)
    elif key.endswith("_inst_service_version"):
        print("Mapping: " + key)
        mapping_keys = features[key].drop_duplicates().values
        mapping = {}
        for i in range(len(mapping_keys)):
            mapping[mapping_keys[i]] = i
        features[key] = features[key].map(mapping)
    elif key.endswith("real_trace2.entry_api"):
        print("Cut and Mapping: " + key)
        features[key] = features[key].map(cut_api)
    elif key.endswith("_inst_cpu"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-1]  # 截取字符串提取数字，7m → 7
    elif key.endswith("_inst_memory"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-2]  # 截取字符串提取数字，403MI → 403
    elif key.endswith("_inst_node_cpu"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-1]  # 截取字符串提取数字，7m → 7
    elif key.endswith("_inst_node_memory"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-2]  # 截取字符串提取数字，22181452Ki → 22181452
    elif key.endswith("_inst_node_cpu_limit"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-1]  # 截取字符串提取数字，7m → 7
    elif key.endswith("_inst_node_memory_limit"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-2]  # 截取字符串提取数字，22181452Ki → 22181452
    elif key.endswith("_cpu"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-1]  # 截取字符串提取数字，7m → 7
    elif key.endswith("_memory"):
        print("Fetch: " + key)
        features[key] = features[key].str[:-2]  # 截取字符串提取数字，22181452Ki → 22181452
    else:
        print("Reserved:" + key)

print(features.keys())
