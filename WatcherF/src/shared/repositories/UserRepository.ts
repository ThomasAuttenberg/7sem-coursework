import { post } from '@/shared/repositories/ApiClient.ts'
import type { LoginRequest, UserData } from '@/shared/repositories/types/user.ts'




export const UserRepository = {
  auth: (loginData: LoginRequest) => post<UserData, LoginRequest>('/auth/login',loginData)
}


