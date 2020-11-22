# Activity 1

**1. Explain the main structure of this code and the advantages and disadvantages of the setup of the distributed system.**
  
**2. Run the code with different arrays to sort (different sizes) and include code to measure the time for each of the sortings.  In your Readme describe your experiments and your analyzes of them. E.g. why is the result as it is?  Does the distribution help? Why, why not? See this as setting up your own experiment and give me a good description and evaluation, which is well structured.**

*Array Size: 14* 
| Structure | Trial 1 (ms) | Trial 2 (ms) | Trial 3 (ms) | Trial 4 (ms | Average (ms) |
| ------ | ------ | ------ | ------ | ------ | ------ |
| 1 Sorter | 67 | 73 | 65 | 65 | 67.5 | 
| 1 branch and 2 sorters | 74 | 68 | 67 | 73 | 70.5 |
| 3 branches and 4 sorters | 171 | 168 | 146 | 145 | 157.5 |

*Array Size: 35* 
| Structure | Trial 1 (ms) | Trial 2 (ms) | Trial 3 (ms) | Trial 4 (ms | Average (ms) |
| ------ | ------ | ------ | ------ | ------ | ------ |
| 1 Sorter | 81 | 85 | 84  | 82  | 83 | 
| 1 branch and 2 sorters | 126 | 127 | 126 | 127  | 126.5 |
| 3 branches and 4 sorters | 309 | 362 | 317 | 306 | 323.5 |

*Array Size: 79* 
| Structure | Trial 1 (ms) | Trial 2 (ms) | Trial 3 (ms) | Trial 4 (ms | Average (ms) |
| ------ | ------ | ------ | ------ | ------ | ------ |
| 1 Sorter | 134 | 139 | 135 | 140 | 137 | 
| 1 branch and 2 sorters | 245 | 282 | 261 | 258 | 261.5 |
| 3 branches and 4 sorters | 628 | 651 | 630 | 656 | 641.25 |

*Averages across different lengths*
| Structure | Arr Size: 14 | Arr Size: 35 | Arr Size: 79 |
| ------ | ------ | ------ | ------ |
| 1 Sorter | 67.5 | 83 | 137 | 
| 1 branch and 2 sorters | 70.5 | 126.5 | 261.5 |
| 3 branches and 4 sorters | 157.5 | 323.5 | 641.25 |

The results show that the less complex the distribution algorithm, the faster the merge sort.  I testes 3 different array lengths with 4 trials each. In all the attempts, a simple structure of 1 sorter resulted the fastest merge sort results.

**3. Experiment with the "tree" setup, what happens with more or less nodes when sorting the same array and different arrays?  When does the distribution make things better? Does it ever make things faster. As in the previous step experiment and describe your experiment and your results in detail.**

**4. Explain the traffic that you see on Wireshark. Would you say it is a lot?**

There were a lot of packets exchanged. All packets were TCP; the more complex the structure, the more packets that were exchanged to establish connections, etc... Communication overhead was significant when the structure contained more nodes.

For an array sized 79:
| Structure | Packets Exchanged |
| ------ | ------ |
| 1 Sorter | 1079 |
| 1 branch and 2 sorters | 3800 |
| 3 branches and 4 sorters | 10,247 | 


