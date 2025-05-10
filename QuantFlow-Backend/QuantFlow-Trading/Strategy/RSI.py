import backtrader as bt

class RSIStrategy(bt.Strategy):
    params = (
        ('rsi_period', 14),  # RSI 周期
        ('overbought', 70),  # 超买阈值
        ('oversold', 30),    # 超卖阈值
    )

    def __init__(self):
        # 计算 RSI 指标
        self.rsi = bt.indicators.RSI(self.data.close, period=self.params.rsi_period)

    def next(self):
        # 如果没有持仓，检查是否触发买入条件
        if not self.position:
            if self.rsi < self.params.oversold and self.rsi[-1] >= self.params.oversold:
                self.buy()  # 买入

        # 如果有持仓，检查是否触发卖出条件
        else:
            if self.rsi > self.params.overbought and self.rsi[-1] <= self.params.overbought:
                self.sell()  # 卖出