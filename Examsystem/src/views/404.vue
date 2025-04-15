<template>
  <div class="container"> <!-- 新增包裹容器 -->
    <h1>404 not found</h1>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<style>
.container { /* 新增容器样式 */
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
}

h1 {
  font-size: 100px;
  color:  black;
  margin: 20px 0; /* 添加间距 */
}
</style>

<script>
export default {
  methods: {
    $router: undefined,
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>