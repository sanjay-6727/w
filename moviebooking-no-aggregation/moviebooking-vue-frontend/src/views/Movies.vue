<script setup>
import { ref, onMounted } from 'vue'
import { request, showError } from '../api'

const movies = ref([])
const message = ref('')
const error = ref('')
const form = ref({ name: '', genre: '', price: 0 })
const editId = ref('')
const searchName = ref('')
const genre = ref('')
const movieListType = ref('all')
const sortDirection = ref('asc')

async function loadMovies() {
  error.value = ''
  try {
    if (movieListType.value === 'booked') {
      movies.value = await request('/movies/with-bookings')
    } else if (movieListType.value === 'not-booked') {
      movies.value = await request('/movies/without-bookings')
    } else if (movieListType.value === 'sort') {
      movies.value = await request('/movies/sort-by-price?desc=' + (sortDirection.value === 'desc'))
    } else if (genre.value.trim()) {
      movies.value = await request('/movies/by-genre/' + encodeURIComponent(genre.value.trim()))
    } else if (searchName.value.trim()) {
      movies.value = await request('/movies/search?name=' + encodeURIComponent(searchName.value.trim()))
    } else {
      movies.value = await request('/movies')
    }
  } catch (e) {
    error.value = showError(e)
  }
}

function clearFilters() {
  searchName.value = ''
  genre.value = ''
  movieListType.value = 'all'
  loadMovies()
}

function startEdit(movie) {
  editId.value = movie.id
  form.value = { name: movie.name, genre: movie.genre, price: movie.price }
  message.value = ''
}

function resetForm() {
  editId.value = ''
  form.value = { name: '', genre: '', price: 0 }
}

async function saveMovie() {
  error.value = ''
  message.value = ''
  try {
    const body = {
      name: form.value.name,
      genre: form.value.genre,
      price: Number(form.value.price)
    }
    if (editId.value) {
      await request('/movies/' + editId.value, {
        method: 'PUT',
        body: JSON.stringify(body)
      })
      message.value = 'Movie updated.'
    } else {
      await request('/movies', {
        method: 'POST',
        body: JSON.stringify(body)
      })
      message.value = 'Movie added.'
    }
    resetForm()
    await loadMovies()
  } catch (e) {
    error.value = showError(e)
  }
}

async function deleteMovie(id) {
  if (!confirm('Delete this movie and its related bookings?')) return
  error.value = ''
  message.value = ''
  try {
    await request('/movies/' + id, { method: 'DELETE' })
    message.value = 'Movie and related bookings deleted.'
    if (editId.value === id) resetForm()
    await loadMovies()
  } catch (e) {
    error.value = showError(e)
  }
}

onMounted(loadMovies)
</script>

<template>
  <div class="page-heading">
    <div>
      <p class="small-title">MOVIE MANAGEMENT</p>
      <h1>Movies</h1>
    </div>
    <button class="secondary" @click="loadMovies">Refresh</button>
  </div>

  <p v-if="message" class="success">{{ message }}</p>
  <p v-if="error" class="error">{{ error }}. Check that the backend is running.</p>

  <section class="card">
    <h2>{{ editId ? 'Update movie' : 'Add a movie' }}</h2>
    <form @submit.prevent="saveMovie" class="form-grid">
      <label>Movie name
        <input v-model.trim="form.name" required placeholder="e.g. Interstellar" />
      </label>
      <label>Genre
        <input v-model.trim="form.genre" required placeholder="e.g. Sci-Fi" />
      </label>
      <label>Price
        <input v-model.number="form.price" type="number" min="0" step="0.01" required />
      </label>
      <div class="form-buttons">
        <button type="submit">{{ editId ? 'Save changes' : 'Add movie' }}</button>
        <button v-if="editId" type="button" class="secondary" @click="resetForm">Cancel</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>Find movies</h2>
    <div class="filters">
      <label>Search by name
        <input v-model="searchName" placeholder="Movie name" @keyup.enter="loadMovies" />
      </label>
      <label>Filter by genre
        <input v-model="genre" placeholder="Genre" @keyup.enter="loadMovies" />
      </label>
      <label>Movie list
        <select v-model="movieListType">
          <option value="all">All movies</option>
          <option value="booked">Movies with bookings</option>
          <option value="not-booked">Movies without bookings</option>
          <option value="sort">Sort by price</option>
        </select>
      </label>
      <label v-if="movieListType === 'sort'">Order
        <select v-model="sortDirection">
          <option value="asc">Low to high</option>
          <option value="desc">High to low</option>
        </select>
      </label>
    </div>
    <div class="form-buttons">
      <button @click="loadMovies">Search / Apply</button>
      <button class="secondary" @click="clearFilters">Clear filters</button>
    </div>
  </section>

  <section class="card">
    <div class="section-title">
      <h2>Movie list</h2>
      <span class="count">{{ movies.length }} movies</span>
    </div>
    <div v-if="movies.length === 0" class="empty">No movies found.</div>
    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr><th>Name</th><th>Genre</th><th>Price</th><th>Movie ID</th><th>Actions</th></tr>
        </thead>
        <tbody>
          <tr v-for="movie in movies" :key="movie.id">
            <td>{{ movie.name }}</td>
            <td>{{ movie.genre }}</td>
            <td>{{ Number(movie.price).toFixed(2) }}</td>
            <td class="id-cell">{{ movie.id }}</td>
            <td class="actions">
              <button class="small secondary" @click="startEdit(movie)">Edit</button>
              <button class="small danger" @click="deleteMovie(movie.id)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>