import pandas as pd

from sklearn.ensemble import RandomForestClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
import preprocessing_set
import calculation
import numpy as np
from sklearn.utils import shuffle



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
        clf_le = RandomForestClassifier(min_samples_leaf=6000, n_estimators=3)
    elif ml_name == "knn":
        print("Big Model", "LE", "KNN")
        clf_le = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "LE", "MLP")
        clf_le = MLPClassifier(hidden_layer_sizes=[5, 5], max_iter=200)
    clf_le.fit(le_train_x, le_train_y)
    print("LE Model训练完毕")

    # Train MS Model
    print("MS Model训练开始")
    y_ms = "y_issue_ms"
    df_fault_all_ms = pd.read_csv(fault_file_path, header=0, index_col="trace_id")

    df_fault_all_ms["y_issue_ms"] = df_fault_all_ms["y_issue_ms"].str.lower()
    df_fault_all_ms["y_issue_dim_type"] = df_fault_all_ms["y_issue_dim_type"].str.lower()

    df_fault_all_ms = df_fault_all_ms.loc[df_fault_all_ms["y_final_result"] == 1]
    df_fault_all_ms.pop("y_final_result")
    df_fault_all_ms.pop("y_issue_dim_type")
    df_fault_all_ms.pop("trace_api")
    df_fault_all_ms.pop("trace_service")
    ms_train_x, ms_train_y = preprocessing_set.convert_y_multi_label_by_name(df_fault_all_ms, y_ms)
    if ml_name == "rf":
        print("Big Model", "MS", "RF")
        clf_ms = RandomForestClassifier(min_samples_leaf=1200, n_estimators=5)
    elif ml_name == "knn":
        print("Big Model", "MS", "KNN")
        clf_ms = KNeighborsClassifier(n_neighbors=10)
    else:
        print("Big Model", "MS", "MLP")
        clf_ms = MLPClassifier(hidden_layer_sizes=[5, 5], max_iter=200)
    clf_ms.fit(X=ms_train_x, y=ms_train_y)
    print("MS Model训练结束")

    # Train FT Model
    print("FT Model训练开始")
    y_ft = "y_issue_dim_type"
    df_fault_all_ft = pd.read_csv(fault_file_path, header=0, index_col="trace_id")

    df_fault_all_ft["y_issue_ms"] = df_fault_all_ft["y_issue_ms"].str.lower()
    df_fault_all_ft["y_issue_dim_type"] = df_fault_all_ft["y_issue_dim_type"].str.lower()

    df_fault_all_ft = df_fault_all_ft.loc[df_fault_all_ft["y_final_result"] == 1]
    df_fault_all_ft.pop("y_final_result")
    df_fault_all_ft.pop("y_issue_ms")
    df_fault_all_ft.pop("trace_api")
    df_fault_all_ft.pop("trace_service")
    ft_train_x, ft_train_y = preprocessing_set.convert_y_multi_label_by_name(df_fault_all_ft, y_ft)
    if ml_name == "rf":
        print("Big Model", "FT", "RF")
        clf_ft = RandomForestClassifier(min_samples_leaf=1500, n_estimators=3)
    elif ml_name == "knn":
        print("Big Model", "FT", "KNN")
        clf_ft = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "FT", "MLP")
        clf_ft = MLPClassifier(hidden_layer_sizes=[5, 5], max_iter=200)
    clf_ft.fit(X=ft_train_x, y=ft_train_y)
    print("FT Model训练结束")

    # Train Model_2
    print("Model2 Model训练开始")
    y_model2 = "issue_type"
    df_model2_all = pd.read_csv(model_2_file_path, header=0, index_col=None)
    df_model2_all.pop("issue_ms")
    df_model2_all.pop("trace_id")
    df_model2_all.pop("test_trace_id")
    df_model2_all.pop("final_result")
    df_model2_all = preprocessing_set.sampling(df_model2_all, y_model2)
    model2_train_x, model2_train_y = preprocessing_set.convert_y_multi_label_by_name(df_model2_all, y_model2)
    if ml_name == "rf":
        print("Big Model", "MODEL_2", "RF")
        clf_model2 = RandomForestClassifier(min_samples_leaf=500, n_estimators=3)
    elif ml_name == "knn":
        print("Big Model", "MODEL_2", "KNN")
        clf_model2 = KNeighborsClassifier(n_neighbors=200)
    else:
        print("Big Model", "MODEL_2", "MLP")
        clf_model2 = MLPClassifier(hidden_layer_sizes=[5, 5], max_iter=200)
    clf_model2.fit(X=model2_train_x, y=model2_train_y)
    print("Model2 Model训练结束")

    print("四个小模型训练完成，开始进行测试集读取，每条测试集需要抽取")

    # ======================预测部分
    # 用来储存最终德结果集
    le_test_result = []
    ms_test_result = []
    ft_test_result = []

    # 读入测试数据，并分离出真实的final_result,ms和dim_type
    df_test_trace = pd.read_csv(test_trace_file_path, header=0, index_col=0)

    # df_test_trace = preprocessing_set.sampling(df_test_trace, "y_issue_ms")
    # df_test_trace = shuffle(df_test_trace)
    # print("测试集维度分布", df_test_trace["y_issue_dim_type"].value_counts())

    df_test_trace["y_issue_ms"] = df_test_trace["y_issue_ms"].str.lower()
    df_test_trace["y_issue_dim_type"] = df_test_trace["y_issue_dim_type"].str.lower()

    real_ms = df_test_trace.pop("y_issue_ms")

    df_test_trace = df_test_trace.loc[(df_test_trace["y_final_result"] == 1)]
    df_test_trace, real_dim_type = preprocessing_set.convert_y_multi_label_by_name(df_test_trace, "y_issue_dim_type")
    df_test_trace, real_result = preprocessing_set.convert_y_multi_label_by_name(df_test_trace, "y_final_result")
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
    count_top1 = 0
    count_top3 = 0
    count_top5 = 0

    # 记录所有Trace的Index以便后=后续进行记录和提取
    indexs = df_test_trace.index.tolist()
    spans_indexs = df_test_spans["trace_id"].tolist()

    for temp_trace_index in indexs:
        # if model_2_count+model_1_count >= 2000:
        #     break
        # print("==第", str(model_2_count+model_1_count))
        # 抽出测试集中的一条Trace
        temp_trace = df_test_trace.loc[temp_trace_index, :]
        temp_trace = [temp_trace]
        # 预测这个Trace故障与否以及结果的置信度
        temp_trace_result = clf_le.predict(temp_trace)
        temp_trace_proba = clf_le.predict_proba(temp_trace)
        # 如果置信度不符合预期，则进行Model_2预测，否则使用Model_1现有的模型预测
        # todo 这里还要检查一下对应的trace-id到底在span集合里存不存在。不存在的话还是要使用trace模型
        # print("temp-trace-result:", temp_trace_result)
        # print("temp-trace-proba:", temp_trace_proba)
        # [注意] mlp的置信度输出和别人不太一样 mlp是[0.2 0.8] 别人[[0.1,0.9],[0.8,0.2]]
        if spans_indexs.__contains__(temp_trace_index)\
                and (temp_trace_result[0][0] == 0 and temp_trace_result[0][1] == 1 and temp_trace_proba[1][0][1] < 0.1) \
                or (temp_trace_result[0][0] == 1 and temp_trace_result[0][1] == 0 and temp_trace_proba[0][0][1] < 0.1):
        # [注意]MLP的if用下面这行
        # if spans_indexs.__contains__(temp_trace_index)\
        #         and (temp_trace_result[0][0] == 0 and temp_trace_result[0][1] == 1 and temp_trace_proba[0][1] < 0.1) \
        #         or (temp_trace_result[0][0] == 1 and temp_trace_result[0][1] == 0 and temp_trace_proba[0][0] < 0.1):

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
                # print("temp_span_result", temp_span_result)
                # print("temp_span_proba", temp_span_proba)
                span_set_dim_result_collect.append(temp_span_result[0])
                # [注意] 下面这行是MLP专用
                # span_set_dim_confidence_collect.append([
                #     [1 - temp_span_proba[0][0], temp_span_proba[0][0]],
                #     [1 - temp_span_proba[0][1], temp_span_proba[0][1]],
                #     [1 - temp_span_proba[0][2], temp_span_proba[0][2]]
                # ])
                # 下面这个是对一般算法
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
            model_2_count += 1
        else:

            # if temp_trace_result[0][0] == 0 and temp_trace_result[0][1] == 1:
            #     le_test_result.append(temp_trace_result[0])
            #     ms_test_result.append(np.zeros(42))
            #     ft_test_result.append([0, 0, 0])
            # else:
            ms_pred_result = clf_ms.predict(temp_trace)
            ms_proba = clf_ms.predict_proba(temp_trace)

            # print("ms_proba[0]", ms_proba)
            # print("real_ms[(model_2_count+model_1_count)]",real_ms[(model_2_count+model_1_count)])

            # [注意]RF.KNN专用
            ms_proba = convert_to_proba_list(ms_proba)
            top1, top3, top5 = tryTopKMS(ms_proba, real_ms[(model_2_count+model_1_count)])
            # [注意]MLP专用
            # top1, top3, top5 = tryTopKMS(ms_proba[0], real_ms[(model_2_count+model_1_count)])

            if top1:
                count_top1 += 1
            if top3:
                count_top3 += 1
            if top5:
                count_top5 += 1

            ft_pred_result = clf_ft.predict(temp_trace)
            le_test_result.append(temp_trace_result[0])
            # ms_test_result.append(ms_pred_result[0])
            ft_test_result.append(ft_pred_result[0])

            model_1_count += 1

    # 输出结果并计算Precision, Recall与F1值
    print("使用Model1", model_1_count, "使用Model2", model_2_count)
    calculation.calculate_a_p_r_f(real_dim_type, ft_test_result, 3)
    calculation.calculate_a_p_r_f(real_result, le_test_result, 2)

    print("Top1:", count_top1/(model_1_count+model_2_count))
    print("Top3:", count_top3/(model_1_count+model_2_count))
    print("Top5:", count_top5/(model_1_count+model_2_count))


# 检查某次预测中，前K个预测的微服务中有没有目标微服务
def tryTopKMS(probaList, svcName):
    max_num_index_list_1 = np.argpartition(probaList, -1)[-1:]
    max_num_index_list_3 = np.argpartition(probaList, -3)[-3:]
    max_num_index_list_5 = np.argpartition(probaList, -5)[-5:]
    # max_num_index_list_1 = map(probaList.index, heapq.nlargest(3, probaList))
    # max_num_index_list_3 = map(probaList.index, heapq.nlargest(3, probaList))
    # max_num_index_list_5 = map(probaList.index, heapq.nlargest(3, probaList))
    svcIndex = preprocessing_set.service_index_map.get(svcName)
    top1_contains = list(max_num_index_list_1).__contains__(svcIndex)
    top3_contains = list(max_num_index_list_3).__contains__(svcIndex)
    top5_contains = list(max_num_index_list_5).__contains__(svcIndex)
    return top1_contains, top3_contains, top5_contains


def prepare_data_for_big_model():
    for i in range(0,9):
        train_file = "evaluation_2/evaluation_total_part" + str(i) + "_added.csv"
        big_model(tf_file_path=train_file,
                  fault_file_path="fault_without_sampling.csv",
                  # fault_file_path=train_file,
                  model_2_file_path="ts_model2_total.csv",
                  # test_trace_file_path="fault_without_sampling.csv",
                  # test_trace_file_path="evaluation_2/evaluation_total_part0.csv",
                  test_trace_file_path="evaluation_2/evaluation_total_part9.csv",
                  test_spans_file_path="ts_model2_total.csv",
                  ml_name="rf")
    # big_model(tf_file_path="sockshop_data/ss_total_train.csv",
    #           fault_file_path="sockshop_data/ss_fault_train.csv",
    #           model_2_file_path="ss_model2_total.csv",
    #           test_trace_file_path="sockshop_data/ss_total_test.csv",
    #           test_spans_file_path="ss_model2_total.csv",
    #           ml_name="mlp")


def convert_to_proba_list(raw_proba):
    new_proba = []
    raw_proba_len = len(raw_proba)
    for i in range(raw_proba_len):
        if len(raw_proba[i][0]) == 1:
            new_proba.append(0.0)
        else:
            new_proba.append(raw_proba[i][0][1])
    return new_proba


if __name__ == "__main__":
    # df_fault = pd.read_csv("sockshop_data/ss_fault.csv", header=0, index_col="trace_id")
    # df_fault = preprocessing_set.sampling(df_fault, "y_issue_dim_type")
    # df_fault_test, df_fault_train = preprocessing_set.split_data(df_fault, 0.2)
    # df_fault_test.to_csv("sockshop_data/ss_fault_test.csv")
    # df_fault_test.to_csv("sockshop_data/ss_fault_train.csv")
    #
    # df_total = pd.read_csv("sockshop_data/ss_total.csv", header=0, index_col="trace_id")
    # df_total_test, df_total_train = preprocessing_set.split_data(df_total, 0.5)
    # df_total_test.to_csv("sockshop_data/ss_total_test.csv")
    # df_total_test.to_csv("sockshop_data/ss_total_train.csv")
    prepare_data_for_big_model()