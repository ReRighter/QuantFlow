import backtrader as bt
from .strategy_service import compile_code
from data_feeds import minute_data_feed,daily_data_feed
from utils import LoggerObserver,MyBroker,DynamicSharpe,DynamicAnnualReturn,StatsObserver
import threading
from .simulation_manager import SimulationObj
import traceback


def start_live_trading(stock_code:str,simulationId:int,strategy_code:str,frequency:str)->SimulationObj:
    if strategy_code is None or strategy_code == "":
        with open("Strategy/DMA.py", "r",encoding='utf-8') as file:
            strategy_code = file.read()
            print("策略代码为空,使用默认策略")
        #raise ValueError("策略代码不能为空")
    strategy_class = compile_code(strategy_code)

    #实时数据源需要添加关键参数
    cerebro  = bt.Cerebro(exactbars = True,stdstats=False)
    cerebro.addstrategy(strategy = strategy_class)
    stop_event = threading.Event() #创建一个停止事件

    if frequency == "minute":
        data = minute_data_feed.MinuteRealTimeData(symbol=stock_code,stop_event=stop_event)
    elif frequency == "day":
        data = daily_data_feed.DailyRealTimeData(symbol=stock_code)
    cerebro.adddata(data)

    main_service_url = "http://localhost:8081" #主服务地址
    myBroker = MyBroker(api_base_url=main_service_url,sim_id=simulationId,data=data,frequnce=frequency)
    cerebro.broker = myBroker

    cerebro.addanalyzer(DynamicAnnualReturn,_name="AnnualReturn")
    cerebro.addanalyzer(DynamicSharpe,_name="SharpeRatio")
    cerebro.addanalyzer(bt.analyzers.DrawDown,_name ="DrawDown")
    #cerebro.addobserver(bt.observers.Broker)
    #cerebro.addobserver(bt.observers.Trades)
    #cerebro.addobserver(bt.observers.BuySell)
    cerebro.addobserver(LoggerObserver)
    cerebro.addobserver(StatsObserver,api_base_url=main_service_url,simulation_id=simulationId) #模拟交易专用, 计算统计数据

    cerebro.broker.setcommission(commission=0.0003,margin=None,stocklike=True)#设置手续费,stocklike=True表示股票交易,避免购买数量为小数
    cerebro.addsizer(bt.sizers.PercentSizer, percents=20,retint = True) #retint 表示购买数量为整数

    run_thread = threading.Thread(target=run_cerebro, args=(cerebro,stop_event))
    simulationObj = SimulationObj(cerebro=cerebro,thread=run_thread,stop_event=stop_event) 
    
    return simulationObj

# 定义一个函数运行cerebro
def run_cerebro(cerebro,stop_event):
    try:
        cerebro.run(runonce=False) 
    except Exception as e:
        print("Error during cerebro execution:", e)
        traceback.print_exc()
    
    # 如果 cerebro 停止运行，设置停止标志
    stop_event.set()




   

   