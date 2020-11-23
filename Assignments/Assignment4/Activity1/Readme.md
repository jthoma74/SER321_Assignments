=============
Activity 1
=============

Task 1:
gradle runSimpleServer -Pnumber=8000
client --> One client runs telnet

Task 2: 
gradle runThreadedServer -Pnumber=8001
client --> multiple telnet windows

Task 3:
gradle runThreadPoolServer -Pnumber=8002 -Pthreads=3
client --> limited by the number of threads passed into the args (3)