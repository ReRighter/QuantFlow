import tushare as ts
import pandas as pd
from datetime  import datetime
from typing import List

#pro = ts.pro_api()
#df:pd.DataFrame = pro.daily(ts_code='000001.SZ',start_date='20200101',end_date='20241231')
#df.sort_values(by='trade_date', ascending=True,inplace=True)
#df.to_csv('000001.SZ_20200101_20241231.csv')


df = ts.realtime_quote(ts_code="000001.SZ,600100.SH")
print(df)
#print(df['PRICE'])
#
#price = []
#for index,row in df.iterrows():
#    price.append(row['PRICE'])
#print(price)
#print(df.columns)
'''Index(['NAME', 'TS_CODE', 'DATE', 'TIME', 'OPEN', 'PRE_CLOSE', 'PRICE', 'HIGH',
       'LOW', 'BID', 'ASK', 'VOLUME', 'AMOUNT', 'B1_V', 'B1_P', 'B2_V', 'B2_P',
       'B3_V', 'B3_P', 'B4_V', 'B4_P', 'B5_V', 'B5_P', 'A1_V', 'A1_P', 'A2_V',
       'A2_P', 'A3_V', 'A3_P', 'A4_V', 'A4_P', 'A5_V', 'A5_P'],
      dtype='object')
'''
#print(datetime.strptime(df['DATE'][0]+' '+df['TIME'][0],"%Y%m%d %H:%M:%S"))