import sys
import math

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

n = int(input())
arr = []
for i in range(n):
    arr.append(int(input()))
arr.sort()
minn=100000
for i, val in enumerate(arr):
    if(i==0):
        continue
    diff=val-arr[i-1]
    if (diff<minn):
        minn=diff
print(minn)
