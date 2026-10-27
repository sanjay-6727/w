<script setup>
import { ref, onMounted } from 'vue'
import { request, showError } from '../api'

const bookings = ref([])
const movies = ref([])
const message = ref('')
const error = ref('')
const form = ref({ movieId: '', customerName: '', seats: 1 })
const editId = ref('')
const customerSearch = ref('')
const movieSearchId = ref('')

async function loadMovies() {
  try {
    movies.value = await request('/movies')
    if (!form.value.movieId && movies.value.length) form.value.movieId = movies.value[0].id
  } catch (e) {
    error.value = showError(e)
  }
}

async function loadBookings() {
  error.value = ''
  try {
    if (movieSearchId.value.trim()) {
      bookings.value = await request('/movies/' + encodeURIComponent(movieSearchId.value.trim()) + '/bookings')
    } else if (customerSearch.value.trim()) {
      bookings.value = await request('/bookings/customer/' + encodeURIComponent(customerSearch.value.trim()))
    } else {
      bookings.value = await request('/bookings')
    }
  } catch (e) {
    error.value = showError(e)
  }
}

function clearFilters() {
  customerSearch.value = ''
  movieSearchId.value = ''
  loadBookings()
}

function startEdit(booking) {
  editId.value = booking.id
  form.value = {
    movieId: booking.movieId,
    customerName: booking.customerName,
    seats: booking.seats
  }
  message.value = ''
}

function resetForm() {
  editId.value = ''
  form.value = { movieId: movies.value[0]?.id || '', customerName: '', seats: 1 }
}

async function saveBooking() {
  error.value = ''
  message.value = ''
  try {
    const body = {
      movieId: form.value.movieId,
      customerName: form.value.customerName,
      seats: Number(form.value.seats)
    }
    if (editId.value) {
      await request('/bookings/' + editId.value, {
        method: 'PUT',
        body: JSON.stringify(body)
      })
      message.value = 'Booking updated.'
    } else {
      await request('/bookings', {
        method: 'POST',
        body: JSON.stringify(body)
      })
      message.value = 'Booking added.'
    }
    resetForm()
    await loadBookings()
  } catch (e) {
    error.value = showError(e)
  }
}

async function deleteBooking(id) {
  if (!confirm('Delete this booking?')) return
  error.value = ''
  try {
    await request('/bookings/' + id, { method: 'DELETE' })
    message.value = 'Booking deleted.'
    if (editId.value === id) resetForm()
    await loadBookings()
  } catch (e) {
    error.value = showError(e)
  }
}

function movieName(id) {
  const movie = movies.value.find(item => item.id === id)
  return movie ? movie.name : 'Movie ID: ' + id
}

onMounted(async () => {
  await loadMovies()
  await loadBookings()
})
</script>

<template>
  <div class="page-heading">
    <div>
      <p class="small-title">BOOKING MANAGEMENT</p>
      <h1>Bookings</h1>
    </div>
    <button class="secondary" @click="loadBookings">Refresh</button>
  </div>

  <p v-if="message" class="success">{{ message }}</p>
  <p v-if="error" class="error">{{ error }}. Check that the backend is running and the movie ID is valid.</p>

  <section class="card">
    <h2>{{ editId ? 'Update booking' : 'Add a booking' }}</h2>
    <p v-if="movies.length === 0" class="hint">Add a movie first, then create a booking.</p>
    <form @submit.prevent="saveBooking" class="form-grid">
      <label>Movie
        <select v-model="form.movieId" required>
          <option disabled value="">Choose a movie</option>
          <option v-for="movie in movies" :key="movie.id" :value="movie.id">
            {{ movie.name }} ({{ movie.id }})
          </option>
        </select>
      </label>
      <label>Customer name
        <input v-model.trim="form.customerName" required placeholder="Customer name" />
      </label>
      <label>Seats
        <input v-model.number="form.seats" type="number" min="1" step="1" required />
      </label>
      <div class="form-buttons">
        <button type="submit" :disabled="movies.length === 0">{{ editId ? 'Save changes' : 'Add booking' }}</button>
        <button v-if="editId" type="button" class="secondary" @click="resetForm">Cancel</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>Find bookings</h2>
    <div class="filters">
      <label>Customer name
        <input v-model="customerSearch" placeholder="Customer name" @keyup.enter="loadBookings" />
      </label>
      <label>Bookings for movie ID
        <input v-model="movieSearchId" placeholder="Paste movie ID" @keyup.enter="loadBookings" />
      </label>
    </div>
    <div class="form-buttons">
      <button @click="loadBookings">Search</button>
      <button class="secondary" @click="clearFilters">Show all</button>
    </div>
  </section>

  <section class="card">
    <div class="section-title">
      <h2>Booking list</h2>
      <span class="count">{{ bookings.length }} bookings</span>
    </div>
    <div v-if="bookings.length === 0" class="empty">No bookings found.</div>
    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr><th>Customer</th><th>Movie</th><th>Seats</th><th>Booking ID</th><th>Actions</th></tr>
        </thead>
        <tbody>
          <tr v-for="booking in bookings" :key="booking.id">
            <td>{{ booking.customerName }}</td>
            <td>{{ movieName(booking.movieId) }}</td>
            <td>{{ booking.seats }}</td>
            <td class="id-cell">{{ booking.id }}</td>
            <td class="actions">
              <button class="small secondary" @click="startEdit(booking)">Edit</button>
              <button class="small danger" @click="deleteBooking(booking.id)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>