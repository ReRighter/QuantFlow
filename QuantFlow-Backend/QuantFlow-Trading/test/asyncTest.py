import asyncio
import time


async def async_function():
    await asyncio.sleep(1)
    return "Hello, World!"

async  def async_function2():
    await asyncio.sleep(2)
    return "Hello, World2!"


async def main():
    task1 =  asyncio.create_task(async_function())
    task2 =  asyncio.create_task(async_function2())
    result =await asyncio.gather(task1,task2)
    print(result)


if __name__ == "__main__":
    startTime = time.time()
    asyncio.run(main())
    endTime= time.time()
    print(endTime-startTime)