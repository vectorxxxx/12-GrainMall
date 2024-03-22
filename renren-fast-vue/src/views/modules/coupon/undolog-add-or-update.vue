<template>
  <el-dialog
    :title="!dataForm.branchId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="global transaction id" prop="xid">
      <el-input v-model="dataForm.xid" placeholder="global transaction id"></el-input>
    </el-form-item>
    <el-form-item label="undo_log context,such as serialization" prop="context">
      <el-input v-model="dataForm.context" placeholder="undo_log context,such as serialization"></el-input>
    </el-form-item>
    <el-form-item label="rollback info" prop="rollbackInfo">
      <el-input v-model="dataForm.rollbackInfo" placeholder="rollback info"></el-input>
    </el-form-item>
    <el-form-item label="0:normal status,1:defense status" prop="logStatus">
      <el-input v-model="dataForm.logStatus" placeholder="0:normal status,1:defense status"></el-input>
    </el-form-item>
    <el-form-item label="create datetime" prop="logCreated">
      <el-input v-model="dataForm.logCreated" placeholder="create datetime"></el-input>
    </el-form-item>
    <el-form-item label="modify datetime" prop="logModified">
      <el-input v-model="dataForm.logModified" placeholder="modify datetime"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          branchId: 0,
          xid: '',
          context: '',
          rollbackInfo: '',
          logStatus: '',
          logCreated: '',
          logModified: ''
        },
        dataRule: {
          xid: [
            { required: true, message: 'global transaction id不能为空', trigger: 'blur' }
          ],
          context: [
            { required: true, message: 'undo_log context,such as serialization不能为空', trigger: 'blur' }
          ],
          rollbackInfo: [
            { required: true, message: 'rollback info不能为空', trigger: 'blur' }
          ],
          logStatus: [
            { required: true, message: '0:normal status,1:defense status不能为空', trigger: 'blur' }
          ],
          logCreated: [
            { required: true, message: 'create datetime不能为空', trigger: 'blur' }
          ],
          logModified: [
            { required: true, message: 'modify datetime不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.branchId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.branchId) {
            this.$http({
              url: this.$http.adornUrl(`/coupon/undolog/info/${this.dataForm.branchId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.xid = data.undoLog.xid
                this.dataForm.context = data.undoLog.context
                this.dataForm.rollbackInfo = data.undoLog.rollbackInfo
                this.dataForm.logStatus = data.undoLog.logStatus
                this.dataForm.logCreated = data.undoLog.logCreated
                this.dataForm.logModified = data.undoLog.logModified
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/coupon/undolog/${!this.dataForm.branchId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'branchId': this.dataForm.branchId || undefined,
                'xid': this.dataForm.xid,
                'context': this.dataForm.context,
                'rollbackInfo': this.dataForm.rollbackInfo,
                'logStatus': this.dataForm.logStatus,
                'logCreated': this.dataForm.logCreated,
                'logModified': this.dataForm.logModified
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
