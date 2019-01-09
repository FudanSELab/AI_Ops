def transform_cpu_diff(value):
    value_new = value / 1000.0
    if value_new <= 0.1:
        return 1
    elif value_new <= 0.3:
        return 2
    elif value_new <= 0.6:
        return 3
    elif value_new > 0.6:
        return 4
    else:
        return 0


def transform_memory_diff(value):
    if value <= 200:
        return 1
    elif value <= 500:
        return 2
    elif value <= 1000:
        return 3
    elif value > 1000:
        return 4
    else:
        return 0
