import pandas as pd
from pandas import DataFrame
from datetime import datetime,timedelta

df:DataFrame = pd.read_csv("test/000001.SZ_20240101_20241231.csv", index_col=0)
df.drop(['trade_date','ts_code','pre_close','change','pct_chg','amount'],axis=1,inplace=True)
df.rename(columns={
    'vol':'VOLUME',
    'close':'PRICE'
},inplace=True)
len = len(df)

def generate_time_sequence(start_time,interval):
    current_time = start_time
    while True:
        yield current_time
        current_time += interval

#start_time = datetime.strptime("20250418 09:00","%Y%m%d %H:%M")
start_time = datetime.strptime("09:00:00","%H:%M:%S")
interval = timedelta(minutes=1)
generator = generate_time_sequence(start_time,interval)


df["DATE"] = "20250418"

TIME = []
for index,row in df.iterrows():
    TIME.append(next(generator).strftime("%H:%M:%S"))
df["TIME"] = TIME
df.to_csv("test/mock_minutes_data.csv",index=False)
