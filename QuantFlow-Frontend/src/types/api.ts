export interface Result<T = any> {
  code: number;
  message: string;
  data?: T;
}

export interface UserInfo {
  id: number;
  username: string;
  email: string;
  role: 'admin' | 'user'; // 添加角色字段
  // 其他用户信息字段
}

export interface Course {
  id: number;
  title: string;
  description: string;
  parentId:number;
  resourceId: number;
  orderNumber: number;
}

export interface CourseTree {
  parent_course: Course;
  lessons: Course[];
  cover: string;
}

export interface resource {
  id: number;
  name: string;
  path: string;
  status: string;
  hash: string;
}
export interface LessonDetails{
  lesson:Course;
  videoUrl:string;
}

export interface Strategy {
  id:number;
  name:string;
  code:string;
}

export interface BackTestFormData {
  start_time: string
  end_time: string
  stock_code: string
  cash: number
  sizer: number
}

export interface BackTestResult {
  start_date : string,
  end_date: string,
  stock_code: number,
  initial_funding: number,
  end_funding: number,
  trading_size: number,
  trading_log:string[],
  earnings: number,
  earnings_rate: number,
  annual_returns: number,
  sharpe_ratio: number,
  max_drawdown: number
}