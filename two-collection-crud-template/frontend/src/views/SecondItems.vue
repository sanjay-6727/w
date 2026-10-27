<script setup>
import { ref, onMounted } from 'vue'
import { request } from '../api'

const items = ref([])
const firstItems = ref([])
const error = ref('')
const message = ref('')
const form = ref({ firstItemId: '', personName: '', value: 1 })
const editId = ref('')
const personSearch = ref('')
const firstIdSearch = ref('')

async function loadFirstItems() {
  try {
    firstItems.value = await request('/first-items')
    if (!form.value.firstItemId && firstItems.value.length) {
      form.value.firstItemId = firstItems.value[0].id
    }
  } catch (e) {
    error.value = e.message
  }
}

async function loadItems() {
  error.value = ''
  try {
    if (firstIdSearch.value.trim()) {
      items.value = await request('/first-items/' + encodeURIComponent(firstIdSearch.value.trim()) + '/second-items')
    } else if (personSearch.value.trim()) {
      items.value = await request('/second-items/search?personName=' + encodeURIComponent(personSearch.value.trim()))
    } else {
      items.value = await request('/second-items')
    }
  } catch (e) {
    error.value = e.message
  }
}

function firstName(id) {
  const found = firstItems.value.find(item => item.id === id)
  return found ? found.name : 'Unknown ID'
}

function editItem(item) {
  editId.value = item.id
  form.value = {
    firstItemId: item.firstItemId,
    personName: item.personName,
    value: item.value
  }
}

function resetForm() {
  editId.value = ''
  form.value = { firstItemId: firstItems.value[0]?.id || '', personName: '', value: 1 }
}

async function saveItem() {
  error.value = ''
  message.value = ''
  try {
    const body = {
      firstItemId: form.value.firstItemId,
      personName: form.value.personName,
      value: Number(form.value.value)
    }
    if (editId.value) {
      await request('/second-items/' + editId.value, { method: 'PUT', body: JSON.stringify(body) })
      message.value = 'Item updated.'
    } else {
      await request('/second-items', { method: 'POST', body: JSON.stringify(body) })
      message.value = 'Item added.'
    }
    resetForm()
    await loadItems()
  } catch (e) {
    error.value = e.message
  }
}

async function deleteItem(id) {
  if (!confirm('Delete this item?')) return
  try {
    await request('/second-items/' + id, { method: 'DELETE' })
    message.value = 'Item deleted.'
    await loadItems()
  } catch (e) {
    error.value = e.message
  }
}

function clearFilters() {
  personSearch.value = ''
  firstIdSearch.value = ''
  loadItems()
}

onMounted(async () => {
  await loadFirstItems()
  await loadItems()
})
</script>

<template>
  <div class="page-heading">
    <div><p class="small-title">SECOND COLLECTION</p><h1>Second items</h1></div>
    <button class="light" @click="loadItems">Refresh</button>
  </div>
  <p v-if="message" class="success">{{ message }}</p>
  <p v-if="error" class="error">{{ error }}. Check the backend and selected ID.</p>

  <section class="card">
    <h2>{{ editId ? 'Update item' : 'Add item' }}</h2>
    <p v-if="firstItems.length === 0" class="hint">Add a first-collection item before adding related items.</p>
    <form class="form-grid" @submit.prevent="saveItem">
      <label>Related first item
        <select v-model="form.firstItemId" required>
          <option disabled value="">Choose an item</option>
          <option v-for="item in firstItems" :key="item.id" :value="item.id">{{ item.name }} ({{ item.id }})</option>
        </select>
      </label>
      <label>Person / label<input v-model.trim="form.personName" required placeholder="Name" /></label>
      <label>Number / quantity<input v-model.number="form.value" type="number" min="0" required /></label>
      <div class="buttons">
        <button type="submit" :disabled="firstItems.length === 0">{{ editId ? 'Save changes' : 'Add item' }}</button>
        <button v-if="editId" type="button" class="light" @click="resetForm">Cancel</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>Search items</h2>
    <div class="filters">
      <label>Search person / label<input v-model="personSearch" placeholder="Name" @keyup.enter="loadItems" /></label>
      <label>Show items for first-item ID<input v-model="firstIdSearch" placeholder="Paste first item ID" @keyup.enter="loadItems" /></label>
    </div>
    <div class="buttons">
      <button @click="loadItems">Search</button>
      <button class="light" @click="clearFilters">Show all</button>
    </div>
  </section>

  <section class="card">
    <h2>Second collection list ({{ items.length }})</h2>
    <p v-if="items.length === 0" class="hint">No items found.</p>
    <div v-else class="table-wrap">
      <table>
        <thead><tr><th>Person / label</th><th>Related item</th><th>Number</th><th>ID</th><th>Actions</th></tr></thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{ item.personName }}</td><td>{{ firstName(item.firstItemId) }}</td><td>{{ item.value }}</td>
            <td class="id">{{ item.id }}</td>
            <td class="buttons"><button class="small light" @click="editItem(item)">Edit</button><button class="small danger" @click="deleteItem(item.id)">Delete</button></td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>