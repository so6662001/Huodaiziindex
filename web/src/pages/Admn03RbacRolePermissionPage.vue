<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const loading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const successMsg = ref('')
const selected = ref(null)

const query = reactive({
  adminToken: 'test-admin-token',
  keyword: '',
  status: '',
  page: 1,
  pageSize: 10
})

const state = reactive({
  records: [],
  total: 0,
  page: 1,
  pageSize: 10,
  activeCount: 0,
  disabledCount: 0,
  keyword: ''
})

const createForm = reactive({
  roleCode: '',
  roleName: '',
  description: '',
  permissionCodesText: 'DASHBOARD_VIEW',
  operator: 'admn03-admin'
})

const permissionForm = reactive({
  permissionCodesText: '',
  operator: 'admn03-admin'
})

const canLoad = computed(() => query.adminToken.trim().length > 0)
const canCreate = computed(() => createForm.roleCode.trim() && createForm.roleName.trim())
const canUpdatePermission = computed(() => selected.value && permissionForm.permissionCodesText.trim())
const totalPages = computed(() => Math.max(Math.ceil(state.total / query.pageSize), 1))

function splitPermissionCodes(raw) {
  return Array.from(
    new Set(
      raw
        .split(/[\s,，;\n]+/)
        .map((item) => item.trim().toUpperCase())
        .filter(Boolean)
    )
  )
}

async function loadList() {
  if (!canLoad.value || loading.value) return
  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const params = new URLSearchParams()
    if (query.keyword.trim()) params.set('keyword', query.keyword.trim())
    if (query.status) params.set('status', query.status)
    params.set('page', String(query.page))
    params.set('pageSize', String(query.pageSize))
    const resp = await fetch(`/api/admin/rbac/roles?${params.toString()}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `角色列表加载失败(${resp.status})`)
    }
    const data = json.data || {}
    state.records = Array.isArray(data.records) ? data.records : []
    state.total = Number(data.total || 0)
    state.page = Number(data.page || query.page)
    state.pageSize = Number(data.pageSize || query.pageSize)
    state.activeCount = Number(data.activeCount || 0)
    state.disabledCount = Number(data.disabledCount || 0)
    state.keyword = data.keyword || query.keyword
    if (selected.value && !state.records.some((item) => item.roleCode === selected.value.roleCode)) {
      selected.value = null
    }
  } catch (error) {
    errorMsg.value = error.message || '角色列表加载失败'
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  if (!row?.roleCode || loading.value) return
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const target = state.records.find((item) => item.roleCode === row.roleCode)
    if (!target) return
    const listResp = await fetch(`/api/admin/rbac/roles?keyword=${encodeURIComponent(target.roleCode)}&page=1&pageSize=50`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const listJson = await listResp.json()
    if (!listResp.ok || listJson.code !== '0') {
      throw new Error(listJson.message || `角色详情查询失败(${listResp.status})`)
    }
    const records = listJson.data?.records || []
    const matched = records.find((item) => item.roleCode === target.roleCode)
    if (!matched) {
      throw new Error('未找到对应角色记录')
    }
    const detailResp = await fetch(`/api/admin/rbac/roles/${matched.roleId || matched.roleCode}`, {
      headers: { 'X-Admin-Token': query.adminToken.trim() }
    })
    const detailJson = await detailResp.json()
    if (!detailResp.ok || detailJson.code !== '0') {
      throw new Error(detailJson.message || `角色详情加载失败(${detailResp.status})`)
    }
    selected.value = detailJson.data || null
    permissionForm.permissionCodesText = (selected.value?.permissionCodes || []).join(', ')
  } catch (error) {
    errorMsg.value = error.message || '角色详情加载失败'
  }
}

async function createOrUpdateRole() {
  if (!canCreate.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      roleCode: createForm.roleCode.trim().toUpperCase(),
      roleName: createForm.roleName.trim(),
      description: createForm.description.trim() || null,
      permissionCodes: splitPermissionCodes(createForm.permissionCodesText),
      operator: createForm.operator.trim() || 'admn03-admin'
    }
    const resp = await fetch('/api/admin/rbac/roles', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `角色保存失败(${resp.status})`)
    }
    selected.value = json.data || null
    permissionForm.permissionCodesText = (selected.value?.permissionCodes || []).join(', ')
    successMsg.value = '角色已保存'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '角色保存失败'
  } finally {
    submitting.value = false
  }
}

async function updatePermissions() {
  if (!canUpdatePermission.value || submitting.value) return
  submitting.value = true
  errorMsg.value = ''
  successMsg.value = ''
  try {
    const payload = {
      permissionCodes: splitPermissionCodes(permissionForm.permissionCodesText),
      operator: permissionForm.operator.trim() || 'admn03-admin'
    }
    const resp = await fetch(`/api/admin/rbac/roles/${selected.value.roleId}/permissions`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Admin-Token': query.adminToken.trim()
      },
      body: JSON.stringify(payload)
    })
    const json = await resp.json()
    if (!resp.ok || json.code !== '0') {
      throw new Error(json.message || `权限更新失败(${resp.status})`)
    }
    selected.value = json.data || null
    permissionForm.permissionCodesText = (selected.value?.permissionCodes || []).join(', ')
    successMsg.value = '角色权限已更新'
    await loadList()
  } catch (error) {
    errorMsg.value = error.message || '权限更新失败'
  } finally {
    submitting.value = false
  }
}

function prevPage() {
  if (query.page <= 1 || loading.value) return
  query.page -= 1
  loadList()
}

function nextPage() {
  if (query.page >= totalPages.value || loading.value) return
  query.page += 1
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <main class="admn03-page">
    <section class="card hero">
      <h1>ADM-N03 角色权限管理（RBAC）</h1>
      <p>统一维护管理端角色、权限集合与职责边界，支持角色创建与权限变更。</p>
    </section>

    <section class="card">
      <div class="filters">
        <label>
          Admin Token
          <input v-model="query.adminToken" placeholder="请输入X-Admin-Token" />
        </label>
        <label>
          状态
          <select v-model="query.status">
            <option value="">全部</option>
            <option value="ACTIVE">启用</option>
            <option value="DISABLED">禁用</option>
          </select>
        </label>
        <label class="span-2">
          关键词
          <input v-model="query.keyword" placeholder="roleCode/roleName/描述" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canLoad || loading" @click="loadList">
          {{ loading ? '加载中...' : '查询角色' }}
        </button>
      </div>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="ok">{{ successMsg }}</p>
    </section>

    <section class="stats">
      <article class="stat"><span>角色总数</span><strong>{{ state.total }}</strong></article>
      <article class="stat"><span>启用角色</span><strong>{{ state.activeCount }}</strong></article>
      <article class="stat"><span>禁用角色</span><strong>{{ state.disabledCount }}</strong></article>
      <article class="stat"><span>当前页</span><strong>{{ query.page }}</strong></article>
    </section>

    <section class="card">
      <h2>角色列表</h2>
      <p v-if="loading">加载中...</p>
      <p v-else-if="state.records.length === 0">暂无角色记录</p>
      <ul v-else class="list">
        <li v-for="row in state.records" :key="row.roleCode" class="item">
          <div>
            <h3>{{ row.roleName }}（{{ row.roleCode }}）</h3>
            <p>状态：{{ row.statusText }} ｜ 类型：{{ row.systemRole ? '系统角色' : '自定义角色' }}</p>
            <p>权限数：{{ row.permissionCount }} ｜ 关联账号：{{ row.userCount }}</p>
            <p>说明：{{ row.roleDesc || '-' }}</p>
            <p>更新人：{{ row.updatedBy || '-' }} ｜ 更新时间：{{ row.updatedAt || '-' }}</p>
          </div>
          <button class="btn" @click="openDetail(row)">查看详情</button>
        </li>
      </ul>
      <div class="actions" v-if="state.total > 0">
        <button class="btn" :disabled="query.page <= 1 || loading" @click="prevPage">上一页</button>
        <button class="btn" :disabled="query.page >= totalPages || loading" @click="nextPage">下一页</button>
        <span>第 {{ query.page }} 页 / 共 {{ totalPages }} 页</span>
      </div>
    </section>

    <section class="card">
      <h2>新建/更新角色</h2>
      <div class="form-grid">
        <label>
          角色编码
          <input v-model="createForm.roleCode" placeholder="示例：OPS_AUDITOR" />
        </label>
        <label>
          角色名称
          <input v-model="createForm.roleName" placeholder="示例：运营审计员" />
        </label>
        <label class="span-2">
          描述
          <input v-model="createForm.description" placeholder="角色职责说明" />
        </label>
        <label class="span-2">
          权限编码（逗号分隔）
          <textarea v-model="createForm.permissionCodesText" rows="2" />
        </label>
      </div>
      <div class="actions">
        <button class="btn btn--primary" :disabled="!canCreate || submitting" @click="createOrUpdateRole">
          {{ submitting ? '提交中...' : '保存角色' }}
        </button>
      </div>
    </section>

    <section class="card" v-if="selected">
      <h2>角色详情与权限变更</h2>
      <p>角色ID：{{ selected.roleId }}</p>
      <p>角色：{{ selected.roleName }}（{{ selected.roleCode }}）</p>
      <p>类型：{{ selected.roleTypeText }} ｜ 状态：{{ selected.statusText }}</p>
      <p>账号绑定数：{{ selected.userCount }} ｜ 可用动作：{{ (selected.availableActions || []).join(' / ') }}</p>
      <p>当前权限：{{ (selected.permissionNames || []).join('，') || '-' }}</p>

      <div class="review-box">
        <label>
          权限编码（逗号分隔）
          <textarea v-model="permissionForm.permissionCodesText" rows="3" />
        </label>
        <button class="btn btn--primary" :disabled="!canUpdatePermission || submitting" @click="updatePermissions">
          {{ submitting ? '提交中...' : '更新权限' }}
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.admn03-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 14px;
}
.card {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 12px;
  padding: 14px;
  margin-bottom: 12px;
}
.hero {
  background: linear-gradient(135deg, #fff7ed, #ffedd5);
}
.hero h1 {
  margin: 0 0 8px;
}
.hero p {
  margin: 0;
  color: #4b5563;
}
.filters,
.form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}
.span-2 {
  grid-column: span 2;
}
label {
  display: grid;
  gap: 6px;
}
input,
select,
textarea {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 9px 10px;
  font: inherit;
}
.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
}
.btn {
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  padding: 8px 12px;
  cursor: pointer;
}
.btn--primary {
  border-color: #f57c00;
  background: #f57c00;
  color: #fff;
}
.error {
  color: #b42318;
}
.ok {
  color: #0c7a43;
}
.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}
.stat {
  background: #fff;
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
  display: grid;
  gap: 6px;
}
.stat span {
  color: #6b7280;
}
.stat strong {
  font-size: 24px;
}
.list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 10px;
}
.item {
  border: 1px solid #ececec;
  border-radius: 10px;
  padding: 10px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
}
.item h3 {
  margin: 0 0 8px;
}
.item p {
  margin: 5px 0;
}
.review-box {
  margin-top: 10px;
  display: grid;
  gap: 10px;
}
@media (max-width: 960px) {
  .filters,
  .form-grid,
  .stats {
    grid-template-columns: 1fr;
  }
  .span-2 {
    grid-column: span 1;
  }
  .item {
    flex-direction: column;
  }
}
</style>
