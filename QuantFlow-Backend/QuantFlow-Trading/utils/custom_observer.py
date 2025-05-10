import backtrader as bt
import requests


class LoggerObserver(bt.Observer):
    """
    自定义观察者类，用于记录策略执行过程中的日志信息
    """
    lines = ('log',)

    def __init__(self):
        self.log = {}

    def next(self):
        # 遍历当前策略的所有订单
        #print("logger: _orderspending:",self._owner._orderspending)
        for order in self._owner._orderspending:
            if order.status == order.Completed:
                if order.isbuy():
                    self.log[order.ref] = f"{bt.num2date(self.data.datetime[0])} - 买入执行,价格: {order.executed.price:.2f}, 花费: {order.executed.value:.2f}, 手续费: {order.executed.comm:.2f}"
                    print(order.ref," ",self.log[order.ref])
                elif order.issell():
                    self.log[order.ref] = f"{bt.num2date(self.data.datetime[0])} - 卖出执行,价格: {order.executed.price:.2f}, 花费: {order.executed.value:.2f}, 手续费: {order.executed.comm:.2f}"
                    print(order.ref, " ", self.log[order.ref])


    def getLog(self):
        return list(self.log.values())
    def clearLog(self):
        self.log.clear()



    

#模拟交易专用, 计算统计数据
class StatsObserver(bt.Observer):
    lines = ('value',)
    plotinfo = dict(plot=False)  # 是否绘图，默认关闭

    def __init__(self,api_base_url,simulation_id):
        # 初始化时获取相关的数据和分析器
        self.api_base_url = api_base_url
        self.simulation_id = simulation_id
        self.cash = self._owner.broker.get_cash()  # 初始资金
        self.strategy = self._owner
        self.annual_returns = None
        self.annual_return = None
        self.sharpe_ratio = None
        self.drawdown = None
        self.earnings = None
        self.earnings_rate = None
        self.log = []
        self.cnt = 0

    def next(self):
        try:
            self.cnt += 1  # 每次调用next()时增加计数
            # 获取分析器相关数据,60分钟更新一次
            if self.cnt%60 == 0:  
                self.annual_returns = self.strategy.analyzers.AnnualReturn.get_analysis()
                self.sharpe_ratio = self.strategy.analyzers.SharpeRatio.get_analysis()["sharperatio"]
                self.drawdown = self.strategy.analyzers.DrawDown.get_analysis()["max"]["drawdown"]
                self.log= self.strategy.observers[0].getLog()  # 获取日志信息
                self.strategy.observers[0].clearLog() #清空已有log,避免重复更新
                ## 计算账户收益和年化收益率
                end_funding = round(self.strategy.broker.getvalue(), 2)
                earnings = round(end_funding - self.cash, 2)
                earnings_rate = round((earnings / self.cash), 4)
                self.earnings = earnings
                self.earnings_rate = earnings_rate

                # 计算总年化收益率
                cumulative_growth = 1
                years = 0
                for year, ret in self.annual_returns.items():
                    cumulative_growth *= (1 + ret)
                    years += 1
                self.annual_return = round(cumulative_growth ** (1 / years) - 1, 4)

                self.updateStats()  # 更新统计数据
        except Exception as e:
            print("Error in CustomStatsObserver:", e)
    def updateStats(self):
        """
        更新统计数据
        """
        data=  {
            "simulationId": self.simulation_id,
            "value": round(self.strategy.broker.getvalue(), 2),
            "available": round(self.strategy.broker.getcash(), 2),
            "earningsRate": self.earnings_rate,
            "annualReturns": self.annual_return,
            "sharpeRatio": round(self.sharpe_ratio, 4) if self.sharpe_ratio is not None else None,
            "maxDrawdown": self.drawdown,
            "log" :self.log
        }
        try:
            print("更新统计信息:",data)
            # 将数据发送到指定的API端点
            response = requests.post(f"{self.api_base_url}/trading/update_stats", json=data)
            result =response.json()
            if result["code"] == 200:
                print("统计数据更新成功")
            else:
                msg = result["message"]
                print(f"统计数据更新失败:{msg}")
        except Exception as e:
            print(f"请求失败: {e}")