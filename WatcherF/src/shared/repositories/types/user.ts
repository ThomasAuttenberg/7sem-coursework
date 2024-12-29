export enum UserRole {
  ROLE_ADMIN,
  ROLE_OPERATOR
}

export type UserData = {
  message: string
  username: string,
  roles: UserRole[],
  token: string
}

export type LoginRequest = {
  username: string,
  password: string,
}
