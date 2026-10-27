<template>
  <div>
    <header class="top">
      <div class="brand">Movie Booking</div>
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/movies">Movies</RouterLink>
        <RouterLink to="/bookings">Bookings</RouterLink>
        <RouterLink to="/reports">Reports</RouterLink>
      </nav>
    </header>

    <main class="container">
      <RouterView />
    </main>

    <footer>Movie Booking • Simple Vue project</footer>
  </div>
</template>