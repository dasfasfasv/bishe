<template>
  <el-container>
    <el-header class="header">
      <div class="header-left">
        <img src="@/assets/logo.png" alt="logo" class="logo" v-if="false" />
        <h2>🏢 智慧园区访客管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-icon><UserFilled /></el-icon>
            {{ userStore.userInfo.realName }}
            <el-icon class="icon-down"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="changePassword">
                <el-icon><Edit /></el-icon>
                修改密码
              </el-dropdown-item>
              <el-dropdown-divider />
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container>
      <el-aside class="aside">
        <el-menu
          :default-active="$route.path"
          @select="handleMenuSelect"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <!-- 仪表板 -->
          <el-menu-item index="/dashboard">
            <el-icon><Monitor /></el-icon>
            <span>仪表板</span>
          </el-menu-item>

          <!-- 数据分析 -->
          <el-menu-item index="/analysis">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据分析</span>
          </el-menu-item>

          <!-- 业务管理 -->
          <el-sub-menu index="business">
            <template #title>
              <el-icon><Management /></el-icon>
              <span>业务管理</span>
            </template>

            <el-menu-item index="/visitor">
              <el-icon><User /></el-icon>
              <span>访客管理</span>
            </el-menu-item>

            <el-menu-item index="/appointment">
              <el-icon><Calendar /></el-icon>
              <span>预约管理</span>
            </el-menu-item>

            <el-menu-item index="/approval">
              <el-icon><CircleCheck /></el-icon>
              <span>审批管理</span>
            </el-menu-item>

            <el-menu-item index="/access">
              <el-icon><Key /></el-icon>
              <span>门禁授权</span>
            </el-menu-item>

            <el-menu-item index="/record">
              <el-icon><Monitor /></el-icon>
              <span>通行记录</span>
            </el-menu-item>

            <el-menu-item index="/blacklist">
              <el-icon><Warning /></el-icon>
              <span>黑名单管理</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 系统管理 -->
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>

            <el-menu-item index="/system/user">
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </el-menu-item>

            <el-menu-item index="/system/role">
              <el-icon><CircleFill /></el-icon>
              <span>角色管理</span>
            </el-menu-item>

            <el-menu-item index="/system/permission">
              <el-icon><SuccessFilled /></el-icon>
              <span>权限管理</span>
            </el-menu-item>

            <el-menu-item index="/system/department">
              <el-icon><OfficeBuilding /></el-icon>
              <span>部门管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <el-main class="main">
        <el-backtop />
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/modules/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
    }).catch(() => {})
  } else if (command === 'changePassword') {
    router.push('/system/change-password')
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 20px;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 4px;
}

.header-right .user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.header-right .user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.icon-down {
  font-size: 12px;
}

.aside {
  width: 200px;
  background-color: #545c64;
  border-right: 1px solid #ddd;
  overflow-y: auto;
}

.main {
  padding: 0;
  background-color: #f5f7fa;
}

:deep(.el-menu) {
  border: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  padding: 0 20px;
  height: 50px;
  line-height: 50px;
  transition: all 0.3s;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #3f464e !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #ffd04b !important;
  color: #545c64 !important;
}
</style>