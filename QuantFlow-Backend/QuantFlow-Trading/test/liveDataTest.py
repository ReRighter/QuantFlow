import tushare as ts
import time

df = ts.realtime_quote(ts_code='000001.SZ')
print(df["PRICE"][0])
# while True:
#     df = ts.get_realtime_quotes("000001.SZ")
#     time.sleep(10)
#     print(df)