import backtrader as bt

#单均线策略
class MyStrategy(bt.Strategy):
    params = (('maperiod',20),)
    def __init__(self):
        #指定价格序列
        self.dataclose = self.datas[0].close
        self.order = None
        self.buyprice = None
        self.buycomm = None

        self.sma= bt.indicators.MovingAverageSimple(self.datas[0],period=self.params.maperiod)