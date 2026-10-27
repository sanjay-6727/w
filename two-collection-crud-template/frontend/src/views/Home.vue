<template>
  <section class="hero">
    <p class="small-title">BEGINNER CRUD TEMPLATE</p>
    <h1>Manage two related collections</h1>
    <p>This sample shows CRUD operations and how documents in one MongoDB collection can refer to another.</p>
    <div class="buttons">
      <RouterLink class="button" to="/first-items">Open first collection</RouterLink>
      <RouterLink class="button light" to="/second-items">Open second collection</RouterLink>
    </div>
  </section>

  <section class="card">
    <h2>How the connection works</h2>
    <p>Each document in the second collection contains a <code>firstItemId</code>. That ID connects it to a document in the first collection.</p>
    <p class="hint">Try this template with Student + Enrollment, Author + Book, Department + Employee, or another pair of collections.</p>
  </section>
</template>