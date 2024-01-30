<template>
  <div>
    <el-switch v-model="draggable" active-text="开启拖拽" inactive-text="关闭拖拽"/>
    <el-button v-if="draggable" @click="batchSave">批量保存</el-button>
    <el-button type="danger" @click="batchRemove">批量删除</el-button>
    <!-- 分类管理树节点  -->
    <el-tree
      :data="menus"
      :props="defaultProps"
      :expand-on-click-node="false"
      show-checkbox
      node-key="catId"
      :default-expanded-keys="expandedKeys"

      :draggable="draggable"
      :allow-drop="allowDrop"
      @node-drop="handleDrop"

      ref="menuTree">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            v-if="node.level <= 2"
            type="text"
            size="mini"
            @click="() => append(data)">
            Append
          </el-button>
          <el-button
            type="text"
            size="small"
            @click="() => edit(data)">
            Edit
          </el-button>
          <el-button
            v-if="node.childNodes.length === 0"
            type="text"
            size="mini"
            @click="() => remove(node, data)">
            Delete
          </el-button>
        </span>
      </span>
    </el-tree>
    <!-- 渲染写法 -->
    <!--<el-tree-->
    <!--  :data="menus"-->
    <!--  show-checkbox-->
    <!--  node-key="catId"-->
    <!--  expand-on-click-node="false"-->
    <!--  :default-expanded-keys="expandedKeys"-->
    <!--  :render-content="renderContent">-->
    <!--</el-tree>-->

    <!-- 对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" auto-complete="false"/>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="category.icon" auto-complete="false"/>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" auto-complete="false"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitData()">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      menus: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },

      // ======新增、修改======
      // 默认展开的节点id
      expandedKeys: [],
      // 显示对话框
      dialogVisible: false,
      // 分类信息
      category: {
        catId: null,
        name: '',
        parentCid: 0,
        catLevel: 0,
        showStatus: 1,
        sort: 0,
        icon: '',
        productUnit: '',
        productCount: 0
      },
      // 对话框标题
      title: '',
      // 对话框类型
      dialogType: '',

      // ======拖拽功能======
      // 开启拖拽功能
      draggable: false,
      // 最大层级
      maxLevel: 1,
      // 父节点id
      pCid: [],
      // 拖拽节点
      updateNodes: []
    }
  },

  // 生命周期 - 创建完成（可以访问当前this实例）
  created () {
    this.getMenus()
  },

  // 方法集合
  methods: {
    // 获取后台数据
    getMenus () {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({data}) => {
        this.menus = data.menus
        // console.log(this.menus)
      })
    },
    // 渲染节点
    // renderContent (h, {node, data, store}) {
    //   // 返回要显示的dom元素
    //   return (
    //     <span class="custom-tree-node">
    //       <span>{node.label}</span>
    //       <span>
    //         <el-button
    //           v-if="node.level <= 2"
    //           type="text"
    //           size="mini"
    //           on-click="{() => this.append(data)}">
    //           Append
    //         </el-button>
    //         <el-button
    //           type="text"
    //           size="small"
    //           on-click="{() => this.edit(data)}">
    //           Edit
    //         </el-button>
    //         <el-button
    //           v-if="node.childNodes.length === 0"
    //           type="text"
    //           size="mini"
    //           on-click="{() => this.remove(node, data)}">
    //         Delete
    //         </el-button>
    //       </span>
    //     </span>
    //   )
    // },
    // 新增节点
    append (data) {
      this.dialogType = 'add'
      this.title = '添加分类'
      this.dialogVisible = true
      // 初始化数据
      this.category.catId = null
      this.category.name = ''
      this.category.parentCid = data.catId
      this.category.catLevel = data.catLevel * 1 + 1
      this.category.showStatus = 1
      this.category.sort = 0
      this.category.icon = ''
      this.category.productUnit = ''
      this.category.productCount = 0
    },

    // 编辑节点
    edit (data) {
      this.dialogType = 'edit'
      this.title = '编辑分类'
      this.dialogVisible = true

      // 查询当前分类信息
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        method: 'get'
      }).then(({data}) => {
        // 初始化数据
        this.category.catId = data.category.catId
        this.category.name = data.category.name
        this.category.parentCid = data.category.parentCid
        this.category.catLevel = data.category.catLevel
        this.category.showStatus = data.category.showStatus
        this.category.sort = data.category.sort
        this.category.icon = data.category.icon
        this.category.productUnit = data.category.productUnit
        this.category.productCount = data.category.productCount
      })
    },

    // 提交数据
    submitData () {
      if (this.dialogType === 'add') {
        this.addCategory()
      } else {
        this.editCategory()
      }
    },

    // 新增分类
    addCategory () {
      this.$http({
        url: this.$http.adornUrl('/product/category/save'),
        method: 'post',
        data: this.$http.adornData(this.category)
      }).then(({data}) => {
        this.$message.success('新增成功')
        this.dialogVisible = false
        this.getMenus()
        this.expandedKeys = [this.category.parentCid]
      })
    },

    // 修改分类
    editCategory () {
      this.$http({
        url: this.$http.adornUrl('/product/category/update'),
        method: 'put',
        data: this.$http.adornData(this.category, false)
      }).then(({data}) => {
        this.$message.success('修改成功')
        this.dialogVisible = false
        this.getMenus()
        this.expandedKeys = [this.category.parentCid]
      })
    },

    // 删除节点
    remove (node, data) {
      const ids = [data.catId]
      this.$confirm(`是否删除${data.name}菜单？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'delete',
          data: this.$http.adornData(ids, false)
        })
      }).then(() => {
        this.$message.success('delete success')
        this.getMenus()
        // 默认展开父节点
        this.expandedKeys = [node.parent.data.catId]
      }).catch(err => {
        this.$message.error(err.message)
      })
    },

    // 允许拖拽节点
    allowDrop (draggingNode, dropNode, type) {
      // // 计算深度方式一：
      // // 计算拖拽节点最大层级
      // this.countNodeLevel(draggingNode.data)
      // // 计算当前节点深度
      // const deep = this.maxLevel - draggingNode.data.catLevel + 1
      // console.log('deep: ', deep)

      // 计算深度方式二：
      // 计算拖拽节点最大层级
      this.countNodeLevel(draggingNode)
      // 计算当前节点深度
      const deep = this.maxLevel - draggingNode.level + 1
      // console.log('deep: ', deep)

      // 判断拖拽到某个结点上还是两个结点之间
      if (type === 'inner') {
        // console.log(deep + dropNode.level)
        return deep + dropNode.level <= 3
      } else {
        // console.log(deep + dropNode.parent.level)
        return deep + dropNode.parent.level <= 3
      }
    },

    // 方式二：计算拖拽节点最大层级
    countNodeLevel (node) {
      const childNodes = node.childNodes
      if (childNodes != null && childNodes.length > 0) {
        for (let i = 0; i < childNodes.length; i++) {
          let childNode = childNodes[i]
          if (childNode.level > this.maxLevel) {
            this.maxLevel = childNode.level
          }
          this.countNodeLevel(childNode)
        }
      } else {
        this.maxLevel = node.level
      }
    },
    // 方式一：计算拖拽节点最大层级
    // countNodeLevel (category) {
    //   const children = category.children
    //   if (children != null && children.length > 0) {
    //     for (let i = 0; i < children.length; i++) {
    //       let child = children[i]
    //       if (child.catLevel > this.maxLevel) {
    //         this.maxLevel = child.catLevel
    //       }
    //       this.countNodeLevel(child)
    //     }
    //   } else {
    //     this.maxLevel = category.catLevel
    //   }
    // },

    // 拖拽结束
    handleDrop (draggingNode, dropNode, dropType, ev) {
      // 获取父节点id
      let pCid = 0
      // 获取兄弟节点
      let siblings
      if (dropType === 'before' || dropType === 'after') {
        pCid = dropNode.parent.data.catId ? dropNode.parent.data.catId : 0
        siblings = dropNode.parent.childNodes
      } else {
        pCid = dropNode.data.catId
        siblings = dropNode.childNodes
      }
      this.pCid.push(pCid)

      // 遍历siblings节点
      for (let i = 0; i < siblings.length; i++) {
        if (siblings[i].data.catId === draggingNode.data.catId) { // 如果当前遍历的节点是拖拽的节点
          // 如果当前节点层级发生变化，更新子节点的层级
          if (draggingNode.level !== dropNode.level) {
            this.updateChildNodeLevel(siblings[i].childNodes)
          }
          // 更新拖拽的节点
          this.updateNodes.push({
            catId: siblings[i].data.catId,
            sort: i,
            parentCid: pCid,
            catLevel: dropType === 'inner' ? siblings[i].level + 1 : siblings[i].level
          })
        } else {  // 如果当前遍历的节点不是拖拽的节点，即被影响的节点
          this.updateNodes.push({
            catId: siblings[i].data.catId,
            sort: i
          })
        }
      }
    },
    updateChildNodeLevel (childNodes) {
      if (!childNodes || childNodes.length <= 0) {
        return
      }
      for (let i = 0; i < childNodes.length; i++) {
        let childNode = childNodes[i]
        this.updateNodes.push({
          catId: childNode.data.catId,
          catLevel: childNode.level
        })
        this.updateChildNodeLevel(childNode.childNodes)
      }
    },

    // 批量保存
    batchSave () {
      this.$confirm('是否批量保存菜单？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log('updateNodes: ', this.updateNodes)
        this.$http({
          url: this.$http.adornUrl('/product/category/update/sort'),
          method: 'put',
          data: this.$http.adornData(this.updateNodes, false)
        }).then(() => {
          this.$message.success('保存成功')
          // 刷新菜单
          this.getMenus()
          // 默认展开更新的父节点
          this.expandedKeys = this.pCid
          // 重置数据
          this.updateNodes = []
          this.maxLevel = 0
          this.pCid = []
        })
      })
    },

    // 批量删除
    batchRemove () {
      const checkedNodes = this.$refs.menuTree.getCheckedNodes()
      console.log(checkedNodes)
      const categoryIds = checkedNodes.map(item => item.catId)
      const categoryNames = checkedNodes.map(item => item.name)
      this.pCid = checkedNodes.map(item => item.parentCid)
      this.$confirm(`是否批量删除【${categoryNames}】菜单？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'delete',
          data: this.$http.adornData(categoryIds, false)
        }).then(() => {
          this.$message.success('删除成功')
          this.getMenus()
          this.expandedKeys = this.pCid
          this.pCid = []
        })
      })
    }
  }
}
</script>
<style scoped></style>
