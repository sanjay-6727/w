const API = 'http://localhost:8080/api'

export async function request(path, options = {}) {
  const response = await fetch(API + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })

  const text = await response.text()
  let data = null

  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }
  }

  if (!response.ok) {
    throw new Error(data?.message || data?.error || 'Request failed')
  }

  return data
}