import backtrader as bt
import pandas as pd
from datetime import datetime, timedelta
import time

import requests
import tushare as ts
#from utils import LoggerObserver
from time import sleep
import threading
from queue import Queue
from collections import OrderedDict

from backtrader import Analyzer, TimeFrame, Position
from backtrader.mathsupport import average, standarddev
from backtrader.analyzers import TimeReturn, AnnualReturn
from backtrader.utils.py3 import itervalues
import math

class RealTimeData(bt.feeds.DataBase):
    params = (("interval",1),
              ("test",True),
              ('timeframe',bt.dataseries.TimeFrame.Minutes),
              ('compression',1)
              )
    def __init__(self, symbol,stop_event):
        super(RealTimeData,self).__init__()
        self._name:str = symbol  # 股票代码
        self.last_time=None  #上一次获取数据的时间
        self.stop_event = stop_event  # 停止事件标志
        if self.params.test:
            self.generator = mock_data_generator()
        


    def stop(self):
        print("数据源停止")
        pass

    def _load(self):
        #print("调用了_load:"+str(datetime.today()))
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
                #遇到错误则继续等待,否则跳出循环返回数据
                break
            except Exception as e:
                print(f"获取实时行情失败{e},5秒后重试")
                time.sleep(5)
            
        if self.stop_event.is_set():
            return False  # 如果停止事件被设置，返回False

        latest_price = float(df['PRICE'][0])
        # 写入数据到line
        date_str = str(df['DATE'][0])+' '+str(df['TIME'][0])
        #print("dateTime:"+date_str)
        self.lines.datetime[0] = bt.date2num(datetime.strptime(date_str,"%Y%m%d %H:%M:%S"))
        self.lines.open[0] = latest_price
        self.lines.high[0] = latest_price
        self.lines.low[0] = latest_price
        self.lines.close[0] = latest_price
        self.lines.volume[0] = df['VOLUME'][0]

        #self._save2stack(erase=True)
        #self._laststatus = self.CONNECTED
        time.sleep(0.2)
        return True



class MyStrategy(bt.Strategy):
    params = (
        # 单均线策略周期长度
        ('maperiod', 5),
    )    
    def __init__(self):
        # 指定价格序列
        self.dataclose = self.datas[0].close
        self.sma = bt.indicators.MovingAverageSimple(self.datas[0], period=self.params.maperiod)
    
    def next(self):
        print("策略执行中,当前数据点收盘价:"+str(self.data.close[0])+" sma:"+str(self.sma[0]))
        if not self.position:
            if self.data.close[0] > self.sma[0]:
                print(f"买入信号: {self.data.datetime.date(0)}")
                self.buy()
        else:
            if self.data.close[0] < self.sma[0]:
                print(f"卖出信号: {self.data.datetime.date(0)}")
                self.sell()

class DynamicAnnualReturn(bt.Analyzer):
    def __init__(self):
        self.cur_year = -1
        self.value_start = 0.0
        self.value_cur = 0.0
        self.value_end = 0.0
        self.rets = list()
        self.ret = OrderedDict()

    def next(self):
        # 动态计算年化收益率
        dt = self.data.datetime.date(0)  # 获取当前时间
        self.value_cur = self.strategy.broker.getvalue()  # 当前账户价值
        
        if dt.year > self.cur_year:
            if self.cur_year >= 0:
                # 计算上一年的收益率
                annualret = (self.value_end / self.value_start) - 1.0
                self.rets.append(annualret)
                self.ret[self.cur_year] = annualret

                # 新的一年开始，重置起始值
                self.value_start = self.value_end
            else:
                # 初始化第一年的起始值s
                self.value_start = self.value_cur
            
            self.cur_year = dt.year

        # 更新当前年的最后价值
        self.value_end = self.value_cur

    def get_analysis(self):
        # 计算当前年份的收益率
        annualret = (self.value_end / self.value_start) - 1.0
        #print("start:",self.value_start," end:",self.value_end," annualret:",annualret)
        #结果为[(年份,收益率)]
        if self.cur_year not in self.ret:
            self.rets.append(annualret)
        self.ret[self.cur_year] = annualret
        return self.ret

class DynamicSharpe(bt.Analyzer):
    params = (
        ('timeframe', TimeFrame.Minutes),
        ('compression', 1),
        ('riskfreerate', 0.01),
        ('factor', None),
        ('convertrate', True),
        ('annualize', False),
        ('stddev_sample', False),

        # old behavior
        ('daysfactor', None),
        ('legacyannual', False),
        ('fund', None),
        ('annualize', True),
    )
    RATEFACTORS = {
        TimeFrame.Days: 252,
        TimeFrame.Weeks: 52,
        TimeFrame.Months: 12,
        TimeFrame.Years: 1,
    }
    def __init__(self):
        if self.p.legacyannual:
            self.anret = AnnualReturn()
        else:
            self.timereturn = TimeReturn(
                timeframe=self.p.timeframe,
                compression=self.p.compression,
                fund=self.p.fund)
    def get_analysis(self):
       
        if self.p.legacyannual:
            rate = self.p.riskfreerate
            retavg = average([r - rate for r in self.anret.rets])
            retdev = standarddev(self.anret.rets)

            self.ratio = retavg / retdev
        else:
            # Get the returns from the subanalyzer
            returns = list(itervalues(self.timereturn.get_analysis()))

            rate = self.p.riskfreerate  #

            factor = None

            # Hack to identify old code
            if self.p.timeframe == TimeFrame.Days and \
               self.p.daysfactor is not None:

                factor = self.p.daysfactor

            else:
                if self.p.factor is not None:
                    factor = self.p.factor  # user specified factor
                elif self.p.timeframe in self.RATEFACTORS:
                    # Get the conversion factor from the default table
                    factor = self.RATEFACTORS[self.p.timeframe]

            if factor is not None:
                # A factor was found

                if self.p.convertrate:
                    # Standard: downgrade annual returns to timeframe factor
                    rate = pow(1.0 + rate, 1.0 / factor) - 1.0
                else:
                    # Else upgrade returns to yearly returns
                    returns = [pow(1.0 + x, factor) - 1.0 for x in returns]

            lrets = len(returns) - self.p.stddev_sample
            # Check if the ratio can be calculated
            if lrets:
                # Get the excess returns - arithmetic mean - original sharpe
                ret_free = [r - rate for r in returns]
                ret_free_avg = average(ret_free)
                retdev = standarddev(ret_free, avgx=ret_free_avg,
                                     bessel=self.p.stddev_sample)

                try:
                    ratio = ret_free_avg / retdev

                    if factor is not None and \
                       self.p.convertrate and self.p.annualize:

                        ratio = math.sqrt(factor) * ratio
                except (ValueError, TypeError, ZeroDivisionError):
                    ratio = None
            else:
                # no returns or stddev_sample was active and 1 return
                ratio = None

            self.ratio = ratio
        print("sharpe_ratio:",self.ratio)

        self.rets['sharperatio'] = self.ratio
        return self.rets

class CustomStatsObserver(bt.Observer):
    #alias = ('CustomStats',)  # 用于在策略中引用
    #lines = ('annual_return', 'sharpe_ratio', 'max_drawdown', 'earnings', 'earnings_rate')  # 定义观察数据线
    lines = ('earnings',)
    plotinfo = dict(plot=False)  # 是否绘图，默认关闭

    def __init__(self):
        # 初始化时获取相关的数据和分析器
        self.cash = self._owner.broker.get_cash()  # 初始资金
        self.strategy = self._owner
        self.annual_returns = None
        self.annual_return = None
        self.sharpe_ratio = None
        self.drawdown = None
        self.log = None
        self.cnt = 0

    def next(self):
        try:
            self.cnt +=1
            # 获取分析器相关数据,60分钟更新一次
            if self.cnt%60 == 0:  
                self.annual_returns = self.strategy.analyzers.AnnualReturn.get_analysis()
                self.sharpe_ratio = self.strategy.analyzers.SharpeRatio.get_analysis()["sharperatio"]
                self.drawdown = self.strategy.analyzers.DrawDown.get_analysis()["max"]["drawdown"]
            
                
                ## 计算账户收益和年化收益率
                end_funding = round(self.strategy.broker.getvalue(), 2)
                earnings = round(end_funding - self.cash, 2)
                earnings_rate = round((earnings / self.cash), 4)
                self.lines.earnings[0] = earnings  # 更新收益线
                # 计算总年化收益率
                cumulative_growth = 1
                years = 0
                for year, ret in self.annual_returns.items():
                    cumulative_growth *= (1 + ret)
                    years += 1
                self.annual_return = round(cumulative_growth ** (1 / years) - 1, 4)
            
        
        except Exception as e:
            print("Error in CustomStatsObserver:", e)


def mock_data_generator():
    df:pd.DataFrame = pd.read_csv("test/mock_minutes_data.csv")
    for index,row in df.iterrows():
        row.name=0
        yield pd.DataFrame([row])


def test():
    cerebro = bt.Cerebro()
    cerebro.addstrategy(MyStrategy)
    # 创建自定义数据源
    stock_code = '000001.SZ'

    data = RealTimeData(symbol=stock_code)
    cerebro.adddata(data)
    cash = 100000.0
    cerebro.broker.set_cash(cash)
    cerebro.broker.setcommission(commission=0.001)
    # 添加策略
    cerebro.addanalyzer(bt.analyzers.AnnualReturn,_name="AnnualReturn")
    cerebro.addanalyzer(bt.analyzers.SharpeRatio,_name="SharpeRatio")
    cerebro.addanalyzer(bt.analyzers.DrawDown,_name ="DrawDown")
    cerebro.addobserver(bt.observers.Broker)
    cerebro.addobserver(bt.observers.Trades)
    cerebro.addobserver(bt.observers.BuySell)

    cerebro.addsizer(bt.sizers.PercentSizer, percents=20)
    result = cerebro.run()
    strategy = result[0]
    end_funding = round(cerebro.broker.getvalue(),2)
    earnings = round(end_funding - cash,2)
    earnings_rate = round((earnings / cash) ,4) 
    annual_returns = strategy.analyzers.AnnualReturn.get_analysis() #返回结果为[(年份,收益率)]
    sharpe_ratio = strategy.analyzers.SharpeRatio.get_analysis()["sharperatio"]
    drawdown = strategy.analyzers.DrawDown.get_analysis()["max"]["drawdown"]


def main():
    #实时数据源下的模拟交易需要添加三个关键参数
    cerebro = bt.Cerebro( exactbars=True,stdstats=False)
    # 添加策略
    cerebro.addstrategy(MyStrategy)
    # 创建自定义数据源
    stock_code = '000001.SZ'

    stop_event = threading.Event()
    data = RealTimeData(symbol=stock_code,stop_event=stop_event)
    cerebro.adddata(data)
    cash = 100000.0
    cerebro.broker.set_cash(cash)


    main_service_url = "http://localhost:8081"  # 主服务地址
    myBroker = MyBroker(api_base_url=main_service_url, sim_id=6, data=data, frequnce="minute")
    cerebro.broker = myBroker
    
    #myBroker = MyBroker(api_base_url="http://localhost:8081",sim_id=simulationId,frequnce=frequence) #模拟账户
    #cerebro.setbroker(myBroker)

    cerebro.addanalyzer(DynamicAnnualReturn,_name="AnnualReturn")
    cerebro.addanalyzer(DynamicSharpe,_name="SharpeRatio")
    cerebro.addanalyzer(bt.analyzers.DrawDown,_name ="DrawDown")
    #erebro.addobserver(bt.observers.Broker)
    #erebro.addobserver(bt.observers.Trades)
    #erebro.addobserver(bt.observers.BuySell)
    #cerebro.addobserver(LoggerObserver)
    cerebro.addobserver(CustomStatsObserver)  # 添加自定义统计观察者
    cerebro.addsizer(bt.sizers.PercentSizer, percents=20, retint = True) #retint 表示购买数量为整数

    cerebro.broker.setcommission(commission=0.0003, margin=None, stocklike=True)  # 设置手续费,stocklike=True表示股票交易,手续费为百分比计算

    run_thread = threading.Thread(target=run_cerebro, args=(cerebro,stop_event))
    run_thread.start()
    #统计线程
    #stats_thread = threading.Thread(target=collect_statistics, args=(cerebro,cash,stop_event))
    #stats_thread.start()

    while not stop_event.is_set():
        user_input = input("Enter 's' to stop: ")  # 实时获取用户输入
        if user_input.lower() == 's':
            print("Stopping cerebro...")
            stop_event.set()  # 通知其他线程停止
            cerebro.runstop()  # 停止 cerebro
            break
    run_thread.join()
    print("Simulation stopped.")

# 定义一个函数运行cerebro
def run_cerebro(cerebro,stop_event):
    try:
        cerebro.run(runonce=False) 
    except Exception as e:
        print("Error during cerebro execution:", e)
    # 如果 cerebro 停止运行，设置停止标志
    stop_event.set()

#main()

class MyBroker(bt.brokers.BackBroker):
    def __init__(self, api_base_url, sim_id, data=None, frequnce: str = 'minute'):
        super().__init__()
        self.api_base_url = api_base_url
        self.simulation_id = sim_id
        self.order_lock = threading.Lock()
        self.pending_orders = {}
        self.frequnce = frequnce
        self.session = requests.Session()
        self.data = data

    def start(self):
        """
        启动broker时同步账户状态
        """
        super().start()
        self._sync_account_status()

    def _sync_account_status(self):
        """
        同步账户状态
        """
        print("开始同步账户状态...")
        try:
            response = self.session.get(
                f"{self.api_base_url}/trading/sync_status/{self.simulation_id}"
            )
            response.raise_for_status()  # 检查HTTP错误
            result = response.json()

            if result.get("code") == 200:
                data = result.get("data", {})
                balances = data.get("balances", {})
                self.cash = balances.get("available", self.cash)  # 更新现金
                print(f"同步现金: {self.cash}")

                # 同步持仓信息 - 假设API返回一个包含持仓对象的列表
                api_positions = data.get('positions', [])
                if isinstance(api_positions, dict):  # 如果API只返回单个仓位字典
                    api_positions = [api_positions]

                # --- 正确填充 self.positions ---
                new_positions = {}

                for pos_data in api_positions:
                    stock_code = pos_data.get("stockCode")
                    quantity = pos_data.get("quantity")
                    # 重要：需要从API获取平均持仓价格
                    avg_price = pos_data.get("costPrice")

                    if not stock_code or quantity is None or avg_price is None:
                        print(f"警告: API返回的持仓信息不完整，跳过: {pos_data}")
                        continue

                    print(f"同步持仓: symbol: {stock_code}, quantity: {quantity}, avg_price: {avg_price}")
                    # 使用 data_feed 作为键, 创建 Position 对象作为值
                    new_positions[self.data] = Position(size=quantity, price=avg_price)

                # 更新 self.positions (可以用 new_positions 替换，或者更精细地更新)
                # 替换法：假设API总是返回完整持仓
                self.positions.clear()
                self.positions.update(new_positions)
                print(f"持仓同步完成，当前持仓键: {[d._name for d in self.positions.keys()]}")
                # --- 结束填充 ---

            else:
                print(f"账户同步API请求失败: {result.get('message', '未知错误')}")

        except requests.exceptions.RequestException as e:
            print(f"账户同步网络请求失败: {str(e)}")
        except Exception as e:
            print(f"账户同步时发生错误: {str(e)}", exc_info=True)  # 打印详细错误

    def submit(self, order, **kwargs):
        """
        提交订单到远程交易系统
        """
        with self.order_lock:
            # 构建订单请求
            order_request = {
                'simulationId': self.simulation_id,
                'orderType': 'market' if order.exectype == bt.Order.Market else 'limit',
                'stockCode': order.data._name,
                'side': 'buy' if order.isbuy() else 'sell',
                'quantity': abs(order.size),
                'price': order.price if order.exectype == bt.Order.Limit else order.data.close[0],  # 使用当前收盘价作为市价单价格
                'frequency': self.frequnce
            }
            print("提交订单:", order_request)
            try:
                response = self.session.post(
                    f"{self.api_base_url}/trading/place_order",
                    json=order_request
                )
                result = response.json()
                if result["code"] == 200:
                    data = result["data"]
                    self.pending_orders[order.ref] = {
                        'order': order,
                        'remote_id': data['orderId'],
                        'status': data['status']
                    }

                    # 交易系统中订单处理成功,此处直接完成订单
                    if data['status'] == 'filled':
                        self._process_filled_order(order)
                    elif data['status'] == 'cancelled':
                        del self.pending_orders[order.ref]

                    return order
                else:
                    msg = result["message"]
                    print(f"订单提交失败:{msg}")
                    return None

            except Exception as e:
                print(f"订单提交失败: {str(e)}")
                return None

    def _process_filled_order(self, order):
        """
        处理已成交订单
        """
        exec_price = order.created.price
        exec_size = order.size
        # 确定成交时间
        exec_dt_num = self.data.datetime[0]  # 默认使用当前bar时间
        # 确保成交数量带符号
        exec_size_signed = exec_size if order.isbuy() else -exec_size

        print(f"执行订单: ref={order.ref}, dt={bt.num2date(exec_dt_num)}, size={exec_size_signed}, price={exec_price}")

        # 调用 backtrader 的 execute 方法
        try:
            super()._execute(order=order,price=exec_price,ago=0)
        except Exception as e:
            print(f"super()执行订单出错{str(e)}")
        '''order.execute(
            dt=exec_dt_num,
            size=exec_size_signed,  # 使用带符号的成交量
            price=exec_price,
        )


        order.completed()'''
        print(f"订单完成: ref={order.ref}")
        # 如果订单完成，从待处理列表中移除
        if order.ref in self.pending_orders:
            del self.pending_orders[order.ref]


        # 通知策略订单状态变化
        self.notify(order)

        # 不建议在此处立即调用 _sync_account_status()
        # order.execute() 应该已经更新了 backtrader 内部的 cash 和 position
        # self._sync_account_status()

    def _check_pending_orders(self):
        """
        检查待处理订单状态
        """
        with self.order_lock:
            for order_ref, order_info in list(self.pending_orders.items()):
                if order_info['status'] == 'pending':
                    try:
                        # 获取交易系统中订单信息
                        response = self.session.get(
                            f"{self.api_base_url}/trading/{order_info['remote_id']}/status"
                        )
                        result = response.json()
                        if result["code"] == 200:
                            order = result["data"]
                            status = order["status"]
                            if status == 'filled':
                                self._process_filled_order(order_info['order'], order["price"])
                                del self.pending_orders[order_ref]
                            elif status == 'cancelled':
                                order_info['order'].cancel()
                                del self.pending_orders[order_ref]
                            else:
                                print(f"订单状态错误:{status}")

                    except Exception as e:
                        print(f"订单状态查询失败: {str(e)}")

    def next(self):
        """
        在每个bar更新时检查订单状态并调用父类next
        """
        # 先检查外部订单状态，处理完成、取消等
        self._check_pending_orders()

        # 然后调用父类的next，让BackBroker处理其内部逻辑
        # 此时 self.positions 应该是正确的格式，_get_value 不会报错
        try:
            super().next()
        except Exception as e:
            print(f"调用 super().next() 时出错: {e}", exc_info=True)  # 捕获并打印父类next的错误

        # 可以在这里添加其他逻辑，例如定期（而非每次）同步账户状态
        # if len(self.data) % 60 == 0: # 例如每60个bar同步一次
        #     self._sync_account_status()

main()