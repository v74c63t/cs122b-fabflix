# JMeter Log Processing

## Processing JMeter Logs
   1. Place the log(s) in the logs directory. Each line of the log should contain a ts and a tj of the query in nanoseconds.
   2. Move into the log directory
      
      ```
      cd logs
      ```
      
   3. Run [log_processing.py](log_processing.py) using the terminal

      - Single Instance Run (*Assuming the file name for the log is single.txt*)
      
         ```
         python log_processing.py single.txt
         ```
      
      - Scaled Instance Run (*Assuming the files names for the logs are master.txt and slave.txt*)
      
         ```
         python log_processing.py master.txt slave.txt
         ```

   4. The average TS and TJ will be calculated from the inputted log files and will be printed out to the terminal

## JMeter TS/TJ Time Measurement Report

### Single Instance Version Test

#### Case 1: HTTP - 1 Thread

**Results**

![](../img/single-http-1.png)

<table>
  <thead>
    <th>Average Query Time (ms)</th>
    <th>Average Search Servlet Time (ms)</th>
    <th>Average JDBC Time (ms)</th>
    <th>Analysis</th>
  </thead>
  <tbody>
    <td>34</td>
    <td>2.3827870518651832</td>
    <td>2.0868950099803665</td>
    <td>
      It seems that this case has the fastest time compared to the other cases for the single instance. This is expected because there is only 1 thread so there is not as much traffic on the site. 
    </td>
  </tbody>
</table>

#### Case 2: HTTP - 10 Threads

**Results**

![](../img/single-http-10.png)

<table>
  <thead>
    <th>Average Query Time (ms)</th>
    <th>Average Search Servlet Time (ms)</th>
    <th>Average JDBC Time (ms)</th>
    <th>Analysis</th>
  </thead>
  <tbody>
    <td>35</td>
    <td>2.690676658394369</td>
    <td>2.469576564840217</td>
    <td>
      The average times in this case are higher compared to the single thread case due to it having more threads so there is more traffic on the site. Since it has to serve more users at a time, the time to perform a search takes slightly more time. 
    </td>
  </tbody>
</table>

#### Case 3: HTTPS - 10 Threads

**Results**

![](../img/single-https-10.png)

<table>
  <thead>
    <th>Average Query Time (ms)</th>
    <th>Average Search Servlet Time (ms)</th>
    <th>Average JDBC Time (ms)</th>
    <th>Analysis</th>
  </thead>
  <tbody>
    <td>52</td>
    <td>2.791398990755587</td>
    <td>2.228219503569997</td>
    <td>
      Compared to the other cases, this has the highest average query time. Http is generally faster than https due to its simplicity and https has additional steps it must do to ensure security so loading in information is slower.
    </td>
  </tbody>
</table>

#### Case 4: HTTP - 10 Threads - No Connection Pooling

**Results**

![](../img/single-http-np-10.png)

<table>
  <thead>
    <th>Average Query Time (ms)</th>
    <th>Average Search Servlet Time (ms)</th>
    <th>Average JDBC Time (ms)</th>
    <th>Analysis</th>
  </thead>
  <tbody>
    <td>37</td>
    <td>3.0634857221497707</td>
    <td>2.4360997822461163</td>
    <td>
      This is the case with the slowest average ts because of the fact that it does not have connection pooling. This makes it so it has to constantly open and close a connection every time it accesses the database which slows it down.
    </td>
  </tbody>
</table>

### Scaled Version Test

#### Case 1: HTTP - 1 Thread

**Results**

![](../img/scaled-1.png)

<table>
  <thead>
    <th>Average Query Time (ms)</th>
    <th>Average Search Servlet Time (ms)</th>
    <th>Average JDBC Time (ms)</th>
    <th>Analysis</th>
  </thead>
  <tbody>
    <td>42</td>
    <td>2.1150040801285375</td>
    <td>1.8591904174354723</td>
    <td>
      Out of all the cases, this has the lowest ts/tj time because there is less load/traffic on the site. 
    </td>
  </tbody>
</table>

#### Case 2: HTTP - 10 Threads

**Results**

![](../img/scaled-10.png)

<table>
  <thead>
    <th>Average Query Time (ms)</th>
    <th>Average Search Servlet Time (ms)</th>
    <th>Average JDBC Time (ms)</th>
    <th>Analysis</th>
  </thead>
  <tbody>
    <td>49</td>
    <td>2.97711008777521</td>
    <td>2.7791825205936016</td>
    <td>
      Since there are more threads in this case, there is more traffic so the times are higher than the previous case, but due to the load balancing, the time are still relatively low.
    </td>
  </tbody>
</table>

#### Case 3: HTTP - 10 Threads - No Connection Pooling

**Results**

![](../img/scaled-np-10.png)

<table>
  <thead>
    <th>Average Query Time (ms)</th>
    <th>Average Search Servlet Time (ms)</th>
    <th>Average JDBC Time (ms)</th>
    <th>Analysis</th>
  </thead>
  <tbody>
    <td>66</td>
    <td>3.2196646812145446</td>
    <td>2.1892558056436013</td>
    <td>
      This is the slowest because it does not have connection pooling meaning it has to open and close a connection each time it access the database instead of reusing an already open connection like in the previous cases.
    </td>
  </tbody>
</table>
