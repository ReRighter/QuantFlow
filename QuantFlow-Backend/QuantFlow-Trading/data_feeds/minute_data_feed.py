import backtrader as bt
from datetime import datetime
import tushare as ts
import time    
import pandas as pd

class MinuteRealTimeData(bt.feeds.DataBase):
    params = (("interval",60),
              ("test",True),  #是否使用测试数据
              ('timeframe',bt.dataseries.TimeFrame.Minutes),
              ('compression',1)
              )
    def __init__(self, symbol,stop_event):
        super(MinuteRealTimeData,self).__init__()
        self.symbol:str = symbol  # 股票代码
        self._name = symbol  # 数据源名称
        self.last_time=None  #上一次获取数据的时间
        self.stop_event = stop_event  # 停止事件
        if self.params.test:
            # 测试环境下使用假数据
            self.generator = mock_data_generator()

    def stop(self):
        print("数据源停止")
        pass

    def _load(self):
        while not self.stop_event.is_set():
            try:
                if self.params.test:
                    #测试环境下使用假数据
                    df = next(self.generator)
                else:
                    df = ts.realtime_quote(self.symbol)
                #break
                current_time = str(df['TIME'][0]) #时间点,例如15:00:00
                if current_time == self.last_time:
                    #如果数据未更新, 则暂停等待下一次更新
                    time.sleep(self.params.interval)
                    continue
                self.last_time = current_time
                #遇到错误则继续等待,否则跳出循环处理数据
                break
            except Exception as e:
                print(f"获取实时行情失败{e},5秒后重试")
                time.sleep(5)
        if self.stop_event.is_set():
            return False  # 如果停止事件被设置，返回False
        latest_price = float(df['PRICE'][0])
        # 写入数据到line
        date_str = str(df['DATE'][0])+' '+str(df['TIME'][0])
        self.lines.datetime[0] = bt.date2num(datetime.strptime(date_str,"%Y%m%d %H:%M:%S"))
        self.lines.open[0] = latest_price
        self.lines.high[0] = latest_price
        self.lines.low[0] = latest_price
        self.lines.close[0] = latest_price
        self.lines.volume[0] = df['VOLUME'][0]

        time.sleep(0.5)
        return True
    
def mock_data_generator():
    df:pd.DataFrame = pd.read_csv("test/mock_minutes_data.csv")
    for index,row in df.iterrows():
        row.name=0
        yield pd.DataFrame([row])