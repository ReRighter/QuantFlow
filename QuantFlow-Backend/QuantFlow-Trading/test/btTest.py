import backtrader as bt
import pandas as pd
import matplotlib

def get_stock_data():
    df:pd.DataFrame = pd.read_csv("../datas/daily/000001.SZ_20240101_20241231.csv", index_col=0)
    #按时间升序
    df.sort_values(by='trade_date',inplace=True)
    df['trade_date'] = pd.to_datetime(df['trade_date'],format='%Y%m%d')
    df.rename(columns={
        'vol':'volume'
    }, inplace=True)
    return df


class MyStrategy(bt.Strategy):

    def next(self):
        # 策略逻辑
        print(f"当前日期: {self.data.datetime.date(0)}")
        print("当前收盘价: ", self.data.close[0])

class SMAStrategy(bt.Strategy):
    params = (
        ('fast_period', 5),  # 快速均线周期
        ('slow_period', 20),  # 慢速均线周期
    )

    def __init__(self):
        # 计算两条均线
        self.fast_sma = bt.indicators.SimpleMovingAverage(
            self.data.close, period=self.params.fast_period)
        self.slow_sma = bt.indicators.SimpleMovingAverage(
            self.data.close, period=self.params.slow_period)
        self.order_percentage = 1

        # 生成交叉信号
        self.crossover = bt.indicators.CrossOver(self.fast_sma, self.slow_sma)

    def next(self):
        # 如果没有持仓
        if not self.position:
            # 如果金叉，买入
            if self.crossover > 0:
                #每次买入10%的资金
                self.buy(size=self.broker.getcash() * self.order_percentage / self.data.close[0])
                print(f'买入信号 - 日期: {self.data.datetime.date(0)}, 价格: {self.data.close[0]:.2f}')
        # 如果已经持仓
        else:
            # 如果死叉，卖出
            if self.crossover < 0:
                #平仓
                self.sell(size=self.position.size)
                print(f'卖出信号 - 日期: {self.data.datetime.date(0)}, 价格: {self.data.close[0]:.2f}')
if __name__ == '__main__':
    cerebro = bt.Cerebro()
    cerebro.broker.setcash(100000.0)
    data_df= get_stock_data()
    data = bt.feeds.PandasData(
        dataname=data_df,
        datetime='trade_date',  # 使用日期列作为datetime
        openinterest=-1
    )
    cerebro.adddata(data)
    cerebro.addstrategy(SMAStrategy)

    print(f"Starting Portfolio value: {cerebro.broker.getvalue()}")

    cerebro.run()
    cerebro.plot(style='candlestick', volume=True)

    final_value = cerebro.broker.getvalue()
    print(f'最终资金: {final_value:.2f}')
    print(f'收益率: {(final_value - 100000) / 100000 * 100:.2f}%')