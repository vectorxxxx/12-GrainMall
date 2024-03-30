## 1、集群健康

```bash
GET /_cluster/health
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">{
  "cluster_name" : "elasticsearch",
  "status" : "yellow",
  "timed_out" : false,
  "number_of_nodes" : 1,
  "number_of_data_nodes" : 1,
  "active_primary_shards" : 8,
  "active_shards" : 8,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 5,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 61.53846153846154
}</code></pre></details>


## 2、分片

```bash
PUT /blogs{
	"settings" : {
		"number_of_shards" : 3,
		"number_of_replicas" : 1
	}
}
```



## 3、集群搭建

### 3.1、准备 docker 网络

```bash
# 防止 JVM 报错（临时测试，重启后需重新执行）
sysctl -w vm.max_map_count=262144

# 查看网络模式
docker network ls

# 创建一个新的 bridge 网络
docker network create \
--driver bridge \
--subnet=172.18.0.0/16 \
--gateway=172.18.1.1 mynet

# 查看网络信息
docker network inspect mynet
```

### 3.2、创建 Master 节点

```bash
for port in $(seq 1 3); \
do \
mkdir -p /mydata/elasticsearch/master-${port}/config
mkdir -p /mydata/elasticsearch/master-${port}/data
chmod -R 777 /mydata/elasticsearch/master-${port}
cat << EOF >/mydata/elasticsearch/master-${port}/config/elasticsearch.yml
cluster.name: my-es
node.master: true
node.data: false
network.host: 0.0.0.0
http.host: 0.0.0.0
http.port: 920${port}
transport.tcp.port: 930${port}
discovery.zen.minimum_master_nodes: 2
discovery.zen.ping_timeout: 10s
discovery.seed_hosts: ["172.18.12.21:9301", "172.18.12.22:9302", "172.18.12.23:9303"]
cluster.initial_master_nodes: ["172.18.12.21"]
EOF
docker run --name elasticsearch-node-${port} \
-p 920${port}:920${port} -p 930${port}:930${port} \
--network=mynet --ip 172.18.12.2${port} \
-e ES_JAVA_OPTS="-Xms300m -Xmx300m" \
-v /mydata/elasticsearch/master-${port}/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /mydata/elasticsearch/master-${port}/data:/usr/share/elasticsearch/data \
-v /mydata/elasticsearch/master-${port}/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.4.2
done
```

### 3.3、创建 Data-Node 节点

```bash
for port in $(seq 4 6); \
do \
mkdir -p /mydata/elasticsearch/node-${port}/config
mkdir -p /mydata/elasticsearch/node-${port}/data
chmod -R 777 /mydata/elasticsearch/node-${port}
cat << EOF >/mydata/elasticsearch/node-${port}/config/elasticsearch.yml
cluster.name: my-es
node.master: false
node.data: true
network.host: 0.0.0.0
http.host: 0.0.0.0
network.publish_host: 192.168.56.10
http.port: 920${port}
transport.tcp.port: 930${port}
discovery.zen.minimum_master_nodes: 2
discovery.zen.ping_timeout: 10s
discovery.seed_hosts: ["172.18.12.21:9301", "172.18.12.22:9302", "172.18.12.23:9303"]
cluster.initial_master_nodes: ["172.18.12.21"]
EOF
docker run --name elasticsearch-node-${port} \
-p 920${port}:920${port} -p 930${port}:930${port} \
--network=mynet --ip 172.18.12.2${port} \
-e ES_JAVA_OPTS="-Xms300m -Xmx300m" \
-v /mydata/elasticsearch/node-${port}/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /mydata/elasticsearch/node-${port}/data:/usr/share/elasticsearch/data \
-v /mydata/elasticsearch/node-${port}/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.4.2
done
```

### 3.4、测试集群

#### 3.4.1、查看节点状况

```bash
http://192.168.56.10:9201/_nodes/process?pretty
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">{
    "_nodes": {
        "total": 6,
        "successful": 6,
        "failed": 0
    },
    "cluster_name": "my-es",
    "nodes": {
        "aMUD-8_jR1KOQ0xQG1DXnw": {
            "name": "a4b0b2c50e83",
            "transport_address": "192.168.56.10:9304",
            "host": "192.168.56.10",
            "ip": "192.168.56.10",
            "version": "7.4.2",
            "build_flavor": "default",
            "build_type": "docker",
            "build_hash": "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
            "roles": [
                "ingest",
                "data",
                "ml"
            ],
            "attributes": {
                "ml.machine_memory": "3973541888",
                "ml.max_open_jobs": "20",
                "xpack.installed": "true"
            },
            "process": {
                "refresh_interval_in_millis": 1000,
                "id": 1,
                "mlockall": false
            }
        },
        "T0d38OGWTvCGrAV1OdnuXg": {
            "name": "178f1f250ed0",
            "transport_address": "192.168.56.10:9305",
            "host": "192.168.56.10",
            "ip": "192.168.56.10",
            "version": "7.4.2",
            "build_flavor": "default",
            "build_type": "docker",
            "build_hash": "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
            "roles": [
                "ingest",
                "data",
                "ml"
            ],
            "attributes": {
                "ml.machine_memory": "3973541888",
                "ml.max_open_jobs": "20",
                "xpack.installed": "true"
            },
            "process": {
                "refresh_interval_in_millis": 1000,
                "id": 1,
                "mlockall": false
            }
        },
        "J3cKqX6xS_GtQynqmBDXig": {
            "name": "6f15cbcee900",
            "transport_address": "172.18.12.21:9301",
            "host": "172.18.12.21",
            "ip": "172.18.12.21",
            "version": "7.4.2",
            "build_flavor": "default",
            "build_type": "docker",
            "build_hash": "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
            "roles": [
                "ingest",
                "master",
                "ml"
            ],
            "attributes": {
                "ml.machine_memory": "3973541888",
                "xpack.installed": "true",
                "ml.max_open_jobs": "20"
            },
            "process": {
                "refresh_interval_in_millis": 1000,
                "id": 1,
                "mlockall": false
            }
        },
        "fzQpmRviTgG3PuATVG8lQQ": {
            "name": "009e1602ea45",
            "transport_address": "192.168.56.10:9306",
            "host": "192.168.56.10",
            "ip": "192.168.56.10",
            "version": "7.4.2",
            "build_flavor": "default",
            "build_type": "docker",
            "build_hash": "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
            "roles": [
                "ingest",
                "data",
                "ml"
            ],
            "attributes": {
                "ml.machine_memory": "3973541888",
                "ml.max_open_jobs": "20",
                "xpack.installed": "true"
            },
            "process": {
                "refresh_interval_in_millis": 1000,
                "id": 1,
                "mlockall": false
            }
        },
        "SFogIp_LSAy45vYMBK2C-g": {
            "name": "d37422822f23",
            "transport_address": "172.18.12.22:9302",
            "host": "172.18.12.22",
            "ip": "172.18.12.22",
            "version": "7.4.2",
            "build_flavor": "default",
            "build_type": "docker",
            "build_hash": "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
            "roles": [
                "ingest",
                "master",
                "ml"
            ],
            "attributes": {
                "ml.machine_memory": "3973541888",
                "ml.max_open_jobs": "20",
                "xpack.installed": "true"
            },
            "process": {
                "refresh_interval_in_millis": 1000,
                "id": 1,
                "mlockall": false
            }
        },
        "OSOnjWQ3TsqFRvBtqAXSbQ": {
            "name": "668d49b37f38",
            "transport_address": "172.18.12.23:9303",
            "host": "172.18.12.23",
            "ip": "172.18.12.23",
            "version": "7.4.2",
            "build_flavor": "default",
            "build_type": "docker",
            "build_hash": "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
            "roles": [
                "ingest",
                "master",
                "ml"
            ],
            "attributes": {
                "ml.machine_memory": "3973541888",
                "ml.max_open_jobs": "20",
                "xpack.installed": "true"
            },
            "process": {
                "refresh_interval_in_millis": 1000,
                "id": 1,
                "mlockall": false
            }
        }
    }
}</code></pre></details>

#### 3.4.2、查看集群状态

```bash
http://192.168.56.10:9201/_cluster/stats?pretty
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">{
    "_nodes": {
        "total": 6,
        "successful": 6,
        "failed": 0
    },
    "cluster_name": "my-es",
    "cluster_uuid": "ogYF61y6R7qIyKRt0eedvQ",
    "timestamp": 1711682055674,
    "status": "green",
    "indices": {
        "count": 0,
        "shards": {},
        "docs": {
            "count": 0,
            "deleted": 0
        },
        "store": {
            "size_in_bytes": 0
        },
        "fielddata": {
            "memory_size_in_bytes": 0,
            "evictions": 0
        },
        "query_cache": {
            "memory_size_in_bytes": 0,
            "total_count": 0,
            "hit_count": 0,
            "miss_count": 0,
            "cache_size": 0,
            "cache_count": 0,
            "evictions": 0
        },
        "completion": {
            "size_in_bytes": 0
        },
        "segments": {
            "count": 0,
            "memory_in_bytes": 0,
            "terms_memory_in_bytes": 0,
            "stored_fields_memory_in_bytes": 0,
            "term_vectors_memory_in_bytes": 0,
            "norms_memory_in_bytes": 0,
            "points_memory_in_bytes": 0,
            "doc_values_memory_in_bytes": 0,
            "index_writer_memory_in_bytes": 0,
            "version_map_memory_in_bytes": 0,
            "fixed_bit_set_memory_in_bytes": 0,
            "max_unsafe_auto_id_timestamp": -9223372036854776000,
            "file_sizes": {}
        }
    },
    "nodes": {
        "count": {
            "total": 6,
            "coordinating_only": 0,
            "data": 3,
            "ingest": 6,
            "master": 3,
            "ml": 6,
            "voting_only": 0
        },
        "versions": [
            "7.4.2"
        ],
        "os": {
            "available_processors": 6,
            "allocated_processors": 6,
            "names": [
                {
                    "name": "Linux",
                    "count": 6
                }
            ],
            "pretty_names": [
                {
                    "pretty_name": "CentOS Linux 7 (Core)",
                    "count": 6
                }
            ],
            "mem": {
                "total_in_bytes": 23841251328,
                "free_in_bytes": 734416896,
                "used_in_bytes": 23106834432,
                "free_percent": 3,
                "used_percent": 97
            }
        },
        "process": {
            "cpu": {
                "percent": 124
            },
            "open_file_descriptors": {
                "min": 310,
                "max": 314,
                "avg": 312
            }
        },
        "jvm": {
            "max_uptime_in_millis": 171351,
            "versions": [
                {
                    "version": "13.0.1",
                    "vm_name": "OpenJDK 64-Bit Server VM",
                    "vm_version": "13.0.1+9",
                    "vm_vendor": "AdoptOpenJDK",
                    "bundled_jdk": true,
                    "using_bundled_jdk": true,
                    "count": 6
                }
            ],
            "mem": {
                "heap_used_in_bytes": 583767280,
                "heap_max_in_bytes": 1835139072
            },
            "threads": 161
        },
        "fs": {
            "total_in_bytes": 171710627840,
            "free_in_bytes": 128970014720,
            "available_in_bytes": 128970014720
        },
        "plugins": [],
        "network_types": {
            "transport_types": {
                "security4": 6
            },
            "http_types": {
                "security4": 6
            }
        },
        "discovery_types": {
            "zen": 6
        },
        "packaging_types": [
            {
                "flavor": "default",
                "type": "docker",
                "count": 6
            }
        ]
    }
}</code></pre></details>

#### 3.4.3、查看集群健康状况

```bash
http://192.168.56.10:9201/_cluster/health?pretty
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">{
    "cluster_name": "my-es",
    "status": "green",
    "timed_out": false,
    "number_of_nodes": 6,
    "number_of_data_nodes": 3,
    "active_primary_shards": 0,
    "active_shards": 0,
    "relocating_shards": 0,
    "initializing_shards": 0,
    "unassigned_shards": 0,
    "delayed_unassigned_shards": 0,
    "number_of_pending_tasks": 0,
    "number_of_in_flight_fetch": 0,
    "task_max_waiting_in_queue_millis": 0,
    "active_shards_percent_as_number": 100
}</code></pre></details>

#### 3.4.4、查看各个节点信息

```bash
http://192.168.56.10:9201/_cat/nodes
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">172.18.12.21  41 97 14 11.20 7.53 4.72 ilm * 6f15cbcee900
172.18.12.23  35 97 15 11.20 7.53 4.72 ilm - 668d49b37f38
192.168.56.10 27 97 14 10.87 7.40 4.67 dil - 009e1602ea45
172.18.12.22  33 97 13 11.20 7.53 4.72 ilm - d37422822f23
192.168.56.10 30 97 14 10.87 7.40 4.67 dil - a4b0b2c50e83
192.168.56.10 31 97 14 11.20 7.53 4.72 dil - 178f1f250ed0</code></pre></details>

#### 3.4.5、_cat


```bash
curl localhost:9201/_cat
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">=^.^=
/_cat/allocation
/_cat/shards
/_cat/shards/{index}
/_cat/master
/_cat/nodes
/_cat/tasks
/_cat/indices
/_cat/indices/{index}
/_cat/segments
/_cat/segments/{index}
/_cat/count
/_cat/count/{index}
/_cat/recovery
/_cat/recovery/{index}
/_cat/health
/_cat/pending_tasks
/_cat/aliases
/_cat/aliases/{alias}
/_cat/thread_pool
/_cat/thread_pool/{thread_pools}
/_cat/plugins
/_cat/fielddata
/_cat/fielddata/{fields}
/_cat/nodeattrs
/_cat/repositories
/_cat/snapshots/{repository}
/_cat/templates</code></pre></details>

#### 3.4.5、批量操作

```bash
docker restart $(docker ps -a |grep elasticsearch-node-* | awk '{ print $1}')

docker stop $(docker ps -a |grep elasticsearch-node-* | awk '{ print $1}')

docker rm $(docker ps -a |grep elasticsearch-node-* | awk '{ print $1}')
```

