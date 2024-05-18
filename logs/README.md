# JMeter Log Processing

## JMeter TS/TJ Time Logs
  - #### Instructions of how to use the [`log_processing.py`](logs/log_processing.py) script to process the JMeter logs.
      - The times in the log files are in nanoseconds with each line containing the ts of the query and the tj of the query
      - Make sure the logs to be processed are placed in the logs directory
      - Either one or two logs can be provided as input
      - Cd into the [logs](logs) directory: `cd logs`
      - For the case for single instance run `python log_processing.py single.txt` (assuming the file name is single.txt) in the terminal
      - For the case for scaled instance run `python log_processing.py master.txt slave.txt` (assuming the files names are master.txt and slave.txt) in the terminal
      - The average TS and average TJ times will calculated from looking through the logs provided and the final results will printed out into the terminal


## JMeter TS/TJ Time Measurement Report

| [**Single-instance Version Test Plan**](logs)  | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](img/single-http-1.png)   | 34                         | 2.3827870518651832                  | 2.0868950099803665        | It seems that this case has the fastest time compared to the other cases for the single instance. This is expected because there is only 1 thread so there is not as much traffic on the site.           |
| Case 2: HTTP/10 threads                        | ![](img/single-http-10.png)  | 35                         | 2.690676658394369                   | 2.469576564840217         | The average times in this case is higher compared to the single thread case due to it having more threads so there is more traffic on the site. Since it has to serve more users at a time, the time to perform a search takes slightly more time.           |
| Case 3: HTTPS/10 threads                       | ![](img/single-https-10.png) | 52                         | 2.791398990755587                   | 2.228219503569997         | Compared to the other cases, this has the highest average query time. Http is generally faster than https due to its simplicity and https has additional steps it must do to ensure security so loading in information is slower.           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](img/single-http-np-10.png)| 37                         | 3.0634857221497707                 | 2.4360997822461163        | This is the case with the slowest average ts because of the fact that it does not have connection pooling. This makes it so it has to constantly open and close a connection every time it accesses the database which slows it down.          |

| [**Scaled Version Test Plan**](logs)           | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](img/scaled-1.png)        | 42                         | 2.1150040801285375                  | 1.8591904174354723        | Out of all the cases, this has the lowest ts/tj time because there is less load/traffic on the site.           |
| Case 2: HTTP/10 threads                        | ![](img/scaled-10.png)       | 49                         | 2.97711008777521                    | 2.7791825205936016        | Since there are more threads in this case, there is more traffic so the times are higher than the previous case, but due to the load balancing, the time are still relatively low.           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](img/scaled-np-10.png)    | 66                         | 3.2196646812145446                  | 2.1892558056436013        | This is the slowest because it does not have connection pooling meaning it has to open and close a connection each time it access the database instead of reusing an already open connection like in the previous cases.           |
