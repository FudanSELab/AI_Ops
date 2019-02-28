import pandas as pd


def merge():
    df1 = pd.read_csv("ts-tpds.csv", header=0, index_col=0)
    print("数量", len(df1))

    df_p1 = df1.sample(n=50000, replace=False)
    df_p1.to_csv("ts-tpds-part1.csv")
    df1.drop(df_p1.index)

    print("1")

    df_p1 = df1.sample(n=50000, replace=False)
    df_p1.to_csv("ts-tpds-part2.csv")
    df1.drop(df_p1.index)

    print("2")

    df_p1 = df1.sample(n=50000, replace=False)
    df_p1.to_csv("ts-tpds-part3.csv")
    df1.drop(df_p1.index)

    print("3")

    df_p1 = df1.sample(n=50000, replace=False)
    df_p1.to_csv("ts-tpds-part4.csv")
    df1.drop(df_p1.index)

    print("4")
    df1.to_csv("ts-tpds-part5.csv")

    # df_p1 = df1.sample(n=100000)
    # df_p1.to_csv("ts-tpds-part5.csv")
    #
    # print("5")
    #
    # df_p1 = df1.sample(n=100000)
    # df_p1.to_csv("ts-tpds-part6.csv")
    #
    # print("6")
    #
    # df_p1 = df1.sample(n=100000)
    # df_p1.to_csv("ts-tpds-part7.csv")
    #
    # print("7")
    #
    # df_p1 = df1.sample(n=100000)
    # df_p1.to_csv("ts-tpds-part8.csv")
    #
    # print("8")
    #
    # df_p1 = df1.sample(n=100000)
    # df_p1.to_csv("ts-tpds-part9.csv")
    #
    # print("9")
    #
    # df_p1 = df1.sample(n=100000)
    # df_p1.to_csv("ts-tpds-part10.csv")
    #
    # print("10")
    #
    # df_p1 = df1.sample(n=114637)
    # df_p1.to_csv("ts-tpds-part11.csv")
    #
    # print("11")


if __name__ == "__main__":
    merge()
