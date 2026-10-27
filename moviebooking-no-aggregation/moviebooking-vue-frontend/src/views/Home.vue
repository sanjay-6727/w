<template>
  <section class="hero">
    <p class="small-title">MOVIE BOOKING SYSTEM</p>
    <h1>Manage movies and bookings</h1>
    <p class="intro">A simple frontend connected to the Spring Boot API.</p>
    <div class="home-links">
      <RouterLink class="button" to="/movies">Manage Movies</RouterLink>
      <RouterLink class="button secondary" to="/bookings">Manage Bookings</RouterLink>
    </div>
  </section>

  <section class="card">
    <h2>What this app does</h2>
    <ul>
      <li>Add, view, update and delete movies.</li>
      <li>Add, view, update and delete bookings.</li>
      <li>Find movies with bookings or without bookings.</li>
      <li>Search and filter movies and bookings.</li>
      <li>View the counts, seat totals and movie price statistics.</li>
    </ul>
    <p class="hint">Start the Spring Boot application and MongoDB before using these pages.</p>
  </section>
</template>