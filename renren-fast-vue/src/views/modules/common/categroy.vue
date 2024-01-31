<template>
  <el-tree
    :data="menus"
    :props="defaultProps"
    node-key="catId"
    ref="menuTree"
    @node-click="nodeClick"
  ></el-tree>
</template>

<script>
export default {
  data () {
    // 这里存放数据
    return {
      menus: [],
      expandedKey: [],
      defaultProps: {
        children: 'children', // 子节点
        label: 'name' // name属性作为标签的值，展示出来
      }
    }
  },
  // 方法集合
  methods: {
    getMenus () {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({data}) => {
        console.log('成功了获取到菜单数据....', data.menus)
        this.menus = data.menus
      })
    },

    nodeClick (data, node, component) {
      console.log('子组件categroy的节点被点击:', data, node, component)
      // 向父组件发送tree-node-click事件
      this.$emit('tree-node-click', data, node, component)
    }
  },
  // 生命周期 - 创建完成（可以访问当前this实例）
  created () {
    this.getMenus()
  }
}
</script>
