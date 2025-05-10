import pandas as pd
import backtrader as bt
import tushare as ts
from datetime import datetime


#token仅需设置一次
#tushareToken="32f21ba37fc15213e2e71c32277a58b381f1fb14543ab206dcbec0e4"
#ts.set_token(tushareToken)

#pro =ts.pro_api()
#data:pd.DataFrame = pro.stock_basic(exchange='', list_status='L', fields='ts_code,symbol,name,area,industry,list_date')
#data.to_csv('stocks.csv')

#df:pd.DataFrame = pd.read_csv("../datas/daily/000001.SZ_20240101_20241231.csv", index_col=0)
##按时间升序
#df.sort_values(by='trade_date',inplace=True)
#df.to_csv("000001.SZ_20240101_20241231.csv")

dict = {("2021",0.01),("2022",0.02),("2023",0.03)}
cumulative_growth = 1
for ret in dict.value():
    cumulative_growth *= (1+ret)
years = len(dict)
annual_return = cumulative_growth ** (1/years) -1
