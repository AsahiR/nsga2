import linecache
from matplotlib import pyplot as plt
import sys
from typing import Tuple


# parse indv, indv is searchRank then input its evalVector into aXis list
def parseIndividual(src: str, lineNo: int, xAxis: list, yAxis: list, searchRank = None):
    rank = int(linecache.getline(src, lineNo))
    # no need to parse
    lLineNo  = lineNo
    if(searchRank and rank != searchRank):
        lineNo += indvLines
        return lLineNo
    # parse indv and add into x, y
    # rank, dist, eval, {row, column, vec}*3
    lLineNo += 3
    row = int(linecache.getline(src, lLineNo))
    # dim must be 2
    assert row == 2
    lLineNo += 2
    evlVecLine  = linecache.getline(src, lLineNo)
    x, y =  parseVec(evlVecLine)
    xAxis.append(x)
    yAxis.append(y)
    lineNo += indvLines
    return lineNo

def parseVec(line:str) -> Tuple:
    delimitter = ' '
    vec = line.split(delimitter)
    return (float(vec[0]), float(vec[1]))


# program start hera
# *.py srcName destName (frontRank)

lineNo = 1
xAxis = []
yAxis = []

srcDir = 'popLog/'
destDir = ''

src = srcDir + sys.argv[1]
dest = destDir + sys.argv[2]
searchRank = None
if(len(sys.argv) > 3):
    # you can input searchRank in 3rd argument
    searchRank = int(sys.argv[3])

indvLines = 12
headLine = linecache.getline(src, lineNo)
popSize = int(headLine)
lineNo = lineNo + 1

for _ in range(popSize):
    lineNo = parseIndividual(src, lineNo, xAxis, yAxis, searchRank)

plt.scatter(xAxis, yAxis, marker = 'o')
plt.xlabel("f1 axis")
plt.ylabel("f2 axis")
plt.grid(True)
plt.savefig(dest)
