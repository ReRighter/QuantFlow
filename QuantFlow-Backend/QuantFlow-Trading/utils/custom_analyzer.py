from collections import OrderedDict
import backtrader as bt
from backtrader import Analyzer, TimeFrame
from backtrader.mathsupport import average, standarddev
from backtrader.analyzers import TimeReturn, AnnualReturn
from backtrader.utils.py3 import itervalues
import math

# 动态年化收益率分析器
# 该分析器用于动态计算年化收益率，适用于需要实时监控年化收益率的策略
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
            # 获取子分析器返回的结果
            returns = list(itervalues(self.timereturn.get_analysis()))

            rate = self.p.riskfreerate  #

            factor = None

            
            if self.p.timeframe == TimeFrame.Days and \
               self.p.daysfactor is not None:

                factor = self.p.daysfactor

            else:
                if self.p.factor is not None:
                    factor = self.p.factor 
                elif self.p.timeframe in self.RATEFACTORS:
                    
                    factor = self.RATEFACTORS[self.p.timeframe]

            if factor is not None:
                if self.p.convertrate:
                    rate = pow(1.0 + rate, 1.0 / factor) - 1.0
                else:
                    returns = [pow(1.0 + x, factor) - 1.0 for x in returns]

            lrets = len(returns) - self.p.stddev_sample
            if lrets:
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
                ratio = None

            self.ratio = ratio
        self.rets['sharperatio'] = self.ratio
        return self.rets