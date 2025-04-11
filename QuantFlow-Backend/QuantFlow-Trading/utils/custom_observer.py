import backtrader as bt

class LoggerObserver(bt.Observer):
    """
    自定义观察者类，用于记录策略执行过程中的日志信息
    """
    lines = ('log',)

    def __init__(self):
        self.log = []

    def next(self):
        # 遍历当前策略的所有订单
        for order in self._owner._orderspending:
            if order.status == order.Completed:
                if order.isbuy():
                    self.log.append(f"{self.data.datetime.date(0)} - BUY EXECUTED, Price: {order.executed.price:.2f}, Cost: {order.executed.value:.2f}, Comm: {order.executed.comm:.2f}")
                elif order.issell():
                    self.log.append(f"{self.data.datetime.date(0)} - SELL EXECUTED, Price: {order.executed.price:.2f}, Cost: {order.executed.value:.2f}, Comm: {order.executed.comm:.2f}")
              

    def start(self):
        # 初始化日志列表
        self.log = []

    def stop(self):
        # 输出日志信息
        for entry in self.log:
            print(entry) 