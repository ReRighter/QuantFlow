import backtrader as bt
import requests
from threading import Lock
import json
from datetime import datetime

class MyBroker(bt.brokers.BackBroker):
    def __init__(self, api_base_url, sim_id, cash=None,frequnce:str = 'minute'):
        super().__init__()
        self.api_base_url = api_base_url
        self.simulation_id = sim_id
        self.order_lock = Lock()
        self.pending_orders = {}
        self.frequnce = frequnce
        self.session = requests.Session()  # 使用session保持连接
        
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
        try:
            response = self.session.get(
                f"{self.api_base_url}/trading/sync_status/{self.simulation_id}"
            )
            result = response.json()

            if result["code"] == 200:
                data = result["data"]
                self.cash = data["balances"]["available"]
                # 同步持仓信息
                position =  data['positions']
                print("同步持仓:symbol:",position["stockCode"],"quantity:",position["quantity"])
                self.positions[position['stockCode']] = position['quantity']
        except Exception as e:
            print(f"账户同步失败: {str(e)}")
            
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
                'price': order.price , #if order.exectype == bt.Order.Limit else None,
                'frequence' : self.frequnce
            }
            print("提交订单:", order_request)
            try:
                response = self.session.post(
                    f"{self.api_base_url}/trading/place_order",
                    json=order_request
                )
                result = response.json()
                if result["code"] == 200:
                    data =result["data"]
                    self.pending_orders[order.ref] = {
                        'order': order,
                        'remote_id': data['orderId'],
                        'status': data['status']
                    }
                    
                    # 交易系统中订单处理成功,此处直接完成订单
                    if data['status'] == 'filled':
                        self._process_filled_order(order, data)
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
                
    def _process_filled_order(self, order, price):
        """
        处理已成交订单
        """
        order.execute(
            dt=bt.num2date(self.data.datetime[0]),
            size=order.size,
            price=price,
            exectype=order.exectype
        )
        order.completed()
        
        # 更新账户状态
        self._sync_account_status()
        
    def _check_pending_orders(self):
        """
        检查待处理订单状态
        """
        with self.order_lock:
            for order_ref, order_info in list(self.pending_orders.items()):
                if order_info['status'] == 'pending':
                    try:
                        #获取交易系统中订单信息
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
                            else :
                                print(f"订单状态错误:{status}")
                                
                    except Exception as e:
                        print(f"订单状态查询失败: {str(e)}")
                        
    def next(self):
        """
        在每个bar更新时检查订单状态
        """
        super().next()
        self._check_pending_orders()