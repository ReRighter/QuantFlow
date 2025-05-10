import akshare as ak
import pandas as pd
#df = ak.stock_zh_a_minute(symbol="SZ000001",period='1',adjust="qfq")
#df.to_csv("minute_bar_sz000001.csv")
#df = ak.stock_zh_a_minute(symbol="SH600751",period='1',adjust="qfq")
#print(df)

df = pd.read_csv("test/minute_bar_sz000001.csv",index_col=0)
print(df[-200:].close)

