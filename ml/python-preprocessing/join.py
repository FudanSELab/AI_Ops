import pandas as pd

input_trace_path = "input/sample_trace.csv"
input_trace_index_col_name = "real_trace2.trace_id"

input_seq_path = "input/sample_seq.csv"
input_seq_index_col_name = "seq_final.trace_id"

output_after_join_path = "transform/sample_after_join.csv"


# Read Two File with assigned index column.
data_trace = pd.read_csv(input_trace_path,
                         header=0,
                         index_col=input_trace_index_col_name)

print(input_trace_path, "has", len(data_trace.keys()), "columns")

data_seq = pd.read_csv(input_seq_path,
                       header=0,
                       index_col=input_seq_index_col_name)

print(input_seq_path, "has", len(data_seq.keys()), "columns")

# Join two table by index_col with inner join.
data_join = data_trace.join(data_seq,
                            how="inner")

print(output_after_join_path, "has", len(data_join.keys()), "columns")

# Output the result file.
data_join.to_csv(output_after_join_path)

