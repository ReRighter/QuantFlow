import backtrader as bt

class BollingerBandsBreakout(bt.Strategy):
    params = (
        ('period', 20),  # 布林带的周期
        ('devfactor', 2),  # 标准差的倍数
    )

    def __init__(self):
        # 初始化布林带指标
        self.bollinger = bt.indicators.BollingerBands(
            self.data.close, period=self.params.period, devfactor=self.params.devfactor)

    def next(self):
        # 如果没有持仓，检查是否触发买入条件
        if not self.position:
            if self.data.close < self.bollinger.lines.bot:  # 价格低于下轨
                self.buy()  # 买入

        # 如果有持仓，检查是否触发卖出条件
        else:
            if self.data.close > self.bollinger.lines.top:  # 价格高于上轨
                self.sell()  # 卖出