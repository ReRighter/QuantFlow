import backtrader as bt

class MACDStrategy(bt.Strategy):
    params = (
        ('fast_period', 12),  # 快速移动平均周期
        ('slow_period', 26),  # 慢速移动平均周期
        ('signal_period', 9),  # 信号线周期
    )

    def __init__(self):
        # 计算 MACD 指标
        self.macd = bt.indicators.MACDHisto(
            self.data.close,
            period_me1=self.params.fast_period,
            period_me2=self.params.slow_period,
            period_signal=self.params.signal_period
        )
        # 获取 MACD 线、信号线和柱状图
        self.macd_line = self.macd.macd
        self.signal_line = self.macd.signal
        self.histogram = self.macd.histo

    def next(self):
        # 如果没有持仓，检查是否触发买入条件
        if not self.position:
            if self.macd_line > self.signal_line:  # MACD 线穿越信号线
                self.buy()  # 买入

        # 如果有持仓，检查是否触发卖出条件
        else:
            if self.macd_line < self.signal_line:  # MACD 线跌破信号线
                self.sell()  # 卖出