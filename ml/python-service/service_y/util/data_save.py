import csv


def write_to_csv(csv_path, data, header):
    """Header Example:   header = ['id', 'url', 'keywords']
       Data Example:     data = [
                                    ('1', 'http://www.xiaoheiseo.com/', 'xiaohei'),
                                    ('2', 'http://www.baidu.com/', 'baidu'),
                                    ('3', 'http://www.jd.com/', 'jingdong')
                                ]
        Usage: data_save.write_to_csv("temp.csv", data, header)
    """
    print("## Open File: " + csv_path)
    csv_file = open(csv_path, "a", newline='')
    writer = csv.writer(csv_file)
    if header is None:
        print("## No Csv_Header.")
    else:
        # print("## Csv_Header:" + header)
        writer.writerow(header)
    # print("## Csv_Content:" + data)
    writer.writerows(data)
