from datetime import datetime

from RestrictedPython import compile_restricted, safe_builtins
import backtrader as bt
import tushare as ts
import pandas as pd
from utils import LoggerObserver



ts_token="32f21ba37fc15213e2e71c32277a58b381f1fb14543ab206dcbec0e4"

def exec_strategy_code(start_time,end_time,stock_code,strategy_code,cash=100000.0,sizer=10 ):
    strategy_class = compile_code(strategy_code)
    cerebro = bt.Cerebro(stdstats=False)
    # 设置初始资金
    cerebro.broker.setcash(cash)
    data_df = get_stock_data(stock_code,start_time,end_time)
    data = bt.feeds.PandasData(
        dataname=data_df,
        datetime='trade_date',
        openinterest=-1
    )
    cerebro.adddata(data)
    # 设置每次交易的资金比例
    cerebro.addsizer(bt.sizers.PercentSizer, percents=sizer)
    #手续费
    cerebro.broker.setcommission(commission=0.0003)
    #添加策略
    cerebro.addstrategy(strategy_class)
    #分析指标
    cerebro.addanalyzer(bt.analyzers.AnnualReturn,_name="AnnualReturn")
    cerebro.addanalyzer(bt.analyzers.SharpeRatio,_name="SharpeRatio")
    cerebro.addanalyzer(bt.analyzers.DrawDown,_name ="DrawDown")
    cerebro.addobserver(bt.observers.Broker)
    cerebro.addobserver(bt.observers.Trades)
    cerebro.addobserver(bt.observers.BuySell)
    cerebro.addobserver(LoggerObserver)

    
    result = None
    try:
        result=cerebro.run()
    except Exception as e:
        raise ValueError(f"策略执行出错: {str(e)}")
    else:
        #获取第一个策略的结果
        strategy = result[0]
        end_funding = round(cerebro.broker.getvalue(),2)
        earnings = round(end_funding - cash,2)
        earnings_rate = round((earnings / cash) ,4) * 100
        annual_returns = strategy.analyzers.AnnualReturn.get_analysis() #返回结果为[(年份,收益率)]
        sharpe_ratio = strategy.analyzers.SharpeRatio.get_analysis()
        drawdown = strategy.analyzers.DrawDown.get_analysis()["max"]["drawdown"]
        log = strategy.observers[3].log

        result_dictionary = {
            "start_date": start_time,
            "end_date": end_time,
            "stock_code": stock_code,
            "initial_funding": cash,
            "end_funding": end_funding,
            "trading_size": sizer,
            "trading_log":log,
            "earnings": earnings,
            "earnings_rate": earnings_rate,
            "annual_returns": annual_returns,
            "sharpe_ratio": sharpe_ratio,
            "max_drawdown": drawdown
        }
        #cerebro.plot(style='candlestick', volume=True)
        return result_dictionary

    

def compile_code(code):
    namespace={}
    try:
        #compiled_code = compile_restricted(code, '<inline_code>', 'exec')
        exec(code, namespace)
        strategy_class =None
        for name,obj in namespace.items():
            if isinstance(obj,type) and issubclass(obj,bt.Strategy):
                strategy_class = obj
                break

        if strategy_class is None:
            raise ValueError("编译的代码没有定义一个有效的策略类")
        return strategy_class
    except Exception as e:
        raise ValueError(f"编译代码出错: {str(e)}")

def get_stock_data(stock_code,start_time,end_time):
    pro= ts.pro_api(token=ts_token)
    df:pd.DataFrame = pro.daily(ts_code=stock_code,start_date=start_time,end_date=end_time)
    #修改时间逆序为正序
    df.sort_values(by='trade_date', ascending=True,inplace=True)
    #修改时间格式
    df["trade_date"] = pd.to_datetime(df["trade_date"], format='%Y%m%d')
    df.rename(columns={
        'vol':'volume'
    }, inplace=True)
    return df

def get_stock_data_test(stock_code,start_time,end_time):
    df:pd.DataFrame = pd.read_csv("test/000001.SZ_20240101_20241231.csv", index_col=0)
    #按时间升序
    #df.sort_values(by='trade_date',inplace=True)
    df['trade_date'] = pd.to_datetime(df['trade_date'],format='%Y%m%d')
    df.rename(columns={
        'vol':'volume'
    }, inplace=True)
    return df

def main():
    with open("Strategy/SMA.py", "r",encoding='utf-8') as file:
        strategy_code = file.read()
        start_time = "20240101"
        end_time = "20241231"
        stock_code = "000001.SZ"
        exec_strategy_code(start_time, end_time, stock_code, strategy_code)
