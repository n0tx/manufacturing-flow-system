<script setup>
import { computed } from 'vue';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { Line, Bar } from 'vue-chartjs';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

const props = defineProps({
  revenueData: { type: Array, default: () => [] },
  topProducts: { type: Array, default: () => [] }
});

// Revenue Chart Config
const revenueChartData = computed(() => ({
  labels: props.revenueData.map(d => d.month),
  datasets: [
    {
      label: 'Revenue (IDR)',
      backgroundColor: 'rgba(59, 130, 246, 0.2)',
      borderColor: '#3b82f6',
      pointBackgroundColor: '#fff',
      pointBorderColor: '#3b82f6',
      fill: true,
      tension: 0.4,
      data: props.revenueData.map(d => d.revenue)
    }
  ]
}));

const revenueOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: (context) => {
          return ' ' + new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR' }).format(context.raw);
        }
      }
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      grid: { color: 'rgba(255, 255, 255, 0.05)' },
      ticks: { 
        color: '#94a3b8',
        callback: (value) => {
          if (value >= 1000000) return (value / 1000000).toFixed(1) + 'jt';
          if (value === 0) return '0';
          return value.toLocaleString('id-ID');
        }
      }
    },
    x: {
      grid: { display: false },
      ticks: { color: '#94a3b8' }
    }
  }
};

// Top Products Chart Config
const productsChartData = computed(() => ({
  labels: props.topProducts.map(p => p.productName),
  datasets: [
    {
      label: 'Orders',
      backgroundColor: 'rgba(16, 185, 129, 0.6)',
      borderRadius: 6,
      data: props.topProducts.map(p => p.totalSold)
    }
  ]
}));

const productsOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false }
  },
  scales: {
    y: {
      beginAtZero: true,
      grid: { color: 'rgba(255, 255, 255, 0.05)' },
      ticks: { color: '#94a3b8', stepSize: 1 }
    },
    x: {
      grid: { display: false },
      ticks: { color: '#94a3b8' }
    }
  }
};
</script>

<template>
  <div class="charts-container">
    <div class="glass-card chart-box">
      <div class="chart-header">
        <h4>Tren Pendapatan (Revenue)</h4>
        <p>6 Bulan Terakhir</p>
      </div>
      <div class="chart-wrapper">
        <Line :data="revenueChartData" :options="revenueOptions" />
      </div>
    </div>

    <div class="glass-card chart-box">
      <div class="chart-header">
        <h4>Produk Terlaris</h4>
        <p>Berdasarkan Jumlah Pesanan</p>
      </div>
      <div class="chart-wrapper">
        <Bar :data="productsChartData" :options="productsOptions" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.charts-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 1.5rem;
  margin-top: 2rem;
}

.chart-box {
  padding: 1.5rem;
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.chart-header {
  margin-bottom: 1.5rem;
}

.chart-header h4 {
  margin: 0;
  font-size: 1.1rem;
  color: white;
}

.chart-header p {
  margin: 0.25rem 0 0;
  font-size: 0.8rem;
  color: #94a3b8;
}

.chart-wrapper {
  flex: 1;
  position: relative;
}

@media (max-width: 1024px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
}
</style>
