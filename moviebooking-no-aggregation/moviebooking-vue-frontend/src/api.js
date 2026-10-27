// The Spring Boot backend should run on port 8080.
const API = 'http://localhost:8080/api'

export async function request(path, options = {}) {
  const response = await fetch(API + path, {
    headers: {
      'Content-Type': 'application/json'
    },
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
    throw new Error(data?.error || data?.message || 'Request failed')
  }

  return data
}

export function showError(error) {
  return error.message || 'Something went wrong'
}