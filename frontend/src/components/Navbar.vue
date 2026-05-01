<template>
  <nav class="navbar" v-if="isAuthenticated">
    <div class="nav-brand" @click="router.push('/')">
      <div class="logo-small">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path></svg>
      </div>
      <h1>PT. Bintang Tekstil Jaya</h1>
    </div>

    <div class="nav-links">
      <router-link to="/" class="nav-item" active-class="active">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
        Dashboard
      </router-link>
      <router-link to="/inventory" class="nav-item" active-class="active">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
        Inventory
      </router-link>
    </div>

    <div class="nav-user">
      <span class="user-badge">{{ role }}</span>
      <span class="user-name">Halo, <strong>{{ username }}</strong></span>
      <button @click="handleLogout" class="btn-logout">Logout</button>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

const username = ref(localStorage.getItem('username') || 'User');
const role = ref(localStorage.getItem('role') || 'UNKNOWN');

const isAuthenticated = computed(() => {
  return !!localStorage.getItem('token') && route.name !== 'Login';
});

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  localStorage.removeItem('role');
  router.push('/login');
};
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 2rem;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  cursor: pointer;
}

.logo-small {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  padding: 6px;
  border-radius: 8px;
  display: flex;
  color: white;
}

.nav-brand h1 {
  font-size: 1.1rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 1.5rem;
}

.nav-item {
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: 0.3s;
  padding: 0.5rem 1rem;
  border-radius: 8px;
}

.nav-item:hover {
  color: white;
  background: rgba(255, 255, 255, 0.05);
}

.nav-item.active {
  color: white;
  background: rgba(59, 130, 246, 0.2);
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 1.2rem;
}

.user-badge {
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.7rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.user-name {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
}

.btn-logout {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
  border: 1px solid rgba(239, 68, 68, 0.2);
  padding: 0.4rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: 0.3s;
}

.btn-logout:hover {
  background: #ef4444;
  color: white;
}
</style>
