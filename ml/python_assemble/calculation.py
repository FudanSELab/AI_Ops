

def calculate_a_p_r_f(y_real, y_predict, label_num):
    test_num = len(y_real)

    total_accuracy = 0.0   # (TP+TN)/(TP+TN+FN+FP)
    total_precision = 0.0  # P = TP/ (TP+FP)
    total_recall = 0.0     # R = TP/ (TP+FN)
    total_F1 = 0.0

    valid_label_count = 0

    for i in range(label_num):
        TP = 0  # 预测为正，实际为正
        FP = 0  # 预测为正，实际为负
        TN = 0  # 预测为负，实际为负
        FN = 0  # 预测为负，实际为正

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
        total_precision += temp_precision
        total_recall += temp_recall
        print("标签", i, "Recall", temp_recall, "Precision", temp_precision)
    total_precision /= label_num
    total_recall /= label_num
    total_F1 = (2 * total_precision * total_recall) / (total_precision + total_recall)
    print("总体Recall", total_recall, "总体Precision", total_precision, "F1", total_F1)
    return total_precision, temp_recall, total_F1