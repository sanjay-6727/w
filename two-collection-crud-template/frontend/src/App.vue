<template>
  <header class="top">
    <strong>Two Collection CRUD</strong>
    <nav>
      <RouterLink to="/">Home</RouterLink>
      <RouterLink to="/first-items">First Collection</RouterLink>
      <RouterLink to="/second-items">Second Collection</RouterLink>
      <RouterLink to="/reports">Reports</RouterLink>
    </nav>
  </header>

  <main class="container">
    <RouterView />
  </main>

  <footer>Simple Spring Boot + MongoDB + Vue template</footer>
</template>