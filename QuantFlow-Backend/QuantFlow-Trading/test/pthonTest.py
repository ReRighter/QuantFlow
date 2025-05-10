import pandas as pd
import backtrader as bt
from datetime import datetime

d = {}
d["1"] = "str1"
d["2"] = "str2"
d["3"] = "str3"

print(list(d.values()))

#print(datetime.strptime(str(df['DATE'][0])+' '+str(df['TIME'][0]),"%Y%m%d %H:%M:%S"))
#bt.date2num(datetime.strptime(df['DATE'][0]+' '+df['TIME'][0],"%Y%m%d %H:%M:%S"))