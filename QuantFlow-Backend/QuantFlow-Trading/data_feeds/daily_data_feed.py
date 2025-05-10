import backtrader as bt
from datetime import datetime
import pandas as pd
import tushare as ts
import time

def mock_data_generator():
    df:pd.DataFrame = pd.read_csv("test/mock_daily_data.csv")
    for index,row in df.iterrows():
        row.name=0
        yield pd.DataFrame([row])


class DailyRealTimeData(bt.feeds.DataBase):
    params = (("interval",1),
              ("test",True),
              ('timeframe',bt.dataseries.TimeFrame.Days),
              ('compression',1)
              )
    def __init__(self, symbol):
        super(DailyRealTimeData,self).__init__()
        self.symbol:str = symbol  # 股票代码
        self.last_time=None  #上一次获取数据的时间
        if self.params.test:
            self.generator = mock_data_generator()
        #初始化缓冲区
        self.reset()

    def start(self):
        pass
    def stop(self):
        print("数据源停止")
        pass

    def _load(self):
        print("调用了_load:"+str(datetime.today()))
        while True:
            try:
                if self.params.test:
                    #测试环境下使用假数据
                    df = next(self.generator)
                else:
                    df = ts.realtime_quote(self.symbol)
                break
            except Exception as e:
                print(f"获取实时行情失败{e},5秒后重试")
                time.sleep(5)
        current_time = str(df['TIME'][0]) #时间点,例如15:00:00
        if current_time == self.last_time:
            #如果数据未更新, 则暂停等待下一次更新
            time.sleep(self.params.interval)
            return False
        self.last_time = current_time

        latest_price = float(df['PRICE'][0])
        # 写入数据到line
        date_str = str(df['DATE'][0])+' '+str(df['TIME'][0])
        print("dateTime:"+date_str)
        self.lines.datetime[0] = bt.date2num(datetime.strptime(date_str,"%Y%m%d %H:%M:%S"))
        self.lines.open[0] = latest_price
        self.lines.high[0] = latest_price
        self.lines.low[0] = latest_price
        self.lines.close[0] = latest_price
        self.lines.volume[0] = df['VOLUME'][0]

        time.sleep(0.5)
        return True

