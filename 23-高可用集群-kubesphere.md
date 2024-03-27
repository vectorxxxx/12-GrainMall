## 1、安装 Helm

```bash
# 方案一
curl -L https://git.io/get_helm.sh | bash

# 方案二（上述方式安装不了的情况下）
# 下载
wget https://get.helm.sh/helm-v2.16.3-linux-amd64.tar.gz
# 解压
tar -zxvf helm-v2.16.3-linux-amd64.tar.gz
# 移动
mv linux-amd64/helm /usr/local/bin/helm
mv linux-amd64/tiller /usr/local/bin/tiller

# 验证 helm
helm version
# Client: &version.Version{SemVer:"v2.16.3", GitCommit:"1ee0254c86d4ed6887327dabed7aa7da29d7eb0d", GitTreeState:"clean"}
# Server: &version.Version{SemVer:"v2.16.3", GitCommit:"1ee0254c86d4ed6887327dabed7aa7da29d7eb0d", GitTreeState:"clean"}

# 验证 tiller
tiller
# [main] 2024/03/26 14:04:40 Starting Tiller v2.16.3 (tls=false)
# [main] 2024/03/26 14:04:40 GRPC listening on :44134
# [main] 2024/03/26 14:04:40 Probes listening on :44135
# [main] 2024/03/26 14:04:40 Storage driver is ConfigMap
# [main] 2024/03/26 14:04:40 Max history per release is 0
```

**创建权限**

```bash
vi helm-rbac.yaml
```

`helm-rbac.yaml`

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: tiller
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects: 
  - kind: ServiceAccount
    name: tiller
    namespace: kube-system
```

应用 yaml

```yaml
kubectl apply -f helm-rbac.yaml
```



## 2、安装 Tiller

```bash
helm init --service-account tiller --upgrade \
-i registry.cn-hangzhou.aliyuncs.com/google_containers/tiller:v2.16.3 \
--stable-repo-url https://kubernetes.oss-cn-hangzhou.aliyuncs.com/charts
```



## 3、安装 OpenEBS

**准备工作**

```bash
# 去掉污点，污点会影响OpenEBS安装
kubectl describe node k8s-node1 | grep Taint
kubectl taint nodes k8s-node1 node-role.kubernetes.io/master:NoSchedule-
kubectl describe node k8s-node1 | grep Taint
```

**安装**

```bash
kubectl create ns openebs
kubectl apply -f openebs-operator-1.5.0.yaml
kubectl get sc

# 设置默认storageclass
kubectl patch storageclass openebs-hostpath -p \
'{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'

# 验证
kubectl get pods --all-namespaces
kubectl get pod -n openebs
kubectl get sc -n openebs
```



## 4、安装 Kubesphere

```bash
kubectl apply -f kubesphere-minimal.yaml

# 查看安装进度
kubectl logs -n kubesphere-system \
$(kubectl get pod -n kubesphere-system -l app=ks-install -o jsonpath='{.items[0].metadata.name}') -f

# 检查端口
kubectl get svc/ks-console -n kubesphere-system

# 验证
kubectl get ns
kubectl get pods -n kubesphere-system

# linux 访问
curl -L http://10.0.2.4:30880

# windows 访问
http://192.168.56.100:30880
Account: admin
Password: P@88w0rd
```



