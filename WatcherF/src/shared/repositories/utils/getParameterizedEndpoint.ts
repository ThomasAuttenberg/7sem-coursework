export const getParameterizedEndpoint = (endpoint: string, params: Record<string, unknown>): string => {
  if (Object.keys(params).length === 0) {
    return endpoint;
  }
  const queryString = Object.keys(params)
    .map((key) => `${encodeURIComponent(key)}=${encodeURIComponent(String(params[key]))}`)
    .join('&');
  return `${endpoint}?${queryString}`;
};
