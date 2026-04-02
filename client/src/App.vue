<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

body {
  background-color: #f5f7fa;
  color: #333;
  font-size: 14px;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* Element Plus 组件定制 */
:deep(.el-card) {
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: none;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-dialog) {
  border-radius: 4px;
}

:deep(.el-message-box) {
  border-radius: 4px;
}

:deep(.el-button) {
  border-radius: 4px;
  font-size: 13px;
}

:deep(.el-input) {
  border-radius: 4px;
}

:deep(.el-select) {
  border-radius: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.el-aside) {
    width: 64px !important;
  }

  :deep(.el-menu-item span),
  :deep(.el-sub-menu__title span) {
    display: none;
  }

  :deep(.el-header) {
    padding: 0 10px !important;
  }

  :deep(.el-header h2) {
    font-size: 14px;
  }
}
</style>