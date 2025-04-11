class Result:
    code: int
    message: str
    data: str
    def __init__(self, code: int, message: str, data: str = None):
        self.code = code
        self.message = message
        self.data = data

    @staticmethod
    def success(data: str = None, message: str = "success"):
        return Result(code=200, message=message, data=data)

    @staticmethod
    def  error(message: str, code: int = 500) :
        return  Result(code=code, message=message, data=None)
    
    def to_dict(self):
            return {
                "code": self.code,
                "message": self.message,
                "data": self.data
            }

print(Result.error(message="error",code=500).to_dict())