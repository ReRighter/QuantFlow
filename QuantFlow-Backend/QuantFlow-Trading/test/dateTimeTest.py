from datetime import datetime, timedelta
import backtrader as bt

start_time = datetime.strptime("12:00","%H:%M")
start_date_time = datetime.strptime("20250418 13:00","%Y%m%d %H:%M")
print(bt.date2num(start_date_time))

#interval = timedelta(minutes=1)
#len = 10
#
#def generate_time_sequence(start_time,interval):
#    current_time = start_time
#    while True:
#        yield current_time
#        current_time += interval
#
#generator = generate_time_sequence(start_time=start_date_time,interval=interval)
#for _ in range(len):
#    print(next(generator).strftime("%Y%m%d %H:%M"))