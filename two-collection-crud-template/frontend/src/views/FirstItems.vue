<script setup>
import { ref, onMounted } from 'vue'
import { request } from '../api'

const items = ref([])
const error = ref('')
const message = ref('')
const form = ref({ name: '', category: '', price: 0 })
const editId = ref('')
const searchName = ref('')
const category = ref('')
const listType = ref('all')
const desc = ref(false)

async function loadItems() {
  error.value = ''
  try {
    if (listType.value === 'related') {
      items.value = await request('/first-items/with-related')
    } else if (listType.value === 'unrelated') {
      items.value = await request('/first-items/without-related')
    } else if (listType.value === 'sort') {
      items.value = await request('/first-items/sort?desc=' + desc.value)
    } else if (category.value.trim()) {
      items.value = await request('/first-items/category/' + encodeURIComponent(category.value.trim()))
    } else if (searchName.value.trim()) {
      items.value = await request('/first-items/search?name=' + encodeURIComponent(searchName.value.trim()))
    } else {
      items.value = await request('/first-items')
    }
  } catch (e) {
    error.value = e.message
  }
}

function clearFilters() {
  searchName.value = ''
  category.value = ''
  listType.value = 'all'
  loadItems()
}

function editItem(item) {
  editId.value = item.id
  form.value = { name: item.name, category: item.category, price: item.price }
}

function resetForm() {
  editId.value = ''
  form.value = { name: '', category: '', price: 0 }
}

async function saveItem() {
  error.value = ''
  message.value = ''
  try {
    const body = {
      name: form.value.name,
      category: form.value.category,
      price: Number(form.value.price)
    }

    if (editId.value) {
      await request('/first-items/' + editId.value, {
        method: 'PUT',
        body: JSON.stringify(body)
      })
      message.value = 'Item updated.'
    } else {
      await request('/first-items', {
        method: 'POST',
        body: JSON.stringify(body)
      })
      message.value = 'Item added.'
    }

    resetForm()
    await loadItems()
  } catch (e) {
    error.value = e.message
  }
}

async function deleteItem(id) {
  if (!confirm('Delete this item and its related second-collection items?')) return
  try {
    await request('/first-items/' + id, { method: 'DELETE' })
    message.value = 'Item deleted.'
    await loadItems()
  } catch (e) {
    error.value = e.message
  }
}

onMounted(loadItems)
</script>

<template>
  <div class="page-heading">
    <div><p class="small-title">FIRST COLLECTION</p><h1>First items</h1></div>
    <button class="light" @click="loadItems">Refresh</button>
  </div>
  <p v-if="message" class="success">{{ message }}</p>
  <p v-if="error" class="error">{{ error }}. Check that the backend is running.</p>

  <section class="card">
    <h2>{{ editId ? 'Update item' : 'Add item' }}</h2>
    <form class="form-grid" @submit.prevent="saveItem">
      <label>Name<input v-model.trim="form.name" required placeholder="Item name" /></label>
      <label>Category<input v-model.trim="form.category" required placeholder="Category" /></label>
      <label>Price / number<input v-model.number="form.price" type="number" min="0" step="0.01" required /></label>
      <div class="buttons">
        <button type="submit">{{ editId ? 'Save changes' : 'Add item' }}</button>
        <button v-if="editId" type="button" class="light" @click="resetForm">Cancel</button>
      </div>
    </form>
  </section>

  <section class="card">
    <h2>Search and filter</h2>
    <div class="filters">
      <label>Search name<input v-model="searchName" placeholder="Name" @keyup.enter="loadItems" /></label>
      <label>Category<input v-model="category" placeholder="Category" @keyup.enter="loadItems" /></label>
      <label>List
        <select v-model="listType">
          <option value="all">All items</option>
          <option value="related">Items with related records</option>
          <option value="unrelated">Items without related records</option>
          <option value="sort">Sort by price</option>
        </select>
      </label>
      <label v-if="listType === 'sort'">Order
        <select v-model="desc"><option :value="false">Low to high</option><option :value="true">High to low</option></select>
      </label>
    </div>
    <div class="buttons">
      <button @click="loadItems">Apply</button>
      <button class="light" @click="clearFilters">Clear</button>
    </div>
  </section>

  <section class="card">
    <h2>First collection list ({{ items.length }})</h2>
    <p v-if="items.length === 0" class="hint">No items found.</p>
    <div v-else class="table-wrap">
      <table>
        <thead><tr><th>Name</th><th>Category</th><th>Price</th><th>ID</th><th>Actions</th></tr></thead>
        <tbody>
          <tr v-for="item in items" :key="item.id">
            <td>{{ item.name }}</td><td>{{ item.category }}</td><td>{{ item.price }}</td>
            <td class="id">{{ item.id }}</td>
            <td class="buttons"><button class="small light" @click="editItem(item)">Edit</button><button class="small danger" @click="deleteItem(item.id)">Delete</button></td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>