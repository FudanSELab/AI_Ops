import pandas as pd

from sklearn.ensemble import RandomForestClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
import preprocessing_set
import calculation
import numpy as np


def big_model(tf_file_path, fault_file_path, model_2_file_path,
              test_trace_file_path, test_spans_file_path,
              ml_name):

    clf_le = None
    clf_ms = None
    clf_ft = None
    clf_model2 = None

    # Train LE Model
    print("LE Model训练开始")
    y_le = "y_final_result"
    df_tf_all = pd.read_csv(tf_file_path, header=0, index_col="trace_id")
    df_tf_all.pop("y_issue_ms")
    df_tf_all.pop("y_issue_dim_type")
    df_tf_all.pop("trace_api")
    df_tf_all.pop("trace_service")
    df_tf_all = df_tf_all.loc[(df_tf_all["y_final_result"] == 0)
                       | (df_tf_all["y_final_result"] == 1)]
    df_tf_all = preprocessing_set.sampling(df_tf_all,"y_final_result")
    le_train_x, le_train_y = preprocessing_set.convert_y_multi_label_by_name(df_tf_all, y_le)
    if ml_name == "rf":
        print("Big Model", "LE", "RF")
        clf_le = RandomForestClassifier(min_samples_leaf=3000, n_estimators=10)
    elif ml_name == "knn":
        print("Big Model", "LE", "KNN")
        clf_le = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "LE", "MLP")
        clf_le = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_le.fit(le_train_x, le_train_y)
    print("LE Model训练完毕")

    # Train MS Model
    print("MS Model训练开始")
    y_ms = "y_issue_ms"
    df_fault_all_ms = pd.read_csv(fault_file_path, header=0, index_col="trace_id")
    df_fault_all_ms.pop("y_final_result")
    df_fault_all_ms.pop("y_issue_dim_type")
    df_fault_all_ms.pop("trace_api")
    df_fault_all_ms.pop("trace_service")
    ms_train_x, ms_train_y = preprocessing_set.convert_y_multi_label_by_name(df_fault_all_ms, y_ms)
    if ml_name == "rf":
        print("Big Model", "MS", "RF")
        clf_ms = RandomForestClassifier(min_samples_leaf=300, n_estimators=10)
    elif ml_name == "knn":
        print("Big Model", "MS", "KNN")
        clf_ms = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "MS", "MLP")
        clf_ms = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_ms.fit(X=ms_train_x, y=ms_train_y)
    print("MS Model训练结束")

    # Train FT Model
    print("FT Model训练开始")
    y_ft = "y_issue_dim_type"
    df_fault_all_ft = pd.read_csv(fault_file_path, header=0, index_col="trace_id")
    df_fault_all_ft.pop("y_final_result")
    df_fault_all_ft.pop("y_issue_ms")
    df_fault_all_ft.pop("trace_api")
    df_fault_all_ft.pop("trace_service")
    ft_train_x, ft_train_y = preprocessing_set.convert_y_multi_label_by_name(df_fault_all_ft, y_ft)
    if ml_name == "rf":
        print("Big Model", "FT", "RF")
        clf_ft = RandomForestClassifier(min_samples_leaf=1200, n_estimators=10)
    elif ml_name == "knn":
        print("Big Model", "FT", "KNN")
        clf_ft = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "FT", "MLP")
        clf_ft = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_ft.fit(X=ft_train_x, y=ft_train_y)
    print("FT Model训练结束")

    # Train Model_2
    # TODO
    print("Model2 Model训练开始")
    y_model2 = "issue_type"
    df_model2_all = pd.read_csv(model_2_file_path, header=0, index_col=None)
    df_model2_all.pop("issue_ms")
    df_model2_all.pop("trace_id")
    df_model2_all.pop("test_trace_id")
    df_model2_all.pop("final_result")

    model2_train_x, model2_train_y = preprocessing_set.convert_y_multi_label_by_name(df_model2_all, y_model2)
    if ml_name == "rf":
        print("Big Model", "MODEL_2", "RF")
        clf_model2 = RandomForestClassifier(min_samples_leaf=100, n_estimators=5)
    elif ml_name == "knn":
        print("Big Model", "MODEL_2", "KNN")
        clf_model2 = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "MODEL_2", "MLP")
        clf_model2 = MLPClassifier(hidden_layer_sizes=[10, 10], max_iter=200)
    clf_model2.fit(X=model2_train_x, y=model2_train_y)
    print("Model2 Model训练结束")

    print("四个小模型训练完成，开始进行测试集读取，每条测试集需要抽取")

    # ======================预测部分
    # 用来储存最终德结果集
    le_test_result = []
    ms_test_result = []
    ft_test_result = []

    # 读入测试数据，并分离出真实的final_result,ms和dim_type
    df_test_trace = pd.read_csv(test_trace_file_path, header=0, index_col="trace_id")
    real_ms = df_test_trace.pop("y_issue_ms")
    df_test_trace = df_test_trace.loc[(df_test_trace["y_final_result"] == 1)]
    df_test_trace, real_dim_type = preprocessing_set.convert_y_multi_label_by_name(df_test_trace, "y_issue_dim_type")
    df_test_trace, real_result = preprocessing_set.convert_y_multi_label_by_name(df_test_trace, "y_final_result")
    # real_dim_type = df_test_trace.pop("y_issue_dim_type")
    # real_result = df_test_trace.pop("y_final_result")
    df_test_trace.pop("trace_api")
    df_test_trace.pop("trace_service")
    # 读入SPAN测试数据。这个与前面读入的测试数据的Index是匹配的，只是Trace拆分出的Span而已
    df_test_spans = pd.read_csv(test_spans_file_path, header=0, index_col=None)
    df_test_spans.pop("issue_type")
    df_test_spans.pop("test_trace_id")
    df_test_spans.pop("final_result")
    # 记录使用了Model_1和Model_2的数量
    model_2_count = 0
    model_1_count = 0

    # 记录所有Trace的Index以便后=后续进行记录和提取
    indexs = df_test_trace.index.tolist()
    for temp_trace_index in indexs:
        # 抽出测试集中的一条Trace
        temp_trace = df_test_trace.loc[temp_trace_index, :]
        temp_trace = [temp_trace]
        # 预测这个Trace故障与否以及结果的置信度
        temp_trace_result = clf_le.predict(temp_trace)
        temp_trace_proba = clf_le.predict_proba(temp_trace)
        # 如果置信度不符合预期，则进行Model_2预测，否则使用Model_1现有的模型预测
        # todo 这里还要检查一下对应的trace-id到底在span集合里存不存在。不存在的话还是要使用trace模型
        if (temp_trace_result[0][0] == 0 and temp_trace_result[0][1] == 1 and temp_trace_proba[1][0][1] < 0.6) \
                or (temp_trace_result[0][0] == 1 and temp_trace_result[0][1] == 0 and temp_trace_proba[0][0][1] < 0.6):
            model_2_count += 1
            # 根据Trace_id把对应的一串Span抽取出来
            spans_set = df_test_spans.loc[df_test_spans["trace_id"] == temp_trace_index]
            # 服务名不代入训练模型，但是有用，要先记录下来
            spans_set_ms_set = preprocessing_set.convert_y_multi_label_by_name(spans_set, "issue_ms")
            spans_set.pop("trace_id")
            # 准备储存这些Span的结果，以便后续转化输出
            spans_set_size = len(spans_set)
            span_set_dim_result_collect = []
            span_set_dim_confidence_collect = []
            # 执行并存储每个Span的结果
            for i in range(spans_set_size):
                temp_span = spans_set.iloc[i]
                temp_span = [temp_span]
                temp_span_result = clf_model2.predict(temp_span)
                temp_span_proba = clf_model2.predict_proba(temp_span)
                span_set_dim_result_collect.append(temp_span_result[0])
                span_set_dim_confidence_collect.append([temp_span_proba[0][0], temp_span_proba[1][0], temp_span_proba[2][0]])
            # 计算最终结果 1.计算le
            temp_trace_model2_le = True
            temp_trace_model2_fault_span_record = [] # 记录哪些span是有错误的
            for i in range(spans_set_size):
                if span_set_dim_result_collect[i][0] != 0 \
                    or span_set_dim_result_collect[i][1] != 0 \
                        or span_set_dim_result_collect[i][2] != 0:
                            temp_trace_model2_le = False
                            temp_trace_model2_fault_span_record.append(i)
            if not temp_trace_model2_le:
                # 如果一系列span有些报错了，说明整体trace有错误，需要计算结论
                le_test_result.append([0, 1])
                # 计算最终结果 2.计算Dim_Type
                temp_trace_model_2_max_index = -1
                temp_trace_model_2_max_confidence = -1.0
                for i in temp_trace_model2_fault_span_record:
                    temp_confidence = max(span_set_dim_confidence_collect[i][0][0],
                                          span_set_dim_confidence_collect[i][0][1]) \
                                      + max(span_set_dim_confidence_collect[i][1][0],
                                            span_set_dim_confidence_collect[i][1][1]) \
                                      + max(span_set_dim_confidence_collect[i][1][0],
                                            span_set_dim_confidence_collect[i][2][1])
                    if temp_confidence > temp_trace_model_2_max_confidence:
                        temp_trace_model_2_max_index = i
                        temp_trace_model_2_max_confidence = temp_confidence
                ft_test_result.append(span_set_dim_result_collect[temp_trace_model_2_max_index])
                # 计算最终结果 3.计算MS
                ms_test_result.append(spans_set_ms_set[temp_trace_model_2_max_index])
            else:
                # 如果一系列span都没有报错，说明整体trace是对的，结论中输出正确结果
                le_test_result.append([1, 0])
                ms_test_result.append(np.zeros(42))
                ft_test_result.append([0, 0, 0])
        else:
            model_1_count += 1
            ms_pred_result = clf_ms.predict(temp_trace)
            ft_pred_result = clf_ft.predict(temp_trace)
            le_test_result.append(temp_trace_result[0])
            ms_test_result.append(ms_pred_result[0])
            ft_test_result.append(ft_pred_result[0])

    # 输出结果并计算Precision, Recall与F1值
    print("使用Model1", model_1_count, "使用Model2", model_2_count)
    calculation.calculate_a_p_r_f(real_dim_type, ft_test_result, 3)
    calculation.calculate_a_p_r_f(real_result, le_test_result, 2)


def prepare_data_for_big_model():
    big_model(tf_file_path="ready_use_max_final_result.csv",
              fault_file_path="fault_without_sampling.csv",
              model_2_file_path="ts_model2_total.csv",
              test_trace_file_path="fault_without_sampling.csv",
              test_spans_file_path="ts_model2_total.csv",
              ml_name="rf")


if __name__ == "__main__":
    prepare_data_for_big_model()