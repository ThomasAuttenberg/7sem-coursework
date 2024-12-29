import { get, post } from '@/shared/repositories/ApiClient.ts'

export const StaticRepository = {
  uploadImage: (file: File) => {
    const formData = new FormData();
    formData.append("image", file);

    return post<{file:string},FormData>('/static/image', formData, {
      "Content-Type": "multipart/form-data",
    });
  },
  getImage: (file: string) => get<Blob>(`/static/image/${file}`)
}
