from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import data_save


def verify_and_get_result(features):
    # Send a remote to call and get the result of the verify
    print("Send a remote call to verify the result.")
    result = '0'
    return result


def do_verify_and_save(features):
    verify_result = verify_and_get_result(features)
    features.append(verify_result)

    print("======")
    print(features)
    # Append line to csv.

    write_data = []
    write_data.append(features)

    data_save.write_to_csv(
        "../data/y3.csv",
        data=write_data,
        header=None)


def verify(feature):
    do_verify_and_save(feature)

