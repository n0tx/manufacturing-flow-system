<template>
  <div class="inventory-container">
    <div class="header-section">
      <h1 class="page-title">Inventory Management</h1>
      <button @click="openAdjustmentModal(null)" class="btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 10px;"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
        <span style="font-size: 1rem; font-weight: 600; letter-spacing: 0.1px;">Manual Adjustment</span>
      </button>
    </div>

    <!-- Inventory Table -->
    <div class="glass-card">
      <table class="custom-table">
        <thead>
          <tr>
            <th>SKU</th>
            <th>Material Name</th>
            <th>Current Stock</th>
            <th>Min. Stock</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in inventories" :key="item.id">
            <td class="font-mono">{{ item.sku }}</td>
            <td class="font-bold">{{ item.materialName }}</td>
            <td>
              <div class="stock-display">
                <span :class="getStockClass(item)">{{ item.stockQuantity }}</span>
                <small class="unit-label">{{ item.unit }}</small>
              </div>
            </td>
            <td>{{ item.minStock }} {{ item.unit }}</td>
            <td>
              <span :class="['badge', getStatusBadgeClass(item)]">
                {{ getStatusText(item) }}
              </span>
            </td>
            <td>
              <div class="action-buttons">
                <!-- History Icon (SVG) -->
                <button @click="viewLogs(item)" class="btn-icon" title="View History">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                </button>
                <!-- Adjust Icon (SVG) -->
                <button @click="openAdjustmentModal(item)" class="btn-icon" title="Adjust Stock">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Adjustment Modal -->
    <div v-if="showAdjustmentModal" class="modal-overlay" @click.self="closeAdjustmentModal">
      <div class="modal-content glass-card small-modal">
        <div class="modal-header">
          <h2>
            {{ adjType === 'IN' ? 'Stock IN' : 'Stock OUT' }} 
            <span v-if="selectedItemFromTable">: {{ selectedItemFromTable.materialName }}</span>
          </h2>
          <button @click="closeAdjustmentModal" class="btn-close">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitAdjustment">
            <!-- Material Selector (only for top-level button) -->
            <div v-if="!selectedItemFromTable" class="form-group mb-4">
              <label>Select Material</label>
              <select v-model="selectedItemId" class="form-input" required>
                <option value="" disabled>-- Choose Material --</option>
                <option v-for="item in inventories" :key="item.id" :value="item.id">
                  {{ item.materialName }} ({{ item.sku }})
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Adjustment Type</label>
              <div class="type-selector">
                <button type="button" :class="{ active: adjType === 'IN' }" @click="adjType = 'IN'" class="btn-type-in">
                  <i class="fas fa-arrow-down"></i> Stock IN
                </button>
                <button type="button" :class="{ active: adjType === 'OUT' }" @click="adjType = 'OUT'" class="btn-type-out">
                  <i class="fas fa-arrow-up"></i> Stock OUT
                </button>
              </div>
            </div>
            
            <div class="form-group mt-4">
              <label>Quantity ({{ selectedItem?.unit }})</label>
              <input v-model.number="adjForm.quantity" type="number" step="0.001" class="form-input" required min="0.001" />
            </div>

            <div class="form-group mt-4">
              <label>Reference (Optional)</label>
              <input v-model="adjForm.reference" type="text" class="form-input" placeholder="e.g. INV-2026-001" />
            </div>

            <div class="form-group mt-4">
              <label>Notes</label>
              <textarea v-model="adjForm.notes" class="form-input" rows="2" placeholder="Describe this movement..."></textarea>
            </div>

            <div class="modal-footer mt-6">
              <button type="button" @click="closeAdjustmentModal" class="btn-cancel">Cancel</button>
              <button type="submit" class="btn-primary" :disabled="isSubmitting">
                {{ isSubmitting ? 'Processing...' : 'Confirm Adjustment' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- History Modal -->
    <div v-if="showLogsModal" class="modal-overlay" @click.self="showLogsModal = false">
      <div class="modal-content glass-card large-modal">
        <div class="modal-header">
          <h2>Stock History: {{ selectedItem.materialName }}</h2>
          <button @click="showLogsModal = false" class="btn-close">&times;</button>
        </div>
        <div class="modal-body scrollable">
          <table class="custom-table compact">
            <thead>
              <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Qty</th>
                <th>Reference</th>
                <th>Notes</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in selectedLogs" :key="log.id">
                <td>{{ formatDate(log.timestamp) }}</td>
                <td>
                  <span :class="['badge', log.type === 'IN' ? 'badge-success' : 'badge-danger']">
                    {{ log.type }}
                  </span>
                </td>
                <td class="font-mono">{{ log.quantity }}</td>
                <td>{{ log.reference || '-' }}</td>
                <td class="text-muted">{{ log.notes }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import api from '../services/api';

const inventories = ref([]);
const selectedItem = ref(null);
const selectedLogs = ref([]);
const showLogsModal = ref(false);
const showAdjustmentModal = ref(false);
const isQuickAdd = ref(false);
const isSubmitting = ref(false);
const adjType = ref('IN');
const selectedItemId = ref('');
const selectedItemFromTable = ref(null);
const adjForm = ref({
  quantity: 0,
  reference: '',
  notes: ''
});

const fetchInventory = async () => {
  try {
    const response = await api.get('/inventories');
    inventories.value = response.data.data;
  } catch (error) {
    console.error('Error fetching inventory:', error);
  }
};

const viewLogs = async (item) => {
  selectedItem.value = item;
  try {
    const response = await api.get(`/inventories/${item.id}/logs`);
    selectedLogs.value = response.data.data;
    showLogsModal.value = true;
  } catch (error) {
    console.error('Error fetching logs:', error);
  }
};

const openAdjustmentModal = (item) => {
  selectedItemFromTable.value = item;
  selectedItemId.value = item ? item.id : '';
  adjType.value = 'IN';
  adjForm.value = { quantity: 1, reference: '', notes: 'Manual adjustment' };
  showAdjustmentModal.value = true;
};

const closeAdjustmentModal = () => {
  showAdjustmentModal.value = false;
  selectedItemFromTable.value = null;
  selectedItemId.value = '';
};

const submitAdjustment = async () => {
  if (!selectedItemId.value) {
    alert('Please select a material first.');
    return;
  }
  
  isSubmitting.value = true;
  try {
    const endpoint = adjType.value === 'IN' ? 'add' : 'reduce';
    await api.post(`/inventories/${selectedItemId.value}/${endpoint}`, adjForm.value);
    await fetchInventory(); // Refresh data
    closeAdjustmentModal();
    alert('Stock updated successfully!');
  } catch (error) {
    const errorMsg = error.response?.data?.message || 'Failed to adjust stock.';
    alert('Error: ' + errorMsg);
  } finally {
    isSubmitting.value = false;
  }
};

const getStockClass = (item) => {
  if (item.stockQuantity <= item.minStock) return 'text-danger font-bold blink';
  if (item.stockQuantity <= item.minStock * 2) return 'text-warning font-bold';
  return 'text-success font-bold';
};

const getStatusBadgeClass = (item) => {
  if (item.stockQuantity <= item.minStock) return 'badge-danger';
  if (item.stockQuantity <= item.minStock * 2) return 'badge-warning';
  return 'badge-success';
};

const getStatusText = (item) => {
  if (item.stockQuantity <= item.minStock) return 'CRITICAL';
  if (item.stockQuantity <= item.minStock * 2) return 'LOW STOCK';
  return 'HEALTHY';
};

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('en-GB', {
    day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
  });
};

const handleEscKey = (e) => {
  if (e.key === 'Escape') {
    closeAdjustmentModal();
    showLogsModal.value = false;
  }
};

onMounted(() => {
  fetchInventory();
  window.addEventListener('keydown', handleEscKey);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleEscKey);
});
</script>

<style scoped>
.inventory-container {
  padding: 2rem;
  animation: fadeIn 0.5s ease-out;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  color: white;
  font-size: 2rem;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.glass-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
}

.custom-table {
  width: 100%;
  border-collapse: collapse;
  color: #e0e0e0;
}

.custom-table th {
  background: rgba(255, 255, 255, 0.1);
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.8rem;
  letter-spacing: 1px;
}

.custom-table td {
  padding: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
}

.stock-display {
  display: flex;
  align-items: baseline;
  gap: 0.4rem;
}

.unit-label {
  font-size: 0.7rem;
  opacity: 0.6;
}

.badge {
  padding: 0.4rem 0.8rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: bold;
}

.badge-success { background: rgba(76, 175, 80, 0.2); color: #81c784; }
.badge-warning { background: rgba(255, 152, 0, 0.2); color: #ffb74d; }
.badge-danger { background: rgba(244, 67, 54, 0.2); color: #e57373; }

.btn-icon {
  background: none;
  border: none;
  color: rgba(255,255,255,0.6);
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.3s;
  margin-right: 0.5rem;
}

.btn-icon:hover { 
  color: #fff; 
  transform: scale(1.2); 
}

.modal-overlay {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.large-modal { width: 800px; max-height: 80vh; }
.small-modal { width: 450px; }

.modal-header h2 { font-size: 1.2rem; color: white; margin: 0; }
.modal-body { padding: 1.5rem; }

.form-group label { display: block; color: rgba(255,255,255,0.7); font-size: 0.85rem; margin-bottom: 0.5rem; }
.form-input { 
  width: 100%; background: rgba(0,0,0,0.2); border: 1px solid rgba(255,255,255,0.1); 
  border-radius: 8px; color: white; padding: 0.8rem; outline: none; transition: 0.3s;
}
.form-input:focus { border-color: #3b82f6; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2); }

.type-selector { display: flex; gap: 1rem; }
.type-selector button { 
  flex: 1; padding: 0.8rem; border-radius: 8px; border: 1px solid rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.05); color: rgba(255,255,255,0.5); cursor: pointer; transition: 0.3s;
  display: flex; align-items: center; justify-content: center; gap: 0.5rem;
}

.btn-type-in.active { background: rgba(76, 175, 80, 0.2); color: #81c784; border-color: #81c784; }
.btn-type-out.active { background: rgba(244, 67, 54, 0.2); color: #e57373; border-color: #e57373; }

.modal-footer { display: flex; justify-content: flex-end; gap: 1rem; }
.btn-cancel { background: transparent; border: none; color: white; cursor: pointer; }
.btn-primary { 
  background: #3b82f6; color: white; border: none; padding: 0.8rem 1.5rem; 
  border-radius: 8px; cursor: pointer; font-weight: bold; transition: 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-primary:hover:not(:disabled) { background: #2563eb; transform: translateY(-2px); }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }

.btn-close { background: none; border: none; color: white; font-size: 1.5rem; cursor: pointer; opacity: 0.6; }
.btn-close:hover { opacity: 1; }

.mt-4 { margin-top: 1rem; }
.mt-6 { margin-top: 1.5rem; }

.text-danger { color: #ff5252; }
.text-warning { color: #ffd740; }
.text-success { color: #69f0ae; }

.blink { animation: blinker 1.5s linear infinite; }

@keyframes blinker { 50% { opacity: 0.3; } }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
</style>
