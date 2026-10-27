<script setup>
import { ref, onMounted } from 'vue'
import { request, showError } from '../api'

const stats = ref({})
const bookingCounts = ref({})
const seatTotals = ref({})
const bookedMovies = ref([])
const unbookedMovies = ref([])
const error = ref('')

async function loadReports() {
  error.value = ''
  try {
    const results = await Promise.all([
      request('/stats/movie-count'),
      request('/stats/booking-count'),
      request('/stats/seats'),
      request('/stats/price/average'),
      request('/stats/price/min'),
      request('/stats/price/max'),
      request('/stats/booking-count-by-movie'),
      request('/stats/seats-by-movie'),
      request('/movies/with-bookings'),
      request('/movies/without-bookings')
    ])

    stats.value = {
      movieCount: results[0].movieCount,
      bookingCount: results[1].bookingCount,
      totalSeats: results[2].totalSeats,
      averagePrice: results[3].averagePrice,
      minPrice: results[4].minPrice,
      maxPrice: results[5].maxPrice
    }
    bookingCounts.value = results[6]
    seatTotals.value = results[7]
    bookedMovies.value = results[8]
    unbookedMovies.value = results[9]
  } catch (e) {
    error.value = showError(e)
  }
}

function getMovieName(id) {
  const movie = [...bookedMovies.value, ...unbookedMovies.value].find(item => item.id === id)
  return movie ? movie.name : id
}

onMounted(loadReports)
</script>

<template>
  <div class="page-heading">
    <div>
      <p class="small-title">SERVICE RESULTS</p>
      <h1>Reports</h1>
    </div>
    <button class="secondary" @click="loadReports">Refresh reports</button>
  </div>

  <p v-if="error" class="error">{{ error }}. Check that the backend is running.</p>

  <section class="stats-grid">
    <div class="stat-card"><span>Total movies</span><strong>{{ stats.movieCount ?? '—' }}</strong></div>
    <div class="stat-card"><span>Total bookings</span><strong>{{ stats.bookingCount ?? '—' }}</strong></div>
    <div class="stat-card"><span>Seats booked</span><strong>{{ stats.totalSeats ?? '—' }}</strong></div>
    <div class="stat-card"><span>Average price</span><strong>{{ stats.averagePrice == null ? '—' : Number(stats.averagePrice).toFixed(2) }}</strong></div>
    <div class="stat-card"><span>Lowest price</span><strong>{{ stats.minPrice == null ? '—' : Number(stats.minPrice).toFixed(2) }}</strong></div>
    <div class="stat-card"><span>Highest price</span><strong>{{ stats.maxPrice == null ? '—' : Number(stats.maxPrice).toFixed(2) }}</strong></div>
  </section>

  <section class="card">
    <h2>Booking count by movie</h2>
    <p v-if="Object.keys(bookingCounts).length === 0" class="empty">No bookings yet.</p>
    <div v-else class="table-wrap">
      <table>
        <thead><tr><th>Movie</th><th>Movie ID</th><th>Bookings</th></tr></thead>
        <tbody>
          <tr v-for="(count, id) in bookingCounts" :key="id">
            <td>{{ getMovieName(id) }}</td><td class="id-cell">{{ id }}</td><td>{{ count }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>

  <section class="card">
    <h2>Seat total by movie</h2>
    <p v-if="Object.keys(seatTotals).length === 0" class="empty">No seat totals yet.</p>
    <div v-else class="table-wrap">
      <table>
        <thead><tr><th>Movie</th><th>Movie ID</th><th>Total seats</th></tr></thead>
        <tbody>
          <tr v-for="(total, id) in seatTotals" :key="id">
            <td>{{ getMovieName(id) }}</td><td class="id-cell">{{ id }}</td><td>{{ total }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>

  <div class="two-columns">
    <section class="card">
      <h2>Movies with bookings</h2>
      <p v-if="bookedMovies.length === 0" class="empty">No movies with bookings.</p>
      <ul v-else class="simple-list"><li v-for="movie in bookedMovies" :key="movie.id">{{ movie.name }}</li></ul>
    </section>
    <section class="card">
      <h2>Movies without bookings</h2>
      <p v-if="unbookedMovies.length === 0" class="empty">No movies without bookings.</p>
      <ul v-else class="simple-list"><li v-for="movie in unbookedMovies" :key="movie.id">{{ movie.name }}</li></ul>
    </section>
  </div>
</template>