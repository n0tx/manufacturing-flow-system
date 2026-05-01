<script setup>
import { RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue'
</script>

<template>
  <div class="app-layout">
    <Navbar />
    <RouterView />
  </div>
</template>

<style>
/* App specific styles if needed */
</style>
