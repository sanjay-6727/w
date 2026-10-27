<script setup>
import { ref, onMounted } from 'vue'
import { request } from '../api'

const stats = ref({})
const counts = ref({})
const totals = ref({})
const firstItems = ref([])
const error = ref('')

async function loadReports() {
  error.value = ''
  try {
    const result = await Promise.all([
      request('/reports/stats'),
      request('/reports/second-count-by-first'),
      request('/reports/value-total-by-first'),
      request('/first-items')
    ])
    stats.value = result[0]
    counts.value = result[1]
    totals.value = result[2]
    firstItems.value = result[3]
  } catch (e) {
    error.value = e.message
  }
}

function itemName(id) {
  const item = firstItems.value.find(row => row.id === id)
  return item ? item.name : id
}

onMounted(loadReports)
</script>

<template>
  <div class="page-heading">
    <div><p class="small-title">REPORTS</p><h1>Summary</h1></div>
    <button class="light" @click="loadReports">Refresh</button>
  </div>
  <p v-if="error" class="error">{{ error }}. Check that the backend is running.</p>

  <section class="stats-grid">
    <div class="stat"><span>First collection</span><strong>{{ stats.firstCount ?? '—' }}</strong></div>
    <div class="stat"><span>Second collection</span><strong>{{ stats.secondCount ?? '—' }}</strong></div>
    <div class="stat"><span>Total number / quantity</span><strong>{{ stats.totalValue ?? '—' }}</strong></div>
    <div class="stat"><span>Average price</span><strong>{{ stats.averagePrice == null ? '—' : Number(stats.averagePrice).toFixed(2) }}</strong></div>
    <div class="stat"><span>Minimum price</span><strong>{{ stats.minPrice == null ? '—' : Number(stats.minPrice).toFixed(2) }}</strong></div>
    <div class="stat"><span>Maximum price</span><strong>{{ stats.maxPrice == null ? '—' : Number(stats.maxPrice).toFixed(2) }}</strong></div>
  </section>

  <section class="card">
    <h2>Related item count</h2>
    <p v-if="Object.keys(counts).length === 0" class="hint">No related items yet.</p>
    <div v-else class="table-wrap">
      <table><thead><tr><th>First item</th><th>ID</th><th>Count</th></tr></thead>
        <tbody><tr v-for="(count, id) in counts" :key="id"><td>{{ itemName(id) }}</td><td class="id">{{ id }}</td><td>{{ count }}</td></tr></tbody>
      </table>
    </div>
  </section>

  <section class="card">
    <h2>Total number / quantity by first item</h2>
    <p v-if="Object.keys(totals).length === 0" class="hint">No totals yet.</p>
    <div v-else class="table-wrap">
      <table><thead><tr><th>First item</th><th>ID</th><th>Total</th></tr></thead>
        <tbody><tr v-for="(total, id) in totals" :key="id"><td>{{ itemName(id) }}</td><td class="id">{{ id }}</td><td>{{ total }}</td></tr></tbody>
      </table>
    </div>
  </section>
</template>