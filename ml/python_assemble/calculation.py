def calculate_a_p_f_single_label(y_real, y_predict):
    real = []
    result = []
    test_num = len(y_real)

    # 0为正，1为负
    for j in range(test_num):
        if y_real[j][0] == 0 and y_real[j][1] == 1:
            real.append(1)
        else:
            real.append(0)
        if y_predict[j][0] == 0 and y_predict[j][1] == 1:
            result.append(1)
        else:
            result.append(0)
    TP = 1  # 预测为正，实际为正
    FP = 1  # 预测为正，实际为负
    TN = 1  # 预测为负，实际为负
    FN = 1  # 预测为负，实际为正
    for j in range(test_num):
        if real[j] == 1 and result[j] == 1:
            TP += 1
        elif real[j] == 0 and result[j] == 1:
            FP += 1
        elif real[j] == 0 and result[j] == 0:
            TN += 1
        else:
            FN += 1
    print(TP, FP, TN, FN)
    precision = TP / (TP + FP)
    recall = TP / (TP + FN)
    F1 = (2 * precision * recall) / (precision + recall)
    fpr = FP / (FP + TN)
    # print("故障为positive")
    print("Recall", recall, "Precision", precision, "F1", F1)
    print("FPR", fpr)


    return precision, recall, F1


def calculate_a_p_r_f(y_real, y_predict, label_num):
    if label_num == 2:
        return calculate_a_p_f_single_label(y_real, y_predict)

    test_num = len(y_real)

    total_accuracy = 0.0   # (TP+TN)/(TP+TN+FN+FP)
    total_precision = 0.0  # P = TP/ (TP+FP)
    total_recall = 0.0     # R = TP/ (TP+FN)
    total_F1 = 0.0

    valid_label_count = 0

    for i in range(label_num):
        TP = 1  # 预测为正，实际为正
        FP = 1  # 预测为正，实际为负
        TN = 1  # 预测为负，实际为负
        FN = 1  # 预测为负，实际为正

        # 0为负，1为正
        for j in range(test_num):
            if y_predict[j][i] == 1 and y_real[j][i] == 1:
                TP += 1
            elif y_predict[j][i] == 1 and y_real[j][i] == 0:
                FP += 1
            elif y_predict[j][i] == 0 and y_real[j][i] == 0:
                TN += 1
            else:
                FN += 1
        print(TP, FP, TN, FN)
        temp_precision = TP / (TP + FP)
        temp_recall = TP / (TP + FN)
        temp_f1 = (2 * temp_precision * temp_recall) / (temp_precision + temp_recall)
        total_precision += temp_precision
        total_recall += temp_recall
        print("标签", i, "Recall", temp_recall, "Precision", temp_precision, "F1:", temp_f1)
    total_precision /= label_num
    total_recall /= label_num
    total_F1 = (2 * total_precision * total_recall) / (total_precision + total_recall)
    print("总体Recall", total_recall, "总体Precision", total_precision, "F1", total_F1)
    return total_precision, total_recall, total_F1