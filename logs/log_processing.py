from pathlib import Path
import sys


def process_log(file1, file2=Path()):
    files = [file1]
    if file2 != Path():
        files.append(file2)
    count = 0
    ts = 0
    tj = 0
    for file in files:
        with open(file) as f:
            while True:
                line = f.readline()
                if not line:
                    break
                count += 1
                times = line.split()
                # ts += get ts from line
                # tj += get tj from line
    avg_ts = ts/count
    avg_tj = tj/count
    # convert to ms?
    print(f'average TS: {avg_ts}')
    print(f'average TJ: {avg_tj}')
    return


def main():
    if len(sys.argv) == 3:
        master = Path(sys.argv[1])
        if not master.exists():
            print(f'{sys.argv[1]} does not exist')
            return
        slave = Path(sys.argv[2])
        if not slave.exists():
            print(f'{sys.argv[2]} does not exist')
            return
        process_log(master, slave)
    elif len(sys.argv) == 2:
        single = Path(sys.argv[1])
        if not single.exists():
            print(f'{sys.argv[1]} does not exist')
            return
        process_log(single)
    else:
        print('Please input at least one file')


if __name__ == "__main__":
    main()