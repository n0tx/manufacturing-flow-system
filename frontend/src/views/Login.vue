<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const username = ref('');
const password = ref('');
const loading = ref(false);
const errorMsg = ref('');

const handleLogin = async () => {
  loading.value = true;
  errorMsg.value = '';
  
  try {
    const response = await api.post('/auth/login', {
      username: username.value,
      password: password.value
    });
    
    // Check if the response contains the token based on our ApiResponse structure
    if (response.data && response.data.data && response.data.data.token) {
      localStorage.setItem('token', response.data.data.token);
      localStorage.setItem('username', username.value);
      localStorage.setItem('role', response.data.data.role);
      
      // Redirect to dashboard
      router.push('/');
    } else {
      errorMsg.value = 'Invalid response format from server';
    }
  } catch (error) {
    console.error('Login error:', error);
    if (error.response && error.response.data && error.response.data.message) {
      errorMsg.value = error.response.data.message;
    } else {
      errorMsg.value = 'Login failed. Please check your credentials or server connection.';
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-container">
    <div class="glass-card login-card animate-fade-in">
      <div class="login-header">
        <div class="logo-circle">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-hexagon"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path></svg>
        </div>
        <h2>PT. Bintang Tekstil Jaya</h2>
        <p>Manufacturing Flow System MVP</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div v-if="errorMsg" class="error-alert">
          {{ errorMsg }}
        </div>
        
        <div class="form-group">
          <label class="form-label" for="username">Username</label>
          <input 
            id="username"
            v-model="username" 
            type="text" 
            class="form-input" 
            placeholder="admin / gudang / produksi / finance" 
            required
            autocomplete="username"
          />
        </div>
        
        <div class="form-group">
          <label class="form-label" for="password">Password</label>
          <input 
            id="password"
            v-model="password" 
            type="password" 
            class="form-input" 
            placeholder="••••••••" 
            required
            autocomplete="current-password"
          />
        </div>
        
        <button type="submit" class="btn btn-primary login-btn" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>Sign In to Dashboard</span>
        </button>
      </form>
      
      <div class="demo-info">
        <p><strong>Demo Hint:</strong> Use <code>admin</code> / <code>admin123</code></p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: radial-gradient(circle at top right, var(--bg-tertiary), var(--bg-primary));
  padding: 1rem;
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 2.5rem;
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.logo-circle {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--accent-primary), var(--accent-hover));
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 1.5rem;
  box-shadow: var(--shadow-glow);
  color: white;
}

.login-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.25rem;
}

.login-header p {
  font-size: 0.875rem;
  color: var(--text-muted);
}

.login-btn {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  margin-top: 1rem;
}

.error-alert {
  background-color: rgba(239, 68, 68, 0.1);
  border-left: 4px solid var(--danger);
  color: #fca5a5;
  padding: 0.75rem 1rem;
  border-radius: var(--border-radius-sm);
  margin-bottom: 1.5rem;
  font-size: 0.875rem;
}

.demo-info {
  margin-top: 2rem;
  text-align: center;
  font-size: 0.875rem;
  color: var(--text-muted);
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.demo-info code {
  background-color: var(--bg-tertiary);
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
  font-family: monospace;
  color: var(--text-secondary);
}

/* Spinner for loading state */
.spinner {
  display: inline-block;
  width: 1.25rem;
  height: 1.25rem;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
