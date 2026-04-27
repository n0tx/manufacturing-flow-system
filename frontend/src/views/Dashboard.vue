<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const router = useRouter();
const username = ref(localStorage.getItem('username') || 'User');
const role = ref(localStorage.getItem('role') || 'UNKNOWN');

// State
const summary = ref(null);
const orders = ref([]);
const loading = ref(true);
const filterStatus = ref('');

// Modal State
const showModal = ref(false);
const modalType = ref(''); // 'RECEIVING', 'PRODUCTION', 'DELIVERY', 'PAYMENT'
const selectedOrder = ref(null);
const modalLoading = ref(false);
const modalError = ref('');

// Modal Form Data
const formData = ref({});

// Fetch Data
const fetchData = async () => {
  loading.value = true;
  try {
    const [summaryRes, ordersRes] = await Promise.all([
      api.get('/dashboard/summary'),
      api.get('/orders', { params: { status: filterStatus.value}})
    ]);
    summary.value = summaryRes.data.data;
    orders.value = ordersRes.data.data.sort((a, b) => b.id - a.id); // Latest first
  } catch (error) {
    console.error('Error fetching dashboard data:', error);
    if (error.response?.status === 401) {
      handleLogout();
    }
  } finally {
    loading.value = false;
  }
};

watch(filterStatus, () => {
  fetchData();
});

onMounted(() => {
  fetchData();
});

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  localStorage.removeItem('role');
  router.push('/login');
};

// Formatting helpers
const formatCurrency = (value) => {
  return new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0 }).format(value);
};

const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('id-ID', { dateStyle: 'medium', timeStyle: 'short' }).format(date);
};

const getStatusBadgeClass = (status) => {
  const map = {
    'CREATED': 'badge-created',
    'MATERIAL_PREPARED': 'badge-material',
    'IN_PRODUCTION': 'badge-production',
    'COMPLETED_PRODUCTION': 'badge-completed',
    'DELIVERED': 'badge-delivered',
    'PAID': 'badge-paid'
  };
  return map[status] || 'badge-created';
};

// Modal Actions
const openModal = (type, order) => {
  modalType.value = type;
  selectedOrder.value = order;
  formData.value = {}; // Reset
  modalError.value = '';
  
  // Set default values based on type
  if (type === 'PRODUCTION') {
    formData.value.productionType = 'KNITTING';
  } else if (type === 'PAYMENT') {
    formData.value.paymentMethod = 'BANK_TRANSFER';
    formData.value.amountPaid = order.totalPrice;
  }
  
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedOrder.value = null;
};

const cancelOrder = async (orderId) => {
  if (!confirm('Yakin ingin membatalkan pesanan ini?')) return;
  try {
    await api.delete('/orders/' + orderId);
    await fetchData();
  } catch (error) {
    alert(error.response?.data?.message || 'Gagal membatalkan pesanan.');
  }
};

const submitAction = async () => {
  modalLoading.value = true;
  modalError.value = '';
  
  try {
    let endpoint = '';
    let payload = { orderId: selectedOrder.value.id };
    
    if (modalType.value === 'RECEIVING') {
      endpoint = '/receivings';
      payload.rawMaterialNotes = formData.value.rawMaterialNotes;
    } else if (modalType.value === 'PRODUCTION') {
      endpoint = '/productions';
      payload.productionType = formData.value.productionType;
      payload.machineId = formData.value.machineId;
      payload.notes = formData.value.notes;
    } else if (modalType.value === 'FINISH_PRODUCTION') {
      endpoint = `/productions/${selectedOrder.value.id}/finish`;
      payload = null; // No body needed
    } else if (modalType.value === 'DELIVERY') {
      endpoint = '/deliveries';
      payload.trackingNumber = formData.value.trackingNumber;
      payload.driverName = formData.value.driverName;
      payload.vehiclePlate = formData.value.vehiclePlate;
    } else if (modalType.value === 'PAYMENT') {
      endpoint = '/payments';
      payload.paymentMethod = formData.value.paymentMethod;
      payload.amountPaid = formData.value.amountPaid;
      payload.referenceNumber = formData.value.referenceNumber;
    }

    await api.post(endpoint, payload);
    
    // Success! Refresh data and close modal
    await fetchData();
    closeModal();
    
  } catch (error) {
    modalError.value = error.response?.data?.message || 'Terjadi kesalahan saat memproses data.';
  } finally {
    modalLoading.value = false;
  }
};

</script>

<template>
  <div class="dashboard-layout">
    <!-- Navbar -->
    <nav class="navbar">
      <div class="nav-brand">
        <div class="logo-small">
          <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path></svg>
        </div>
        <h1>PT. Bintang Tekstil Jaya</h1>
      </div>
      <div class="nav-user">
        <span class="user-badge">{{ role }}</span>
        <span class="user-name">Halo, <strong>{{ username }}</strong></span>
        <button @click="handleLogout" class="btn btn-outline-danger btn-sm">Logout</button>
      </div>
    </nav>

    <main class="main-content">
      <div v-if="loading" class="loading-state">
        <div class="spinner-large"></div>
        <p>Memuat data dashboard...</p>
      </div>

      <div v-else class="animate-fade-in">
        <!-- Summary Cards -->
        <div class="summary-grid">
          <div class="glass-card summary-card">
            <div class="summary-icon icon-blue">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
            </div>
            <div class="summary-info">
              <h3>Total Orders</h3>
              <p class="summary-value">{{ summary?.totalOrders || 0 }}</p>
            </div>
          </div>
          
          <div class="glass-card summary-card">
            <div class="summary-icon icon-yellow">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
            </div>
            <div class="summary-info">
              <h3>In Production</h3>
              <p class="summary-value">{{ summary?.inProduction || 0 }}</p>
            </div>
          </div>

          <div class="glass-card summary-card">
            <div class="summary-icon icon-green">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="3" width="15" height="13"></rect><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon><circle cx="5.5" cy="18.5" r="2.5"></circle><circle cx="18.5" cy="18.5" r="2.5"></circle></svg>
            </div>
            <div class="summary-info">
              <h3>Delivered</h3>
              <p class="summary-value">{{ summary?.delivered || 0 }}</p>
            </div>
          </div>

          <div class="glass-card summary-card">
            <div class="summary-icon icon-emerald">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
            </div>
            <div class="summary-info">
              <h3>Paid (Selesai)</h3>
              <p class="summary-value">{{ summary?.paid || 0 }}</p>
            </div>
          </div>
        </div>

        <!-- Orders Table -->
        <div class="glass-card mt-6">
          <div class="table-header">
            <h2>Daftar Pesanan (Order List)</h2>
            <select v-model="filterStatus" class="filter-dropdown">
              <option value="">Semua Status</option>
              <option value="CREATED">Baru (Created)</option>
              <option value="MATERIAL_PREPARED">Bahan Siap</option>
              <option value="IN_PRODUCTION">Dalam Produksi</option>
              <option value="COMPLETED_PRODUCTION">Produksi Selesai</option>
              <option value="DELIVERED">Terkirim</option>
              <option value="PAID">Lunas (Paid)</option>
            </select>
          </div>
          
          <div class="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Tanggal</th>
                  <th>Customer</th>
                  <th>Produk</th>
                  <th>Qty</th>
                  <th>Total Harga</th>
                  <th>Status</th>
                  <th>Aksi (Workflow)</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="orders.length === 0">
                  <td colspan="8" class="text-center py-4">Belum ada data pesanan. Buat order baru via Swagger.</td>
                </tr>
                <tr v-for="order in orders" :key="order.id">
                  <td><strong>#{{ order.id }}</strong></td>
                  <td>{{ formatDate(order.orderDate) }}</td>
                  <td>{{ order.customer?.name }}</td>
                  <td>{{ order.product?.name }}</td>
                  <td>{{ order.quantity }}</td>
                  <td>{{ formatCurrency(order.totalPrice) }}</td>
                  <td>
                    <span class="badge" :class="getStatusBadgeClass(order.status)">
                      {{ order.status.replace('_', ' ') }}
                    </span>
                  </td>
                  <td>
                    <!-- Interactive Action Buttons -->
                    <button v-if="order.status === 'CREATED'" @click="openModal('RECEIVING', order)" class="btn btn-sm btn-primary">
                      1. Terima Bahan
                    </button>
                    <button v-if="order.status === 'CREATED'" @click="cancelOrder(order.id)" class="btn btn-sm btn-outline-danger">
                      Batal Pesan
                    </button>
                    <button v-else-if="order.status === 'MATERIAL_PREPARED'" @click="openModal('PRODUCTION', order)" class="btn btn-sm btn-warning">
                      2. Mulai Produksi
                    </button>
                    
                    <div v-else-if="order.status === 'IN_PRODUCTION'" class="btn-group">
                      <button @click="openModal('PRODUCTION', order)" class="btn btn-sm btn-outline-warning" title="Tambah log mesin">
                        + Log Mesin
                      </button>
                      <button @click="openModal('FINISH_PRODUCTION', order)" class="btn btn-sm btn-warning">
                        Selesai Produksi
                      </button>
                    </div>

                    <button v-else-if="order.status === 'COMPLETED_PRODUCTION'" @click="openModal('DELIVERY', order)" class="btn btn-sm btn-info">
                      3. Kirim Barang
                    </button>
                    
                    <button v-else-if="order.status === 'DELIVERED'" @click="openModal('PAYMENT', order)" class="btn btn-sm btn-success">
                      4. Catat Bayar
                    </button>
                    
                    <span v-else-if="order.status === 'PAID'" class="text-success fw-bold">
                      ✓ Tuntas
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal Form (Glassmorphism Overlay) -->
    <div v-if="showModal" class="modal-overlay">
      <div class="glass-card modal-content animate-fade-in">
        <div class="modal-header">
          <h3 v-if="modalType === 'RECEIVING'">Terima Bahan Baku (Receiving)</h3>
          <h3 v-else-if="modalType === 'PRODUCTION'">Log Produksi Mesin</h3>
          <h3 v-else-if="modalType === 'FINISH_PRODUCTION'">Selesaikan Produksi</h3>
          <h3 v-else-if="modalType === 'DELIVERY'">Kirim Barang (Delivery)</h3>
          <h3 v-else-if="modalType === 'PAYMENT'">Pembayaran (Payment)</h3>
          <button @click="closeModal" class="btn-close">&times;</button>
        </div>
        
        <div class="modal-body">
          <p class="mb-4">Order ID: <strong>#{{ selectedOrder?.id }}</strong> | Customer: <strong>{{ selectedOrder?.customer?.name }}</strong></p>
          
          <div v-if="modalError" class="error-alert mb-4">{{ modalError }}</div>
          
          <form @submit.prevent="submitAction">
            
            <!-- Receiving Form -->
            <div v-if="modalType === 'RECEIVING'" class="form-group">
              <label class="form-label">Catatan Bahan Baku</label>
              <textarea v-model="formData.rawMaterialNotes" class="form-input" rows="3" required placeholder="Contoh: Diterima benang katun putih 1000kg"></textarea>
            </div>
            
            <!-- Production Form -->
            <div v-if="modalType === 'PRODUCTION'">
              <div class="form-group">
                <label class="form-label">Tipe Produksi</label>
                <select v-model="formData.productionType" class="form-input" required>
                  <option value="KNITTING">KNITTING (Rajut)</option>
                  <option value="DYEING">DYEING (Celup Warna)</option>
                  <option value="PRINTING">PRINTING (Cetak Motif)</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">ID Mesin</label>
                <input v-model="formData.machineId" type="text" class="form-input" required placeholder="Contoh: MESIN-RJT-01" />
              </div>
              <div class="form-group">
                <label class="form-label">Catatan Operator</label>
                <textarea v-model="formData.notes" class="form-input" rows="2" placeholder="Catatan opsional..."></textarea>
              </div>
            </div>

            <!-- Finish Production (No fields) -->
            <div v-if="modalType === 'FINISH_PRODUCTION'" class="alert-warning">
              <p>Apakah Anda yakin semua proses produksi (Knitting, Dyeing, Printing) untuk pesanan ini sudah selesai? Status akan diubah menjadi COMPLETED_PRODUCTION.</p>
            </div>

            <!-- Delivery Form -->
            <div v-if="modalType === 'DELIVERY'">
              <div class="form-group">
                <label class="form-label">Nomor Resi / Surat Jalan</label>
                <input v-model="formData.trackingNumber" type="text" class="form-input" required placeholder="SJ-2026-001" />
              </div>
              <div class="form-group">
                <label class="form-label">Nama Supir</label>
                <input v-model="formData.driverName" type="text" class="form-input" required placeholder="Budi Santoso" />
              </div>
              <div class="form-group">
                <label class="form-label">Plat Nomor Kendaraan</label>
                <input v-model="formData.vehiclePlate" type="text" class="form-input" required placeholder="B 1234 CD" />
              </div>
            </div>

            <!-- Payment Form -->
            <div v-if="modalType === 'PAYMENT'">
              <div class="form-group">
                <label class="form-label">Total Tagihan</label>
                <input type="text" class="form-input bg-disabled" disabled :value="formatCurrency(selectedOrder?.totalPrice)" />
              </div>
              <div class="form-group">
                <label class="form-label">Metode Pembayaran</label>
                <select v-model="formData.paymentMethod" class="form-input" required>
                  <option value="CASH">CASH (Tunai)</option>
                  <option value="BANK_TRANSFER">BANK TRANSFER (Transfer Bank)</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">Jumlah Dibayar (Rp)</label>
                <input v-model.number="formData.amountPaid" type="number" class="form-input" required min="1" />
              </div>
              <div class="form-group">
                <label class="form-label">Nomor Referensi (Opsional)</label>
                <input v-model="formData.referenceNumber" type="text" class="form-input" placeholder="Contoh: BCA-123456" />
              </div>
            </div>

            <div class="modal-footer mt-4">
              <button type="button" @click="closeModal" class="btn" style="background: transparent; color: white;">Batal</button>
              <button type="submit" class="btn btn-primary" :disabled="modalLoading">
                <span v-if="modalLoading">Memproses...</span>
                <span v-else>Simpan Data</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.dashboard-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(15, 23, 42, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-small {
  background: var(--accent-primary);
  border-radius: 4px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.nav-brand h1 {
  font-size: 1.125rem;
  margin: 0;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-badge {
  background: rgba(59, 130, 246, 0.2);
  color: #93c5fd;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: bold;
}

.btn-sm {
  padding: 0.4rem 0.75rem;
  font-size: 0.75rem;
}

.btn-outline-danger {
  background: transparent;
  border: 1px solid var(--danger);
  color: #fca5a5;
}
.btn-outline-danger:hover {
  background: var(--danger);
  color: white;
}

.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
}

.summary-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.icon-blue { background: linear-gradient(135deg, #3b82f6, #2563eb); }
.icon-yellow { background: linear-gradient(135deg, #f59e0b, #d97706); }
.icon-green { background: linear-gradient(135deg, #10b981, #059669); }
.icon-emerald { background: linear-gradient(135deg, #14b8a6, #0d9488); }

.summary-info h3 {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin-bottom: 0.25rem;
  font-weight: 500;
}

.summary-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
}

.mt-6 {
  margin-top: 2rem;
}

.table-header {
  margin-bottom: 1.5rem;
}
.table-header h2 {
  font-size: 1.25rem;
}

/* Badge colors */
.badge-created { background: rgba(100, 116, 139, 0.2); color: #cbd5e1; border: 1px solid #64748b; }
.badge-material { background: rgba(139, 92, 246, 0.2); color: #c4b5fd; border: 1px solid #8b5cf6; }
.badge-production { background: rgba(245, 158, 11, 0.2); color: #fcd34d; border: 1px solid #f59e0b; }
.badge-completed { background: rgba(14, 165, 233, 0.2); color: #7dd3fc; border: 1px solid #0ea5e9; }
.badge-delivered { background: rgba(16, 185, 129, 0.2); color: #6ee7b7; border: 1px solid #10b981; }
.badge-paid { background: rgba(34, 197, 94, 0.2); color: #86efac; border: 1px solid #22c55e; }

.btn-warning { background-color: var(--warning); color: white; }
.btn-warning:hover { background-color: #d97706; }
.btn-outline-warning { background-color: transparent; border: 1px solid var(--warning); color: #fcd34d; }
.btn-info { background-color: var(--info); color: white; }
.btn-info:hover { background-color: #0284c7; }
.btn-group { display: flex; gap: 0.5rem; }

.text-success { color: #4ade80; }
.fw-bold { font-weight: bold; }
.text-center { text-align: center; }
.py-4 { padding-top: 1rem; padding-bottom: 1rem; }

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  width: 100%;
  max-width: 500px;
  background: rgba(30, 41, 59, 0.95);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 1rem;
  margin-bottom: 1rem;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
}

.btn-close {
  background: transparent;
  border: none;
  color: var(--text-muted);
  font-size: 1.5rem;
  cursor: pointer;
  line-height: 1;
}
.btn-close:hover { color: white; }

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 1rem;
}

.mb-4 { margin-bottom: 1rem; }
.bg-disabled { background-color: rgba(0,0,0,0.2); cursor: not-allowed; }

.alert-warning {
  background-color: rgba(245, 158, 11, 0.1);
  border-left: 4px solid var(--warning);
  color: #fcd34d;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

/* Spinner */
.spinner-large {
  width: 3rem;
  height: 3rem;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  border-top-color: var(--accent-primary);
  animation: spin 1s ease-in-out infinite;
  margin: 0 auto 1rem;
}
.loading-state {
  text-align: center;
  padding: 5rem 0;
  color: var(--text-muted);
}
.filter-dropdown {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(30, 41, 59, 0.8);
  color: white;
  font-family: inherit;
  outline: none;
  cursor: pointer;
}
</style>
