import backtrader as bt

class SMA(bt.Strategy):
    params = (
        # 单均线策略周期长度
        ('maperiod', 20),
    )

    def __init__(self):
        # 指定价格序列
        self.dataclose = self.datas[0].close
        self.order = None
        self.sma = bt.indicators.MovingAverageSimple(self.datas[0], period=self.params.maperiod)
    def notify_order(self, order):
        if order.status in [order.Submitted, order.Accepted]:
            # 订单已提交或接受，等待执行
            return
        if order.status in [order.Canceled, order.Margin, order.Rejected]:
            print(f'订单失败 - 日期: {self.data.datetime.date(0)}, 价格: {self.data.close[0]:.2f}')
        self.order = None
    def next(self):
        #当前有订单未完成时,避免操作
        if self.order:
            return

        if not self.position:
            if self.dataclose[0] > self.sma[0]:
                # 买入
                self.order = self.buy()
        else:
            if self.dataclose[0]<self.sma[0]:
                #全部卖出
                self.order= self.close()