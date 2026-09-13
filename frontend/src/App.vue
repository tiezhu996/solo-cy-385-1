<template>
  <main>
    <header>
      <h1>宝宝成长记录</h1>
      <p v-if="currentBaby">{{ currentBaby.name }} {{ ageText }}</p>
      <p v-else>还没有宝宝档案</p>
    </header>

    <van-tabs
      v-if="babies.length"
      v-model:active="activeBabyId"
      sticky
      @change="onBabyChange"
    >
      <van-tab v-for="baby in babies" :key="baby.id" :name="baby.id" :title="baby.name" />
    </van-tabs>

    <section class="card">
      <h2>生长曲线</h2>
      <div ref="growthChart" class="chart"></div>
    </section>

    <section class="card">
      <h2>疫苗提醒</h2>
      <van-empty
        v-if="!vaccines.length"
        image-size="90"
        :description="currentBaby ? '还没有疫苗计划，添加后这里会提醒' : '请先创建宝宝档案'"
      />
      <van-cell v-for="item in sortedVaccines" :key="item.id" :title="item.vaccineName" :value="item.plannedDate">
        <template #label>
          <van-tag :type="statusTagType(item.status)">{{ item.status }}</van-tag>
        </template>
      </van-cell>
    </section>

    <section class="card">
      <h2>辅食推荐</h2>
      <van-cell v-for="food in foods" :key="food" :title="food" value="适合 9-12 个月" />
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { showToast } from 'vant';
import * as echarts from 'echarts';
import { fetchBabies, fetchVaccines } from './api';
import type { Baby, VaccineRecord, VaccineStatus } from './types';

const growthChart = ref<HTMLElement>();
const babies = ref<Baby[]>([]);
const activeBabyId = ref<number>(0);
const vaccines = ref<VaccineRecord[]>([]);
const foods = ['南瓜米糊', '鳕鱼土豆泥', '苹果燕麦粥'];

const currentBaby = computed(() => babies.value.find(b => b.id === activeBabyId.value));

const ageText = computed(() => {
  if (!currentBaby.value?.birthday) return '';
  const birth = new Date(currentBaby.value.birthday);
  const now = new Date();
  let months = (now.getFullYear() - birth.getFullYear()) * 12 + (now.getMonth() - birth.getMonth());
  if (now.getDate() < birth.getDate()) months -= 1;
  if (months < 0) months = 0;
  const years = Math.floor(months / 12);
  const rest = months % 12;
  if (years === 0) return `${rest} 个月`;
  return rest === 0 ? `${years} 岁` : `${years} 岁 ${rest} 个月`;
});

const STATUS_ORDER: Record<VaccineStatus, number> = { '已逾期': 0, '待接种': 1, '已接种': 2 };

const sortedVaccines = computed(() =>
  [...vaccines.value].sort((a, b) => {
    const diff = STATUS_ORDER[a.status] - STATUS_ORDER[b.status];
    return diff !== 0 ? diff : a.plannedDate.localeCompare(b.plannedDate);
  })
);

function statusTagType(status: VaccineStatus) {
  if (status === '已接种') return 'success';
  if (status === '已逾期') return 'danger';
  return 'warning';
}

async function loadVaccines(babyId: number) {
  try {
    vaccines.value = await fetchVaccines(babyId);
  } catch (e) {
    vaccines.value = [];
    showToast('疫苗记录加载失败');
  }
}

function onBabyChange(name: string | number) {
  vaccines.value = [];
  loadVaccines(Number(name));
}

onMounted(async () => {
  const chart = echarts.init(growthChart.value!);
  chart.setOption({ legend: {}, xAxis: { data: ['6月','7月','8月','9月','10月'] }, yAxis: {}, series: [{ name: '体重kg', type: 'line', data: [7.5,7.9,8.2,8.6,9.1] }, { name: '身高cm', type: 'line', data: [66,68,70,72,74] }] });

  try {
    babies.value = await fetchBabies();
  } catch (e) {
    showToast('宝宝档案加载失败');
    return;
  }
  if (babies.value.length) {
    activeBabyId.value = babies.value[0].id;
    loadVaccines(activeBabyId.value);
  }
});
</script>
