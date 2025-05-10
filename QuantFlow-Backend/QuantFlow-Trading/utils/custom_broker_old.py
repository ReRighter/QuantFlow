import backtrader as bt
import requests
from threading import Lock

class CustomBroker(bt.brokers.BackBroker):
    def __init__(self, remote_service_url, **kwargs):
        super().__init__(**kwargs)
        self.remote_service_url = remote_service_url
        self.order_lock = Lock()
        self.pending_orders = {}

    def submit(self, order, **kwargs):
        with self.order_lock:
            # 发送订单到远程服务
            response = self._send_to_remote_service(order)
            
            if response['status'] == 'accepted':
                # 存储订单信息
                self.pending_orders[order.ref] = {
                    'order': order,
                    'remote_id': response.get('remote_order_id'),
                    'status': 'pending'
                }
                return super().submit(order, **kwargs)
            return None

    def submit(self, order, **kwargs):
        """
        重写submit方法来拦截订单
        """
        # 在这里处理订单，发送到远程服务
        response = self._send_to_remote_service(order)
        
        if response['status'] == 'accepted':
            # 如果远程服务接受了订单，则继续正常处理
            return super().submit(order, **kwargs)
        else:
            # 如果远程服务拒绝了订单，返回None或抛出异常
            return None
    def notify_order(self, order):
        """
        处理订单状态更新
        """
        with self.order_lock:
            if order.ref in self.pending_orders:
                # 查询远程订单状态
                status = self._check_remote_order_status(
                    self.pending_orders[order.ref]['remote_id']
                )
                # 根据远程状态更新本地订单
                self._update_order_status(order, status)  

    def _check_remote_order_status(self, remote_order_id):
        """
        检查远程订单状态
        """
        import requests
        try:
            response = requests.get(
                f"{self.remote_service_url}/order_status/{remote_order_id}"
            )
            return response.json()
        except Exception as e:
            print(f"检查订单状态失败: {str(e)}")
            return {'status': 'unknown'}
        

    def _update_order_status(self, order, remote_status):
        """
        根据远程状态更新本地订单
        """
        if remote_status['status'] == 'filled':
            order.execute(
                dt=bt.num2date(self.data.datetime[0]),
                size=order.size,
                price=remote_status['filled_price'],
                exectype=order.exectype
            )
            order.completed()
        elif remote_status['status'] == 'cancelled':
            order.cancel()


    def _send_to_remote_service(self, order):
        """
        实现与远程服务的通信逻辑
        """
        
        order_data = {
            'symbol': order.data._name,
            'size': order.size,
            'price': order.price,
            'type': order.exectype.name,
            'side': 'buy' if order.isbuy() else 'sell'
        }
        
        try:
            response = requests.post(
                f"{self.remote_service_url}/process_order",
                json=order_data
            )
            return response.json()
        except Exception as e:
            print(f"订单处理服务调用失败: {str(e)}")
            return {'status': 'rejected'}

class TradeSystemAPI:
    def place_order(self, order_info):
    
        response = requests.post("http://localhost:8081/trading/place_order", json=order_info)
        return response.json()

    def get_order_status(self, order_id):
        # 查询订单状态
        response = requests.get(f"http://localhost:8081/trading/order_status/{order_id}")
        return response.json()  