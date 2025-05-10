from threading import Lock



class SimulationObj:
    def __init__(self,cerebro,thread,stop_event):
        self.cerebro = cerebro
        self.thread = thread
        self.stop_event = stop_event



class SimulationManager:
    def __init__(self):
        self.simulation_map = {}
        self.lock = Lock()  # 创建一个锁对象

    def add_simulation(self, simulation_id, simulation_obj:SimulationObj):
        with self.lock:# 使用锁来保护对共享资源的访问
            self.simulation_map[simulation_id] = simulation_obj

    def remove_simulation(self, simulation_id):
        with self.lock:
            if simulation_id in self.simulation_map:
                del self.simulation_map[simulation_id]

    def getSimulationObject(self, simulation_id)->SimulationObj:
        with self.lock:
            return self.simulation_map.get(simulation_id)

