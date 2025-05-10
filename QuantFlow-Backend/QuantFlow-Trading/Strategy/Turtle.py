import backtrader as bt

class TurtleStrategy(bt.Strategy):
    params = (
        ('period_high', 20),     # 入场突破周期
        ('period_low', 10),      # 止损突破周期
        ('atr_period', 14),      # ATR周期
        ('risk_per_trade', 0.01),# 每笔交易风险比例
        ('max_units', 4),        # 最大加仓次数
    )

    def __init__(self):
        # 计算N日最高价和最低价
        self.highest = bt.indicators.Highest(self.data.high, period=self.params.period_high)
        self.lowest = bt.indicators.Lowest(self.data.low, period=self.params.period_low)

        # 计算ATR（平均真实波幅）
        self.atr = bt.indicators.AverageTrueRange(self.data, period=self.params.atr_period)

        # 初始化变量
        self.units = 0  # 当前持仓单位数
        self.entry_price = None  # 入场价格
        self.stop_loss = None  # 止损价格
        self.add_position = False  # 是否可以加仓

        # 添加退出信号计算
        self.exit_high = bt.indicators.Highest(self.data.high, period=self.params.period_low)
        self.exit_low = bt.indicators.Lowest(self.data.low, period=self.params.period_low)
        
        # 记录交易方向
        self.order = None
        self.trade_direction = None  # 1为做多，-1为做空
        
    def next(self):
        if self.order:
            return  # 等待订单执行
            
        cash = self.broker.get_cash()
        risk_amount = cash * self.params.risk_per_trade
        
        # 计算头寸规模
        unit_size = self.calculate_unit_size(risk_amount)
        
        if not self.position:
            # 多头入场
            if self.data.close > self.highest[-1]:
                self.buy(size=unit_size)
                self.units = 1
                self.entry_price = self.data.close
                self.stop_loss = self.entry_price - 2 * self.atr[-1]
                self.trade_direction = 1
                
            # 空头入场    
            elif self.data.close < self.lowest[-1]:
                self.sell(size=unit_size)
                self.units = 1
                self.entry_price = self.data.close
                self.stop_loss = self.entry_price + 2 * self.atr[-1]
                self.trade_direction = -1
                
        else:
            # 移动止损
            if self.trade_direction == 1:
                new_stop = self.entry_price - 2 * self.atr[-1]
                self.stop_loss = max(self.stop_loss, new_stop)
                
                # 多头加仓
                if self.units < self.params.max_units and \
                   self.data.close >= self.entry_price + 0.5 * self.atr[-1]:
                    self.buy(size=unit_size)
                    self.adjust_position_params(self.data.close)
                    
                # 多头退出
                elif self.data.close < self.exit_low[-1] or self.data.close <= self.stop_loss:
                    self.close()
                    self.reset_position()
                    
            else:  # 空头
                new_stop = self.entry_price + 2 * self.atr[-1]
                self.stop_loss = min(self.stop_loss, new_stop)
                
                # 空头加仓
                if self.units < self.params.max_units and \
                   self.data.close <= self.entry_price - 0.5 * self.atr[-1]:
                    self.sell(size=unit_size)
                    self.adjust_position_params(self.data.close)
                    
                # 空头退出
                elif self.data.close > self.exit_high[-1] or self.data.close >= self.stop_loss:
                    self.close()
                    self.reset_position()
    
    def calculate_unit_size(self, risk_amount):
        """计算头寸规模"""
        unit_size = risk_amount / (self.atr[-1] * self.data.close)
        return int(unit_size)
        
    def adjust_position_params(self, price):
        """调整持仓参数"""
        self.units += 1
        self.entry_price = (self.entry_price * (self.units - 1) + price) / self.units
        
    def reset_position(self):
        """重置持仓参数"""
        self.units = 0
        self.entry_price = None
        self.stop_loss = None
        self.trade_direction = None