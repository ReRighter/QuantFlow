import backtrader as bt
class DualMovingAverageStrategy(bt.Strategy):
    params = (
        ('short_period', 10),  # 短期均线周期
        ('long_period', 50),  # 长期均线周期
    )

    def __init__(self):
        # 计算短期和长期均线
        self.sma_short = bt.indicators.SimpleMovingAverage(
            self.data.close, period=self.params.short_period)
        self.sma_long = bt.indicators.SimpleMovingAverage(
            self.data.close, period=self.params.long_period)

    def next(self):
        print(bt.num2date(self.data.datetime[0]),"当前价格:", self.data.close[0])
        # 如果短期均线从下向上穿越长期均线，则买入
        if not self.position:  # 没有持仓

            if self.sma_short > self.sma_long:
                print("买入信号")
                self.buy()  # 买入

        # 如果短期均线从上向下穿越长期均线，则卖出
        else:
            if self.sma_short < self.sma_long:
                print("卖出信号")
                self.sell()  # 卖出