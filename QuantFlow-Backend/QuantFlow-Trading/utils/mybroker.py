import backtrader as bt
import requests
from threading import Lock
import json
from datetime import datetime
from backtrader.position import Position  # 导入 Position 类


class MyBroker(bt.brokers.BackBroker):
    def __init__(self, api_base_url, sim_id, data=None, frequnce: str = 'minute'):
        super().__init__()
        self.api_base_url = api_base_url
        self.simulation_id = sim_id
        self.order_lock = Lock()
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


        except Exception as e:
            print(f"账户同步时发生错误: {str(e)}")

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

    def _process_filled_order(self, order):  # 接收API返回的成交数据
        """
        处理已成交订单
        """

        exec_price = order.created.price
        exec_size =  order.size
        # 确定成交时间
        exec_dt_num = self.data.datetime[0]  # 默认使用当前bar时间
        try:
            super()._execute(order=order,price=exec_price,ago=0)
        except Exception as e:
            print(f"处理已成交订单错误{str(e)}")

        print(f"订单执行完成: ref={order.ref}, dt={bt.num2date(exec_dt_num)}, size={exec_size}, price={exec_price}")
        if order.ref in self.pending_orders:
            del self.pending_orders[order.ref]
        # 通知策略订单状态变化
        self.notify(order)

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

        try:
            super().next()
        except Exception as e:
            print(f"调用 super().next() 时出错: {str(e)}")

        # 可以在这里添加其他逻辑，例如定期（而非每次）同步账户状态
        # if len(self.data) % 60 == 0: # 例如每60个bar同步一次
        #     self._sync_account_status()
