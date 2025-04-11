import tushare as ts
import pandas as pd

pro = ts.pro_api()
df:pd.DataFrame = pro.daily(ts_code='000001.SZ',start_date='20240101',end_date='20241231')
df.to_csv('../datas/daily/000001.SZ_20240101_20241231.csv')