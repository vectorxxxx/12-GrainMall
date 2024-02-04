# ElasticSearch

## 一、Docker 安装 Es

### 0、查看磁盘内存大小是否足够

```bash
# 查看内存使用情况
free -m

# 磁盘分布使用情况
df -h
```

### 1、安装 ElasticSearch（存储和检索数据）

```bash
# 下载镜像文件
docker pull elasticsearch:7.4.2

# 初始化配置
mkdir -p /mydata/elasticsearch/config
mkdir -p /mydata/elasticsearch/data
# 允许被所有IP来源的机器访问
echo "http.host: 0.0.0.0" >> /mydata/elasticsearch/config/elasticsearch.yml
# 递归更改权限
chmod -R 777 /mydata/elasticsearch/

# 运行 elasticsearch 镜像实例
# 测试环境下，必须设置ES的初始内存和最大内存，否则默认占用内存过大会启动不了ES
docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
-e "discovery.type=single-node" \
-e ES_JAVA_OPTS="-Xms64m -Xmx512m" \
-v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
-v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
-v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
-d elasticsearch:7.4.2

# 查看启动日志
docker logs elasticsearch

# 容器自启动
docker update elasticsearch --restart=always
docker restart elasticsearch
```

postman 测试1（以下所有的 192.168.56.10 换成自己虚拟机中 Linux 对应的 IP 就行）

```
GET http://192.168.56.10:9200
```

结果

```json
{
    "name": "da3513272cce",
    "cluster_name": "elasticsearch",
    "cluster_uuid": "P29062S_Tg-tiiircc967Q",
    "version": {
        "number": "7.4.2",
        "build_flavor": "default",
        "build_type": "docker",
        "build_hash": "2f90bbf7b93631e52bafb59b3b049cb44ec25e96",
        "build_date": "2019-10-28T20:40:44.881551Z",
        "build_snapshot": false,
        "lucene_version": "8.2.0",
        "minimum_wire_compatibility_version": "6.8.0",
        "minimum_index_compatibility_version": "6.0.0-beta1"
    },
    "tagline": "You Know, for Search"
}
```

### 2、安装 Kibana（可视化检索数据）

```bash
# 下载镜像文件
docker pull kibana:7.4.2

# 运行 kibana 镜像实例
docker run --name kibana -p 5601:5601 \
-e ELASTICSEARCH_HOSTS=http://192.168.56.10:9200 \
-d kibana:7.4.2

# 查看启动日志
docker logs kibana

# 容器自启动
docker update kibana --restart=always
docker restart kibana

# 访问网址验证
http://192.168.56.10:5601/
```

## 二、基本概念

### 1、Index（索引）

动词，相当于 MySQL 中的 insert； 

名词，相当于 MySQL 中的 Database 

### 2、Type（类型）

在 Index（索引）中，可以定义一个或多个类型。

类似于 MySQL 中的 Table；每一种类型的数据放在一起

### 3、Document（文档）

保存在某个索引（Index）下某种类型（Type）的一个数据（Document），文档是 JSON 格式的，Document 就像是 MySQL 中的某个 Table

### 4、倒排索引机制

| 词     | 记录      |
| ------ | --------- |
| 红海   | 1,2,3,4,5 |
| 行动   | 1,2,3     |
| 探索   | 2,5       |
| 特别   | 3,5       |
| 纪录片 | 4         |
| 特工   | 5         |

## 三、初步检索

可以使用 Postman 进行测试，不过我这里直接使用了 Kibana Dev Tools Console

访问地址：[http://192.168.56.10:5601/app/kibana#/dev_tools/console](http://192.168.56.10:5601/app/kibana#/dev_tools/console)

### 1、_cat

#### 1.1、nodes（查看所有节点）

```bash
GET _cat/nodes
```

返回结果

```bash
127.0.0.1 13 99 55 1.65 2.09 0.98 dilm * da3513272cce
```

#### 1.2、master（查看主节点）

```bash
GET _cat/master
```

返回结果

```json
Y9qPLGnNQGuydgJrapcAog 127.0.0.1 127.0.0.1 da3513272cce
```

#### 1.3、health（查看健康状况）

```bash
GET _cat/health
```

返回结果

```bash
1706968540 13:55:40 elasticsearch green 1 1 3 3 0 0 0 0 - 100.0%
```

#### 1.4、indices（查看所有索引）

```bash
GET _cat/indices
```

返回结果

```bash
green open .kibana_task_manager_1   P05muSbUReerrEZhQTOU4w 1 0 2 0 12.5kb 12.5kb
green open .apm-agent-configuration 87QWwx0NRiWzvZfNTfW1AQ 1 0 0 0   283b   283b
green open .kibana_1                ytl495TjQMGYkm6y078-KQ 1 0 8 0   19kb   19kb
```

### 2、索引一个文档（保存）

#### 2.1、PUT 请求（指定 ID）✔️

PUT 请求必须指定 ID，有则更新无则新增

```bash
PUT customer/external/1
{
    "name": "John Doe"
}
```

第一次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
}
```

第二次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 2,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 1,
    "_primary_term": 1
}
```

#### 2.2、PUT 请求（不指定 ID）❌

```bash
PUT customer/external
{
    "name": "John Doe"
}
```

返回结果

```json
{
    "error": "Incorrect HTTP method for uri [/customer/external] and method [PUT], allowed: [POST]",
    "status": 405
}
```

#### 2.3、POST 请求（指定ID）✔️

POST 请求如果指定 id，则与 PUT 请求同理

```bash
POST customer/external/3
{
    "name": "John Doe"
}
```

第一次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "3",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 10,
    "_primary_term": 1
}
```

第二次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "3",
    "_version": 2,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 11,
    "_primary_term": 1
}
```

#### 2.4、POST 请求（不指定ID）✔️

POST 请求如果不指定 id，每次都会自动生成一个新的 id

```bash
POST customer/external
{
    "name": "John Doe"
}
```

第一次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "ZX9Sb40BvPJJ6zkeBLzb",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 2,
    "_primary_term": 1
}
```

第二次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "Zn9Sb40BvPJJ6zkef7zJ",
    "_version": 1,
    "result": "created",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 3,
    "_primary_term": 1
}
```

### 3、查询文档

```bash
GET customer/external/1
```

返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 3,
    "_seq_no": 4,
    "_primary_term": 1,
    "found": true,
    "_source": {
        "name": "John Doe"
    }
}
```

#### 3.1、乐观锁

更新携带 `?if_seq_no=&if_primary_term=`

```bash
PUT customer/external/1?if_seq_no=4&if_primary_term=1
{
    "name": "John Doe 4"
}
```

第一次更新返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 4,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 7,
    "_primary_term": 1
}
```

第二次更新返回结果

```json
{
    "error": {
        "root_cause": [
            {
                "type": "version_conflict_engine_exception",
                "reason": "[1]: version conflict, required seqNo [4], primary term [1]. current document has seqNo [7] and primary term [1]",
                "index_uuid": "VTgKN85CQzi92HabAadvKQ",
                "shard": "0",
                "index": "customer"
            }
        ],
        "type": "version_conflict_engine_exception",
        "reason": "[1]: version conflict, required seqNo [4], primary term [1]. current document has seqNo [7] and primary term [1]",
        "index_uuid": "VTgKN85CQzi92HabAadvKQ",
        "shard": "0",
        "index": "customer"
    },
    "status": 409
}
```

修改携带参数 `if_seq_no` 的值为最新的 `"_seq_no"` 的值，即错误提示的 [7]

```bash
PUT customer/external/1?if_seq_no=7&if_primary_term=1
{
    "name": "John Doe 4"
}
```

返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 5,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 8,
    "_primary_term": 1
}
```

### 4、更新文档

PUT 和 POST 请求也可以更新文档，在上面《3.2、索引一个文档（保存）》中已介绍，这里不在赘述，仅举例携带 `_update` 时的操作

**不增加属性**

- 不带 _update，文档 version 每次都会增加
- 带 _update 对比元数据如果一样就不进行任何操作，文档 version 不增加

```bash
POST customer/external/1/_update
{
    "doc": {
        "name": "John Doew"
    }
}
```

第一次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 6,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 12,
    "_primary_term": 2
}
```

第二次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 6,
    "result": "noop",
    "_shards": {
        "total": 0,
        "successful": 0,
        "failed": 0
    },
    "_seq_no": 12,
    "_primary_term": 2
}
```

**增加属性**

```bash
POST customer/external/1/_update
{
    "doc": {
        "name": "Jane Doe",
        "age": 20
    }
}
```

第一次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 7,
    "result": "updated",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 18,
    "_primary_term": 2
}
```

第二次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 7,
    "result": "noop",
    "_shards": {
        "total": 0,
        "successful": 0,
        "failed": 0
    },
    "_seq_no": 18,
    "_primary_term": 2
}
```

### 5、删除文档

```bash
DELETE customer/external/1
```

第一次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 8,
    "result": "deleted",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 19,
    "_primary_term": 2
}
```

第二次请求返回结果

```json
{
    "_index": "customer",
    "_type": "external",
    "_id": "1",
    "_version": 1,
    "result": "not_found",
    "_shards": {
        "total": 2,
        "successful": 1,
        "failed": 0
    },
    "_seq_no": 20,
    "_primary_term": 2
}
```

### 6、删除索引

```bash
DELETE customer
```

第一次请求返回结果

```json
{
    "acknowledged": true
}
```

第二次请求返回结果

```json
{
    "error": {
        "root_cause": [
            {
                "type": "index_not_found_exception",
                "reason": "no such index [customer]",
                "resource.type": "index_or_alias",
                "resource.id": "customer",
                "index_uuid": "_na_",
                "index": "customer"
            }
        ],
        "type": "index_not_found_exception",
        "reason": "no such index [customer]",
        "resource.type": "index_or_alias",
        "resource.id": "customer",
        "index_uuid": "_na_",
        "index": "customer"
    },
    "status": 404
}
```

### 7、批量操作

==<u>**!!!注意：使用 _bulk 进行批量操作时，在 Kibana 中 Dev Tools 的 Console 命令行的 json 不要为了美观而进行换行格式化，否则执行会报错。这个跟 postman 中 body 里面的 json 格式要求不同**</u>==

**简单示例**

```bash
POST customer/external/_bulk
{"index":{"_id":"1"}}
{"name": "John Doe" }
{"index":{"_id":"2"}}
{"name": "Jane Doe" }
```

注意上述示例中

第一个操作：新增一个"_id"为"1"、"name"为"John Doe"的文档

```json
{"index":{"_id":"1"}}
{"name": "John Doe" }
```

第二个操作：新增一个"_id"为"2"、"name"为"John Doe"的文档

```json
{"index":{"_id":"2"}}
{"name": "John Doe" }
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
#! Deprecation: [types removal] Specifying types in bulk requests is deprecated.
{
  "took" : 497,
  "errors" : false,
  "items" : [
    {
      "index" : {
        "_index" : "customer",
        "_type" : "external",
        "_id" : "1",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 0,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "index" : {
        "_index" : "customer",
        "_type" : "external",
        "_id" : "2",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    }
  ]
}
</code></pre></details>
**复杂示例**

```bash
POST /_bulk
{ "delete": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "create": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "title": "My first blog post" }
{ "index": { "_index": "website", "_type": "blog" }}
{ "title": "My second blog post" }
{ "update": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "doc" : {"title" : "My updated blog post"}}
```

注意上述示例中

第一个操作：删除一个索引为"website"，类型为"blog"，ID为"123"的文档

```json
{ "delete": { "_index": "website", "_type": "blog", "_id": "123" }} 
```

第二个操作：创建一个索引为"website"，类型为"blog"，ID为"123"，"title"为"My first blog post"的文档

```json
{ "create": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "title": "My first blog post" }
```

第三个操作：创建一个索引为"website"，类型为"blog"，"title"为"My second blog post"的文档

```json
{ "index": { "_index": "website", "_type": "blog" }}
{ "title": "My second blog post" }
```

第四个操作：更新索引为"website"，类型为"blog"，ID为"123"，"title"为"My updated blog post"的文档

```json
{ "update": { "_index": "website", "_type": "blog", "_id": "123"} }
{ "doc" : {"title" : "My updated blog post"}}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
#! Deprecation: [types removal] Specifying types in bulk requests is deprecated.
{
  "took" : 366,
  "errors" : false,
  "items" : [
    {
      "delete" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 4,
        "result" : "deleted",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 4,
        "_primary_term" : 1,
        "status" : 200
      }
    },
    {
      "create" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 5,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 5,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "index" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "XTvbcY0BM4fzqjiSfd8V",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 6,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "update" : {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "123",
        "_version" : 6,
        "result" : "updated",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 7,
        "_primary_term" : 1,
        "status" : 200
      }
    }
  ]
}
</code></pre></details>

> 可以看出，bulk API 以此按顺序执行所有的 action（动作）。如果一个单个的动作因任何原因而失败，它将继续处理它后面剩余的动作。当 bulk API 返回时，它将提供每个动作的状态（与发送的顺序相同），所以您可以检查是否一个指定的动作是不是失败了。
>

### 8、样本测试数据

导入测试数据：[https://github.com/elastic/elasticsearch/blob/7.5/docs/src/test/resources/accounts.json](https://github.com/elastic/elasticsearch/blob/7.5/docs/src/test/resources/accounts.json)

如果访问较慢或者网络不通，可以导入当前文件夹下的json数据：[2-分布式高级-Es-样本测试数据.json](./2-分布式高级-Es-样本测试数据.json)

```bash
POST bank/account/_bulk
{"index":{"_id":"1"}}
{"account_number":1,"balance":39225,"firstname":"Amber","lastname":"Duke","age":32,"gender":"M","address":"880 Holmes Lane","employer":"Pyrami","email":"amberduke@pyrami.com","city":"Brogan","state":"IL"}
# 以 “2-分布式高级-Es-样本测试数据.json” 文件中数据为准，数据较长，其余省略......
```



## 四、进阶检索

### 1、_search（检索信息）

#### 1.1、检索所有信息

检索 bank 下所有信息，包括 type 和 docs

```bash
GET bank/_search
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 28,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 1,
          "balance" : 39225,
          "firstname" : "Amber",
          "lastname" : "Duke",
          "age" : 32,
          "gender" : "M",
          "address" : "880 Holmes Lane",
          "employer" : "Pyrami",
          "email" : "amberduke@pyrami.com",
          "city" : "Brogan",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "6",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 6,
          "balance" : 5686,
          "firstname" : "Hattie",
          "lastname" : "Bond",
          "age" : 36,
          "gender" : "M",
          "address" : "671 Bristol Street",
          "employer" : "Netagy",
          "email" : "hattiebond@netagy.com",
          "city" : "Dante",
          "state" : "TN"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "13",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 13,
          "balance" : 32838,
          "firstname" : "Nanette",
          "lastname" : "Bates",
          "age" : 28,
          "gender" : "F",
          "address" : "789 Madison Street",
          "employer" : "Quility",
          "email" : "nanettebates@quility.com",
          "city" : "Nogal",
          "state" : "VA"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "18",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 18,
          "balance" : 4180,
          "firstname" : "Dale",
          "lastname" : "Adams",
          "age" : 33,
          "gender" : "M",
          "address" : "467 Hutchinson Court",
          "employer" : "Boink",
          "email" : "daleadams@boink.com",
          "city" : "Orick",
          "state" : "MD"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "20",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 20,
          "balance" : 16418,
          "firstname" : "Elinor",
          "lastname" : "Ratliff",
          "age" : 36,
          "gender" : "M",
          "address" : "282 Kings Place",
          "employer" : "Scentric",
          "email" : "elinorratliff@scentric.com",
          "city" : "Ribera",
          "state" : "WA"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "25",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 25,
          "balance" : 40540,
          "firstname" : "Virginia",
          "lastname" : "Ayala",
          "age" : 39,
          "gender" : "F",
          "address" : "171 Putnam Avenue",
          "employer" : "Filodyne",
          "email" : "virginiaayala@filodyne.com",
          "city" : "Nicholson",
          "state" : "PA"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "32",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 32,
          "balance" : 48086,
          "firstname" : "Dillard",
          "lastname" : "Mcpherson",
          "age" : 34,
          "gender" : "F",
          "address" : "702 Quentin Street",
          "employer" : "Quailcom",
          "email" : "dillardmcpherson@quailcom.com",
          "city" : "Veguita",
          "state" : "IN"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "37",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 37,
          "balance" : 18612,
          "firstname" : "Mcgee",
          "lastname" : "Mooney",
          "age" : 39,
          "gender" : "M",
          "address" : "826 Fillmore Place",
          "employer" : "Reversus",
          "email" : "mcgeemooney@reversus.com",
          "city" : "Tooleville",
          "state" : "OK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "44",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 44,
          "balance" : 34487,
          "firstname" : "Aurelia",
          "lastname" : "Harding",
          "age" : 37,
          "gender" : "M",
          "address" : "502 Baycliff Terrace",
          "employer" : "Orbalix",
          "email" : "aureliaharding@orbalix.com",
          "city" : "Yardville",
          "state" : "DE"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "49",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 49,
          "balance" : 29104,
          "firstname" : "Fulton",
          "lastname" : "Holt",
          "age" : 23,
          "gender" : "F",
          "address" : "451 Humboldt Street",
          "employer" : "Anocha",
          "email" : "fultonholt@anocha.com",
          "city" : "Sunriver",
          "state" : "RI"
        }
      }
    ]
  }
}
</code></pre></details>

#### 1.2、请求参数检索

```bash
GET bank/_search?q=*&sort=account_number:asc
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 9,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "0",
        "_score" : null,
        "_source" : {
          "account_number" : 0,
          "balance" : 16623,
          "firstname" : "Bradshaw",
          "lastname" : "Mckenzie",
          "age" : 29,
          "gender" : "F",
          "address" : "244 Columbus Place",
          "employer" : "Euron",
          "email" : "bradshawmckenzie@euron.com",
          "city" : "Hobucken",
          "state" : "CO"
        },
        "sort" : [
          0
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "1",
        "_score" : null,
        "_source" : {
          "account_number" : 1,
          "balance" : 39225,
          "firstname" : "Amber",
          "lastname" : "Duke",
          "age" : 32,
          "gender" : "M",
          "address" : "880 Holmes Lane",
          "employer" : "Pyrami",
          "email" : "amberduke@pyrami.com",
          "city" : "Brogan",
          "state" : "IL"
        },
        "sort" : [
          1
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "2",
        "_score" : null,
        "_source" : {
          "account_number" : 2,
          "balance" : 28838,
          "firstname" : "Roberta",
          "lastname" : "Bender",
          "age" : 22,
          "gender" : "F",
          "address" : "560 Kingsway Place",
          "employer" : "Chillium",
          "email" : "robertabender@chillium.com",
          "city" : "Bennett",
          "state" : "LA"
        },
        "sort" : [
          2
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "3",
        "_score" : null,
        "_source" : {
          "account_number" : 3,
          "balance" : 44947,
          "firstname" : "Levine",
          "lastname" : "Burks",
          "age" : 26,
          "gender" : "F",
          "address" : "328 Wilson Avenue",
          "employer" : "Amtap",
          "email" : "levineburks@amtap.com",
          "city" : "Cochranville",
          "state" : "HI"
        },
        "sort" : [
          3
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "4",
        "_score" : null,
        "_source" : {
          "account_number" : 4,
          "balance" : 27658,
          "firstname" : "Rodriquez",
          "lastname" : "Flores",
          "age" : 31,
          "gender" : "F",
          "address" : "986 Wyckoff Avenue",
          "employer" : "Tourmania",
          "email" : "rodriquezflores@tourmania.com",
          "city" : "Eastvale",
          "state" : "HI"
        },
        "sort" : [
          4
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "5",
        "_score" : null,
        "_source" : {
          "account_number" : 5,
          "balance" : 29342,
          "firstname" : "Leola",
          "lastname" : "Stewart",
          "age" : 30,
          "gender" : "F",
          "address" : "311 Elm Place",
          "employer" : "Diginetic",
          "email" : "leolastewart@diginetic.com",
          "city" : "Fairview",
          "state" : "NJ"
        },
        "sort" : [
          5
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "6",
        "_score" : null,
        "_source" : {
          "account_number" : 6,
          "balance" : 5686,
          "firstname" : "Hattie",
          "lastname" : "Bond",
          "age" : 36,
          "gender" : "M",
          "address" : "671 Bristol Street",
          "employer" : "Netagy",
          "email" : "hattiebond@netagy.com",
          "city" : "Dante",
          "state" : "TN"
        },
        "sort" : [
          6
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "7",
        "_score" : null,
        "_source" : {
          "account_number" : 7,
          "balance" : 39121,
          "firstname" : "Levy",
          "lastname" : "Richard",
          "age" : 22,
          "gender" : "M",
          "address" : "820 Logan Street",
          "employer" : "Teraprene",
          "email" : "levyrichard@teraprene.com",
          "city" : "Shrewsbury",
          "state" : "MO"
        },
        "sort" : [
          7
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "8",
        "_score" : null,
        "_source" : {
          "account_number" : 8,
          "balance" : 48868,
          "firstname" : "Jan",
          "lastname" : "Burns",
          "age" : 35,
          "gender" : "M",
          "address" : "699 Visitation Place",
          "employer" : "Glasstep",
          "email" : "janburns@glasstep.com",
          "city" : "Wakulla",
          "state" : "AZ"
        },
        "sort" : [
          8
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "9",
        "_score" : null,
        "_source" : {
          "account_number" : 9,
          "balance" : 24776,
          "firstname" : "Opal",
          "lastname" : "Meadows",
          "age" : 39,
          "gender" : "M",
          "address" : "963 Neptune Avenue",
          "employer" : "Cedward",
          "email" : "opalmeadows@cedward.com",
          "city" : "Olney",
          "state" : "OH"
        },
        "sort" : [
          9
        ]
      }
    ]
  }
}
</code></pre></details>

> 响应结果解释：
>
> - `took` - Elasticsearch 执行搜索的时间（毫秒） 
> - `time_out` - 告诉我们搜索是否超时 
> - `_shards` - 告诉我们多少个分片被搜索了，以及统计了成功/失败的搜索分片 
> - `hits` - 搜索结果 
> - `hits.total` - 搜索结果数
> - `hits.hits` - 实际的搜索结果数组（默认为前 10 的文档） 
> - `sort` - 结果的排序 key（键）（没有则按 score 排序） 
> - `score` 和 `max_score` –相关性得分和最高得分（全文检索用）
>

#### 1.3、uri+请求体检索

```bash
GET bank/_search
{
    "query": {
        "match_all": {}
    },
    "sort": [
        {
            "account_number": {
                "order": "desc"
            }
        }
    ]
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "999",
        "_score" : null,
        "_source" : {
          "account_number" : 999,
          "balance" : 6087,
          "firstname" : "Dorothy",
          "lastname" : "Barron",
          "age" : 22,
          "gender" : "F",
          "address" : "499 Laurel Avenue",
          "employer" : "Xurban",
          "email" : "dorothybarron@xurban.com",
          "city" : "Belvoir",
          "state" : "CA"
        },
        "sort" : [
          999
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "998",
        "_score" : null,
        "_source" : {
          "account_number" : 998,
          "balance" : 16869,
          "firstname" : "Letha",
          "lastname" : "Baker",
          "age" : 40,
          "gender" : "F",
          "address" : "206 Llama Court",
          "employer" : "Dognosis",
          "email" : "lethabaker@dognosis.com",
          "city" : "Dunlo",
          "state" : "WV"
        },
        "sort" : [
          998
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "997",
        "_score" : null,
        "_source" : {
          "account_number" : 997,
          "balance" : 25311,
          "firstname" : "Combs",
          "lastname" : "Frederick",
          "age" : 20,
          "gender" : "M",
          "address" : "586 Lloyd Court",
          "employer" : "Pathways",
          "email" : "combsfrederick@pathways.com",
          "city" : "Williamson",
          "state" : "CA"
        },
        "sort" : [
          997
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "996",
        "_score" : null,
        "_source" : {
          "account_number" : 996,
          "balance" : 17541,
          "firstname" : "Andrews",
          "lastname" : "Herrera",
          "age" : 30,
          "gender" : "F",
          "address" : "570 Vandam Street",
          "employer" : "Klugger",
          "email" : "andrewsherrera@klugger.com",
          "city" : "Whitehaven",
          "state" : "MN"
        },
        "sort" : [
          996
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "995",
        "_score" : null,
        "_source" : {
          "account_number" : 995,
          "balance" : 21153,
          "firstname" : "Phelps",
          "lastname" : "Parrish",
          "age" : 25,
          "gender" : "M",
          "address" : "666 Miller Place",
          "employer" : "Pearlessa",
          "email" : "phelpsparrish@pearlessa.com",
          "city" : "Brecon",
          "state" : "ME"
        },
        "sort" : [
          995
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "994",
        "_score" : null,
        "_source" : {
          "account_number" : 994,
          "balance" : 33298,
          "firstname" : "Madge",
          "lastname" : "Holcomb",
          "age" : 31,
          "gender" : "M",
          "address" : "612 Hawthorne Street",
          "employer" : "Escenta",
          "email" : "madgeholcomb@escenta.com",
          "city" : "Alafaya",
          "state" : "OR"
        },
        "sort" : [
          994
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "993",
        "_score" : null,
        "_source" : {
          "account_number" : 993,
          "balance" : 26487,
          "firstname" : "Campos",
          "lastname" : "Olsen",
          "age" : 37,
          "gender" : "M",
          "address" : "873 Covert Street",
          "employer" : "Isbol",
          "email" : "camposolsen@isbol.com",
          "city" : "Glendale",
          "state" : "AK"
        },
        "sort" : [
          993
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "992",
        "_score" : null,
        "_source" : {
          "account_number" : 992,
          "balance" : 11413,
          "firstname" : "Kristie",
          "lastname" : "Kennedy",
          "age" : 33,
          "gender" : "F",
          "address" : "750 Hudson Avenue",
          "employer" : "Ludak",
          "email" : "kristiekennedy@ludak.com",
          "city" : "Warsaw",
          "state" : "WY"
        },
        "sort" : [
          992
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "991",
        "_score" : null,
        "_source" : {
          "account_number" : 991,
          "balance" : 4239,
          "firstname" : "Connie",
          "lastname" : "Berry",
          "age" : 28,
          "gender" : "F",
          "address" : "647 Gardner Avenue",
          "employer" : "Flumbo",
          "email" : "connieberry@flumbo.com",
          "city" : "Frierson",
          "state" : "MO"
        },
        "sort" : [
          991
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "990",
        "_score" : null,
        "_source" : {
          "account_number" : 990,
          "balance" : 44456,
          "firstname" : "Kelly",
          "lastname" : "Steele",
          "age" : 35,
          "gender" : "M",
          "address" : "809 Hoyt Street",
          "employer" : "Eschoir",
          "email" : "kellysteele@eschoir.com",
          "city" : "Stewartville",
          "state" : "ID"
        },
        "sort" : [
          990
        ]
      }
    ]
  }
}
</code></pre></details>

### 2、Query DSL（Domain-Specific Language，领域特定语言）

#### 2.1、基本语法格式

> - `query` 定义如何查询
> - `match_all` 查询类型【代表查询所有的所有】，es 中可以在 `query` 中组合非常多的查询类型完成复杂查询
> - 除了 `query` 参数之外，我们也可以传递其它的参数以改变查询结果。如 `sort`，`size`
> - `from` + `size` 限定，完成分页功能
> - `sort` 排序，多字段排序，会在前序字段相等时后续字段内部排序，否则以前序为准

```bash
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0,
  "size": 5,
  "sort": [
    {
      "balance": {
        "order": "desc"
      }
    }
  ]
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 36,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "248",
        "_score" : null,
        "_source" : {
          "account_number" : 248,
          "balance" : 49989,
          "firstname" : "West",
          "lastname" : "England",
          "age" : 36,
          "gender" : "M",
          "address" : "717 Hendrickson Place",
          "employer" : "Obliq",
          "email" : "westengland@obliq.com",
          "city" : "Maury",
          "state" : "WA"
        },
        "sort" : [
          49989
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "854",
        "_score" : null,
        "_source" : {
          "account_number" : 854,
          "balance" : 49795,
          "firstname" : "Jimenez",
          "lastname" : "Barry",
          "age" : 25,
          "gender" : "F",
          "address" : "603 Cooper Street",
          "employer" : "Verton",
          "email" : "jimenezbarry@verton.com",
          "city" : "Moscow",
          "state" : "AL"
        },
        "sort" : [
          49795
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "240",
        "_score" : null,
        "_source" : {
          "account_number" : 240,
          "balance" : 49741,
          "firstname" : "Oconnor",
          "lastname" : "Clay",
          "age" : 35,
          "gender" : "F",
          "address" : "659 Highland Boulevard",
          "employer" : "Franscene",
          "email" : "oconnorclay@franscene.com",
          "city" : "Kilbourne",
          "state" : "NH"
        },
        "sort" : [
          49741
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "97",
        "_score" : null,
        "_source" : {
          "account_number" : 97,
          "balance" : 49671,
          "firstname" : "Karen",
          "lastname" : "Trujillo",
          "age" : 40,
          "gender" : "F",
          "address" : "512 Cumberland Walk",
          "employer" : "Tsunamia",
          "email" : "karentrujillo@tsunamia.com",
          "city" : "Fredericktown",
          "state" : "MO"
        },
        "sort" : [
          49671
        ]
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "842",
        "_score" : null,
        "_source" : {
          "account_number" : 842,
          "balance" : 49587,
          "firstname" : "Meagan",
          "lastname" : "Buckner",
          "age" : 23,
          "gender" : "F",
          "address" : "833 Bushwick Court",
          "employer" : "Biospan",
          "email" : "meaganbuckner@biospan.com",
          "city" : "Craig",
          "state" : "TX"
        },
        "sort" : [
          49587
        ]
      }
    ]
  }
}
</code></pre></details>

#### 2.2、返回部分字段

```bash
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0,
  "size": 5,
  "_source": ["age", "balance"]
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 6,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "balance" : 39225,
          "age" : 32
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "6",
        "_score" : 1.0,
        "_source" : {
          "balance" : 5686,
          "age" : 36
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "13",
        "_score" : 1.0,
        "_source" : {
          "balance" : 32838,
          "age" : 28
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "18",
        "_score" : 1.0,
        "_source" : {
          "balance" : 4180,
          "age" : 33
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "20",
        "_score" : 1.0,
        "_source" : {
          "balance" : 16418,
          "age" : 36
        }
      }
    ]
  }
}
</code></pre></details>

#### 2.3、match（匹配查询）

##### 2.3.1、基本类型（非字符串），精确匹配

```bash
GET bank/_search
{
  "query": {
    "match": {
      "account_number": "20"
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 67,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "20",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 20,
          "balance" : 16418,
          "firstname" : "Elinor",
          "lastname" : "Ratliff",
          "age" : 36,
          "gender" : "M",
          "address" : "282 Kings Place",
          "employer" : "Scentric",
          "email" : "elinorratliff@scentric.com",
          "city" : "Ribera",
          "state" : "WA"
        }
      }
    ]
  }
}
</code></pre></details>

返回 account_number=20 的记录

##### 2.3.2、字符串，全文检索

```bash
GET bank/_search
{
  "query": {
    "match": {
      "address": "mill"
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 86,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : 5.4032025,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "136",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "345",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 345,
          "balance" : 9812,
          "firstname" : "Parker",
          "lastname" : "Hines",
          "age" : 38,
          "gender" : "M",
          "address" : "715 Mill Avenue",
          "employer" : "Baluba",
          "email" : "parkerhines@baluba.com",
          "city" : "Blackgum",
          "state" : "KY"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "472",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 472,
          "balance" : 25571,
          "firstname" : "Lee",
          "lastname" : "Long",
          "age" : 32,
          "gender" : "F",
          "address" : "288 Mill Street",
          "employer" : "Comverges",
          "email" : "leelong@comverges.com",
          "city" : "Movico",
          "state" : "MT"
        }
      }
    ]
  }
}
</code></pre></details>

返回 address 中包含 mill 单词的所有记录，并且每条记录都有相关性得分

##### 2.3.3、字符串，多个单词（分词 + 全文检索）

```bash
GET bank/_search
{
  "query": {
    "match": {
      "address": "mill road"
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 143,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 32,
      "relation" : "eq"
    },
    "max_score" : 8.926605,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 8.926605,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "136",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "345",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 345,
          "balance" : 9812,
          "firstname" : "Parker",
          "lastname" : "Hines",
          "age" : 38,
          "gender" : "M",
          "address" : "715 Mill Avenue",
          "employer" : "Baluba",
          "email" : "parkerhines@baluba.com",
          "city" : "Blackgum",
          "state" : "KY"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "472",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 472,
          "balance" : 25571,
          "firstname" : "Lee",
          "lastname" : "Long",
          "age" : 32,
          "gender" : "F",
          "address" : "288 Mill Street",
          "employer" : "Comverges",
          "email" : "leelong@comverges.com",
          "city" : "Movico",
          "state" : "MT"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "431",
        "_score" : 3.5234027,
        "_source" : {
          "account_number" : 431,
          "balance" : 13136,
          "firstname" : "Laurie",
          "lastname" : "Shaw",
          "age" : 26,
          "gender" : "F",
          "address" : "263 Aviation Road",
          "employer" : "Zillanet",
          "email" : "laurieshaw@zillanet.com",
          "city" : "Harmon",
          "state" : "WV"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "436",
        "_score" : 3.5234027,
        "_source" : {
          "account_number" : 436,
          "balance" : 27585,
          "firstname" : "Alexander",
          "lastname" : "Sargent",
          "age" : 23,
          "gender" : "M",
          "address" : "363 Albemarle Road",
          "employer" : "Fangold",
          "email" : "alexandersargent@fangold.com",
          "city" : "Calpine",
          "state" : "OR"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "532",
        "_score" : 3.5234027,
        "_source" : {
          "account_number" : 532,
          "balance" : 17207,
          "firstname" : "Hardin",
          "lastname" : "Kirk",
          "age" : 26,
          "gender" : "M",
          "address" : "268 Canarsie Road",
          "employer" : "Exposa",
          "email" : "hardinkirk@exposa.com",
          "city" : "Stouchsburg",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "873",
        "_score" : 3.5234027,
        "_source" : {
          "account_number" : 873,
          "balance" : 43931,
          "firstname" : "Tisha",
          "lastname" : "Cotton",
          "age" : 39,
          "gender" : "F",
          "address" : "432 Lincoln Road",
          "employer" : "Buzzmaker",
          "email" : "tishacotton@buzzmaker.com",
          "city" : "Bluetown",
          "state" : "GA"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "83",
        "_score" : 3.5234027,
        "_source" : {
          "account_number" : 83,
          "balance" : 35928,
          "firstname" : "Mayo",
          "lastname" : "Cleveland",
          "age" : 28,
          "gender" : "M",
          "address" : "720 Brooklyn Road",
          "employer" : "Indexia",
          "email" : "mayocleveland@indexia.com",
          "city" : "Roberts",
          "state" : "ND"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "88",
        "_score" : 3.5234027,
        "_source" : {
          "account_number" : 88,
          "balance" : 26418,
          "firstname" : "Adela",
          "lastname" : "Tyler",
          "age" : 21,
          "gender" : "F",
          "address" : "737 Clove Road",
          "employer" : "Surelogic",
          "email" : "adelatyler@surelogic.com",
          "city" : "Boling",
          "state" : "SD"
        }
      }
    ]
  }
}
</code></pre></details>

返回 address 中包含 mill 或者 road 或者 mill road 的所有记录，并给出相关性得分

##### 2.3.4、match_phrase（短语匹配）

```bash
GET bank/_search
{
  "query": {
    "match_phrase": {
      "address": "mill road"
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 237,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 8.926605,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 8.926605,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      }
    ]
  }
}
</code></pre></details>

返回 address 中包含 mill road 的所有记录，并给出相关性得分

##### 2.3.5、multi_match（多字段匹配）

```bash
GET bank/_search
{
  "query": {
    "multi_match": {
      "query": "mill",
      "fields": ["state", "address"]
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 32,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : 5.4032025,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "136",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "345",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 345,
          "balance" : 9812,
          "firstname" : "Parker",
          "lastname" : "Hines",
          "age" : 38,
          "gender" : "M",
          "address" : "715 Mill Avenue",
          "employer" : "Baluba",
          "email" : "parkerhines@baluba.com",
          "city" : "Blackgum",
          "state" : "KY"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "472",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 472,
          "balance" : 25571,
          "firstname" : "Lee",
          "lastname" : "Long",
          "age" : 32,
          "gender" : "F",
          "address" : "288 Mill Street",
          "employer" : "Comverges",
          "email" : "leelong@comverges.com",
          "city" : "Movico",
          "state" : "MT"
        }
      }
    ]
  }
}
</code></pre></details>

返回 state 或者 address 包含 mill

##### 2.3.6、bool（复合查询）

> bool 用来做复合查询： 复合语句可以合并任何其它查询语句（包括复合语句），了解这一点是很重要的。
>
> 这就意味着，复合语句之间可以互相嵌套，可以表达非常复杂的逻辑。

> `must`：必须达到 `must` 列举的所有条件

```bash
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "address": "mill"
          }
        },
        {
          "match": {
            "gender": "M"
          }
        }
      ]
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 68,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : 6.0824604,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 6.0824604,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "136",
        "_score" : 6.0824604,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "345",
        "_score" : 6.0824604,
        "_source" : {
          "account_number" : 345,
          "balance" : 9812,
          "firstname" : "Parker",
          "lastname" : "Hines",
          "age" : 38,
          "gender" : "M",
          "address" : "715 Mill Avenue",
          "employer" : "Baluba",
          "email" : "parkerhines@baluba.com",
          "city" : "Blackgum",
          "state" : "KY"
        }
      }
    ]
  }
}
</code></pre></details>

> `should`：应该达到 `should` 列举的条件，如果达到会增加相关文档的评分，并不会改变查询的结果。如果 `query` 中只有 `should` 且只有一种匹配规则，那么 `should` 的条件就会被作为默认匹配条件而去改变查询结果

```bash
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "address": "mill"
          }
        },
        {
          "match": {
            "gender": "M"
          }
        }
      ],
      "should": [
        {
          "match": {
            "address": "lane"
          }
        }
      ]
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 58,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : 10.186735,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "136",
        "_score" : 10.186735,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 6.0824604,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "345",
        "_score" : 6.0824604,
        "_source" : {
          "account_number" : 345,
          "balance" : 9812,
          "firstname" : "Parker",
          "lastname" : "Hines",
          "age" : 38,
          "gender" : "M",
          "address" : "715 Mill Avenue",
          "employer" : "Baluba",
          "email" : "parkerhines@baluba.com",
          "city" : "Blackgum",
          "state" : "KY"
        }
      }
    ]
  }
}
</code></pre></details>

> `must_not` 必须不是指定的情况

```bash
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "address": "mill"
          }
        },
        {
          "match": {
            "gender": "M"
          }
        }
      ],
      "should": [
        {
          "match": {
            "address": "lane"
          }
        }
      ],
      "must_not": [
        {
          "match": {
            "email": "baluba.com"
          }
        }
      ]
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 10.186735,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "136",
        "_score" : 10.186735,
        "_source" : {
          "account_number" : 136,
          "balance" : 45801,
          "firstname" : "Winnie",
          "lastname" : "Holland",
          "age" : 38,
          "gender" : "M",
          "address" : "198 Mill Lane",
          "employer" : "Neteria",
          "email" : "winnieholland@neteria.com",
          "city" : "Urie",
          "state" : "IL"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 6.0824604,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      }
    ]
  }
}
</code></pre></details>

address 包含 mill，并且 gender 是 M，如果 address 里面有 lane 最好不过，但是 email 必须不包含 baluba.com

##### 2.3.7、filter（结果过滤）

> 并不是所有的查询都需要产生分数，特别是那些仅用于 “filtering”（过滤）的文档。
>
> 为了不计算分数，Elasticsearch 会自动检查场景并且优化查询的执行

```bash
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "address": "mill"
          }
        }
      ],
      "filter": {
        "range": {
          "balance": {
            "gte": 10000,
            "lte": 20000
          }
        }
      }
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 37,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 5.4032025,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 5.4032025,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      }
    ]
  }
}
</code></pre></details>

稍作总结：

| 事件       | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| `must`     | 子句（查询）必须出现在匹配的文档中，并将有助于得分           |
| `filter`   | 子句（查询）必须出现在匹配的文档中。然而不像 `must`， 此查询的分数将被忽略 |
| `should`   | 子句（查询）应出现在匹配的文档中。<br/>在布尔查询中不包含 `must` 或 `filter` 子句，一个或多个 `should` 子句必须有相匹配的文件。<br/>匹配 `should` 条件的最小数目可通过设置 `minimum_should_match` 参数 |
| `must_not` | 子句（查询）不能出现在匹配的文档中                           |

##### 2.3.8、term

> 和 `match` 一样，匹配某个属性的值。全文检索字段用 `match`，其他非 `text` 字段匹配用 `term`

```bash
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "age": {
              "value": "28"
            }
          }
        },
        {
          "match": {
            "address": "990 Mill Road"
          }
        }
      ]
    }
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 166,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : 15.329807,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "970",
        "_score" : 15.329807,
        "_source" : {
          "account_number" : 970,
          "balance" : 19648,
          "firstname" : "Forbes",
          "lastname" : "Wallace",
          "age" : 28,
          "gender" : "M",
          "address" : "990 Mill Road",
          "employer" : "Pheast",
          "email" : "forbeswallace@pheast.com",
          "city" : "Lopezo",
          "state" : "AK"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "83",
        "_score" : 4.5234027,
        "_source" : {
          "account_number" : 83,
          "balance" : 35928,
          "firstname" : "Mayo",
          "lastname" : "Cleveland",
          "age" : 28,
          "gender" : "M",
          "address" : "720 Brooklyn Road",
          "employer" : "Indexia",
          "email" : "mayocleveland@indexia.com",
          "city" : "Roberts",
          "state" : "ND"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "728",
        "_score" : 4.5234027,
        "_source" : {
          "account_number" : 728,
          "balance" : 44818,
          "firstname" : "Conley",
          "lastname" : "Preston",
          "age" : 28,
          "gender" : "M",
          "address" : "450 Coventry Road",
          "employer" : "Obones",
          "email" : "conleypreston@obones.com",
          "city" : "Alden",
          "state" : "CO"
        }
      },
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "510",
        "_score" : 4.5234027,
        "_source" : {
          "account_number" : 510,
          "balance" : 48504,
          "firstname" : "Petty",
          "lastname" : "Sykes",
          "age" : 28,
          "gender" : "M",
          "address" : "566 Village Road",
          "employer" : "Nebulean",
          "email" : "pettysykes@nebulean.com",
          "city" : "Wedgewood",
          "state" : "MO"
        }
      }
    ]
  }
}
</code></pre></details>

##### 2.3.9、aggregations（执行聚合）

> 聚合提供了从数据中分组和提取数据的能力，最简单的聚合方法大致等于 SQL GROUP BY 和 SQL 聚合函数。
>
> 在 Elasticsearch 中，您有执行搜索返回 hits（命中结果），并且同时返回聚合结果，把一个响应中的所有 hits（命中结果）分隔开的能力。
>
> 这是非常强大且有效的，您可以执行查询和多个聚合，并且在一次使用中得到各自的（任何一个的）返回结果，使用一次简洁和简化的 API 来避免网络往返

> 1、搜索 address 中包含 mill 的所有人的年龄分布以及平均年龄，但不显示这些人的详情。

```bash
GET bank/_search
{
  "query": {
    "match": {
      "address": "mill"
    }
  },
  "aggs": {
    "group_by_state": {
      "terms": {
        "field": "age"
      }
    },
    "avg_state": {
      "avg": {
        "field": "age"
      }
    }
  },
  "size": 0
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 150,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 4,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "avg_state" : {
      "value" : 34.0
    },
    "group_by_state" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : 38,
          "doc_count" : 2
        },
        {
          "key" : 28,
          "doc_count" : 1
        },
        {
          "key" : 32,
          "doc_count" : 1
        }
      ]
    }
  }
}
</code></pre></details>

> 2、按照年龄聚合，并且请求这些年龄段的这些人的平均薪资

```bash
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "aggs": {
    "age_avg": {
      "terms": {
        "field": "age",
        "size": 1000
      },
      "aggs": {
        "balances_avg": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  },
  "size": 0
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">结果过长，这里只截取部分
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "age_avg" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : 31,
          "doc_count" : 61,
          "balances_avg" : {
            "value" : 28312.918032786885
          }
        },
        {
          "key" : 39,
          "doc_count" : 60,
          "balances_avg" : {
            "value" : 25269.583333333332
          }
        },
        {
          "key" : 26,
          "doc_count" : 59,
          "balances_avg" : {
            "value" : 23194.813559322032
          }
        }
      ]
    }
  }
}
</code></pre></details>


> 3、查出所有年龄分布，并且这些年龄段中 M 的平均薪资和 F 的平均薪资以及这个年龄段的总体平均薪资

```bash
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "aggs": {
    "age_avg": {
      "terms": {
        "field": "age",
        "size": 100
      },
      "aggs": {
        "gender_agg": {
          "terms": {
            "field": "gender.keyword",
            "size": 100
          },
          "aggs": {
            "balance_avg": {
              "avg": {
                "field": "balance"
              }
            }
          }
        },
        "balance_agg": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  },
  "size": 0
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">结果过长，这里只截取部分
{
    "took": 1,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 1000,
            "relation": "eq"
        },
        "max_score": null,
        "hits": []
    },
    "aggregations": {
        "age_avg": {
            "doc_count_error_upper_bound": 0,
            "sum_other_doc_count": 0,
            "buckets": [
                {
                    "key": 31,
                    "doc_count": 61,
                    "balance_agg": {
                        "value": 28312.918032786885
                    },
                    "gender_agg": {
                        "doc_count_error_upper_bound": 0,
                        "sum_other_doc_count": 0,
                        "buckets": [
                            {
                                "key": "M",
                                "doc_count": 35,
                                "balance_avg": {
                                    "value": 29565.628571428573
                                }
                            },
                            {
                                "key": "F",
                                "doc_count": 26,
                                "balance_avg": {
                                    "value": 26626.576923076922
                                }
                            }
                        ]
                    }
                },
                {
                    "key": 39,
                    "doc_count": 60,
                    "balance_agg": {
                        "value": 25269.583333333332
                    },
                    "gender_agg": {
                        "doc_count_error_upper_bound": 0,
                        "sum_other_doc_count": 0,
                        "buckets": [
                            {
                                "key": "F",
                                "doc_count": 38,
                                "balance_avg": {
                                    "value": 26348.684210526317
                                }
                            },
                            {
                                "key": "M",
                                "doc_count": 22,
                                "balance_avg": {
                                    "value": 23405.68181818182
                                }
                            }
                        ]
                    }
                }
            ]
        }
    }
}
</code></pre></details>

### 3、Mapping

#### 3.1、字段类型

官方文档：[https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html)

##### 3.1.1、核心类型

> - 字符串(string)
>   - `text`, `keyword`
> - 数字类型(Numeric)
>   - `btye`, `short`, `integer`, `long`, `double`, `float`, `half_float`, `scaled_float`
> - 日期类型(Date)
>   - `date`
> - 布尔类型(Boolean)
>   - `boolean`
> - 二进制类型(binary)
>   - `binary`

##### 3.1.2、复合类型

> - 数组类型(Array)
>   - `Array` 支持不针对特定的类型
> - 对象类型(Object)
>   - `object` 用于单 JSON 对象
> - 嵌套类型(Nested)
>   - `nested` 用于 JSON 对象数组

##### 3.1.3、理类型(Geo)

> - 地理坐标(Geo-points)
>   - `geo_point` 用于描述经纬度坐标
> - 地理图形(Geo-Shape)
>   - `geo_shape` 用于描述复杂形状，如多边形

##### 3.1.4、特定类型

> - IP类型
>   - `ip` 用于描述 ipv4 和 ipv6 地址
> - 补全类型(Completion)
>   - `completion` 提供自动完成提示
> - 令牌计数类型(Token count)
>   - `token_count` 用于统计字符串中的词条数量
> - 附件类型(attachment)
>   - 参考 `mapper-attachements` 插件，支持将附件如 Microsoft Office 格式，Open Document 格式，ePub, HTML 等等索引为 `attachment` 数据类型。
> - 抽取类型(Percolator)
>   - 接受特定领域查询语言(query-dsl)的查询

##### 3.1.5、多字段

> 通常用于为不同目的用不同的方法索引同一个字段。
>
> 例如，`string` 字段可以映射为一个 `text` 字段用于全文检索，同样可以映射为一个 `keyword` 字段用于排序和聚合。
>
> 另外，你可以使用 `standard analyzer`, `english analyzer` , `french analyzer` 来索引一个 `text` 字段。
>
> 这就是 `muti-fields` 的目的。大多数的数据类型通过 fields 参数来支持 `muti-fields`。

#### 3.2、映射

> Mapping （映射）
>
> **Mapping 是用来定义一个文档(document)，以及它所包含的属性(field)是如何存储和索引的**。比如，使用 mapping 来定义：
>
> - 哪些字符串属性应该被看做全文本属性(`full text fields`)。
> - 哪些属性包含数字，日期或者地理位置。
> - 文档中的所有属性是否都能被索引(_all 配置)。
> - 日期的格式。
> - 自定义映射规则来执行动态添加属性。

- 查看 mapping 信息

```bash
GET bank/_mapping
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "bank" : {
    "mappings" : {
      "properties" : {
        "account_number" : {
          "type" : "long"
        },
        "address" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "age" : {
          "type" : "long"
        },
        "balance" : {
          "type" : "long"
        },
        "city" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "email" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "employer" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "firstname" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "gender" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "lastname" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "state" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        }
      }
    }
  }
}
</code></pre></details>

- 修改 mapping 信息

自动猜测的映射类型

| JSON type                      | 域 type   |
| ------------------------------ | --------- |
| 布尔型：`true` 或者 `false`    | `boolean` |
| 整数：`123`                    | `long`    |
| 浮点数：`123.45`               | `double`  |
| 字符串，有效日期：`2014-09-15` | `date`    |
| 字符串：`foo bar`              | `string`  |

#### 3.3、新版本改变

> Es7 及以上移除了 type 的概念。
>
> - 关系型数据库中两个数据表示是独立的，即使他们里面有相同名称的列也不影响使用，但 ES 中不是这样的。elasticsearch 是基于 Lucene 开发的搜索引擎，而 ES 中不同 type 下名称相同的 filed 最终在 Lucene 中的处理方式是一样的。
>
>   - 两个不同 type 下的两个 user_name，在 ES 同一个索引下其实被认为是同一个 filed， 你必须在两个不同的 type 中定义相同的 filed 映射。否则，不同 type 中的相同字段名称就会在处理中出现冲突的情况，导致 Lucene 处理效率下降。
>
>   - 去掉 type 就是为了提高 ES 处理数据的效率。
>
> Elasticsearch 7.x
>
> - URL 中的 type 参数为可选。比如，索引一个文档不再要求提供文档类型。
>
> Elasticsearch 8.x
>
> - 不再支持 URL 中的 type 参数。
>
> 解决： 
>
> - 1）将索引从多类型迁移到单类型，每种类型文档一个独立索引
> - 2）将已存在的索引下的类型数据，全部迁移到指定位置即可。详见数据迁移

##### 3.3.1、创建映射

创建索引并指定映射

```bash
PUT /my-index
{
  "mappings": {
    "properties": {
      "age": {
        "type": "integer"
      },
      "email": {
        "type": "keyword"
      },
      "name": {
        "type": "text"
      }
    }
  }
}
```

第一次返回结果

```json
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "my-index"
}
```

第二次返回结果

```json
{
  "error": {
    "root_cause": [
      {
        "type": "resource_already_exists_exception",
        "reason": "index [my-index/JhDFTVY4RT-x6cknWTysvw] already exists",
        "index_uuid": "JhDFTVY4RT-x6cknWTysvw",
        "index": "my-index"
      }
    ],
    "type": "resource_already_exists_exception",
    "reason": "index [my-index/JhDFTVY4RT-x6cknWTysvw] already exists",
    "index_uuid": "JhDFTVY4RT-x6cknWTysvw",
    "index": "my-index"
  },
  "status": 400
}
```

提示索引已存在

##### 3.3.2、添加新的字段映射

```bash
PUT /my-index/_mapping
{
  "properties": {
    "employee-id": {
      "type": "keyword",
      "index": false
    }
  }
}
```

第一次返回结果

```json
{
  "acknowledged" : true
}
```

##### 3.3.3、更新映射

尝试更新映射

```bash
PUT /my-index/_mapping
{
  "properties": {
    "employee-id": {
      "type": "long",
      "index": false
    }
  }
}
```

返回结果

```json
{
  "error": {
    "root_cause": [
      {
        "type": "illegal_argument_exception",
        "reason": "mapper [employee-id] of different type, current_type [keyword], merged_type [long]"
      }
    ],
    "type": "illegal_argument_exception",
    "reason": "mapper [employee-id] of different type, current_type [keyword], merged_type [long]"
  },
  "status": 400
}
```

提示非法参数异常

> 对于已经存在的映射字段，我们不能更新。更新必须创建新的索引进行数据迁移

##### 3.3.4、数据迁移

查询老的索引 bank 的映射

```bash
GET /bank/_mapping
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "bank" : {
    "mappings" : {
      "properties" : {
        "account_number" : {
          "type" : "long"
        },
        "address" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "age" : {
          "type" : "long"
        },
        "balance" : {
          "type" : "long"
        },
        "city" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "email" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "employer" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "firstname" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "gender" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "lastname" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "state" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        }
      }
    }
  }
}
</code></pre></details>

创建新的索引 newbank 的映射，可以将老的索引 bank 的映射复制到新的索引 newbank 中再进行修改

<details><summary><font size="3" color="orange">请求数据</font></summary> 
<pre><code class="language-json">
PUT /newbank
{
  "mappings": {
    "properties": {
      "account_number": {
        "type": "long"
      },
      "address": {
        "type": "text"
      },
      "age": {
        "type": "integer"
      },
      "balance": {
        "type": "long"
      },
      "city": {
        "type": "keyword"
      },
      "email": {
        "type": "keyword"
      },
      "employer": {
        "type": "keyword"
      },
      "firstname": {
        "type": "text"
      },
      "gender": {
        "type": "keyword"
      },
      "lastname": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "state": {
        "type": "keyword"
      }
    }
  }
}
</code></pre></details>

查询老的索引 bank 的 _type 字段

```bash
GET bank/_search
{
    "query": {
        "match_all": {}
    },
    "size": 1
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "took" : 0,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1000,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "bank",
        "_type" : "account",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "account_number" : 1,
          "balance" : 39225,
          "firstname" : "Amber",
          "lastname" : "Duke",
          "age" : 32,
          "gender" : "M",
          "address" : "880 Holmes Lane",
          "employer" : "Pyrami",
          "email" : "amberduke@pyrami.com",
          "city" : "Brogan",
          "state" : "IL"
        }
      }
    ]
  }
}
</code></pre></details>

可以看到 `"_type" : "account"`，即 bank 索引中的 type 类型为 account

然后使用如下方式进行数据迁移

```bash
POST _reindex
{
  "source": {
    "index": "bank",
    "type": "accout"
  },
  "dest": {
    "index": "newbank"
  }
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
#! Deprecation: [types removal] Specifying types in reindex requests is deprecated.
{
  "took" : 201,
  "timed_out" : false,
  "total" : 0,
  "updated" : 0,
  "created" : 0,
  "deleted" : 0,
  "batches" : 0,
  "version_conflicts" : 0,
  "noops" : 0,
  "retries" : {
    "bulk" : 0,
    "search" : 0
  },
  "throttled_millis" : 0,
  "requests_per_second" : -1.0,
  "throttled_until_millis" : 0,
  "failures" : [ ]
}
</code></pre></details>

### 4、分词

> 一个 `tokenizer`（分词器）接收一个字符流，将之分割为独立的 `tokens`（词元，通常是独立的单词），然后输出 `tokens` 流。
>
> 例如，`whitespace tokenizer` 遇到空白字符时分割文本。它会将文本 "`Quick brown fox!`" 分割 为 [`Quick, brown, fox!`]。
>
> 该 `tokenizer`（分词器）还负责记录各个 `term`（词条）的顺序或 `position` 位置（用于 `phrase` 短 语和 `word proximity` 词近邻查询），以及 `term`（词条）所代表的原始 `word`（单词）的 `start` （起始）和 `end`（结束）的 `character offsets`（字符偏移量）（用于高亮显示搜索的内容）。 
>
> Elasticsearch 提供了很多内置的分词器，可以用来构建 `custom analyzers`（自定义分词器）。

官方文档：[https://www.elastic.co/guide/en/elasticsearch/reference/7.17/analysis-standard-tokenizer.html](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/analysis-standard-tokenizer.html)

```bash
POST _analyze
{
  "tokenizer": "standard",
  "text": "The 2 QUICK Brown-Foxes jumped over the lazy dog's bone."
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "tokens" : [
    {
      "token" : "The",
      "start_offset" : 0,
      "end_offset" : 3,
      "type" : "<ALPHANUM>",
      "position" : 0
    },
    {
      "token" : "2",
      "start_offset" : 4,
      "end_offset" : 5,
      "type" : "<NUM>",
      "position" : 1
    },
    {
      "token" : "QUICK",
      "start_offset" : 6,
      "end_offset" : 11,
      "type" : "<ALPHANUM>",
      "position" : 2
    },
    {
      "token" : "Brown",
      "start_offset" : 12,
      "end_offset" : 17,
      "type" : "<ALPHANUM>",
      "position" : 3
    },
    {
      "token" : "Foxes",
      "start_offset" : 18,
      "end_offset" : 23,
      "type" : "<ALPHANUM>",
      "position" : 4
    },
    {
      "token" : "jumped",
      "start_offset" : 24,
      "end_offset" : 30,
      "type" : "<ALPHANUM>",
      "position" : 5
    },
    {
      "token" : "over",
      "start_offset" : 31,
      "end_offset" : 35,
      "type" : "<ALPHANUM>",
      "position" : 6
    },
    {
      "token" : "the",
      "start_offset" : 36,
      "end_offset" : 39,
      "type" : "<ALPHANUM>",
      "position" : 7
    },
    {
      "token" : "lazy",
      "start_offset" : 40,
      "end_offset" : 44,
      "type" : "<ALPHANUM>",
      "position" : 8
    },
    {
      "token" : "dog's",
      "start_offset" : 45,
      "end_offset" : 50,
      "type" : "<ALPHANUM>",
      "position" : 9
    },
    {
      "token" : "bone",
      "start_offset" : 51,
      "end_offset" : 55,
      "type" : "<ALPHANUM>",
      "position" : 10
    }
  ]
}
</code></pre></details>
测试中文是否好用

```bash
POST _analyze
{
  "tokenizer": "standard",
  "text": "尚硅谷电商项目"
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "tokens" : [
    {
      "token" : "尚",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "<IDEOGRAPHIC>",
      "position" : 0
    },
    {
      "token" : "硅",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "<IDEOGRAPHIC>",
      "position" : 1
    },
    {
      "token" : "谷",
      "start_offset" : 2,
      "end_offset" : 3,
      "type" : "<IDEOGRAPHIC>",
      "position" : 2
    },
    {
      "token" : "电",
      "start_offset" : 3,
      "end_offset" : 4,
      "type" : "<IDEOGRAPHIC>",
      "position" : 3
    },
    {
      "token" : "商",
      "start_offset" : 4,
      "end_offset" : 5,
      "type" : "<IDEOGRAPHIC>",
      "position" : 4
    },
    {
      "token" : "项",
      "start_offset" : 5,
      "end_offset" : 6,
      "type" : "<IDEOGRAPHIC>",
      "position" : 5
    },
    {
      "token" : "目",
      "start_offset" : 6,
      "end_offset" : 7,
      "type" : "<IDEOGRAPHIC>",
      "position" : 6
    }
  ]
}
</code></pre></details>

发现并没有我们预想的那样分成词语

#### 4.1、安装 ik 分词器

```bash
# 进入 elasticsearch 插件目录
cd /mydata/elasticsearch/plugins/

# 下载对应版本的 ik 分词器压缩包
yum install wget -y
wget https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.4.2/elasticsearch-analysis-ik-7.4.2.zip

# 解压
yum install unzip -y
unzip elasticsearch-analysis-ik-7.4.2.zip
rm -rf *.zip

# 移至 ik 目录下并赋权限
mv * ik/
chmod -R 777 ik/

# 以交互模式进入 elasticsearch 容器的命令行中
docker exec -it elasticsearch /bin/bash

# 运行 elasticsearch-plugin
cd /bin
elasticsearch-plugin

# 查看插件是否已安装
elasticsearch-plugin list

# 重启 elasticsearch 容器
docker restart elasticsearch
```

#### 4.2、测试 ik 分词器

```bash
POST _analyze
{
  "text": "我是中国人"
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "<IDEOGRAPHIC>",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "<IDEOGRAPHIC>",
      "position" : 1
    },
    {
      "token" : "中",
      "start_offset" : 2,
      "end_offset" : 3,
      "type" : "<IDEOGRAPHIC>",
      "position" : 2
    },
    {
      "token" : "国",
      "start_offset" : 3,
      "end_offset" : 4,
      "type" : "<IDEOGRAPHIC>",
      "position" : 3
    },
    {
      "token" : "人",
      "start_offset" : 4,
      "end_offset" : 5,
      "type" : "<IDEOGRAPHIC>",
      "position" : 4
    }
  ]
}
</code></pre></details>

```bash
POST _analyze
{
  "analyzer": "ik_smart", 
  "text": "我是中国人"
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "中国人",
      "start_offset" : 2,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 2
    }
  ]
}
</code></pre></details>

```bash
POST _analyze
{
  "analyzer": "ik_max_word",
  "text": "我是中国人"
}
```

<details><summary><font size="3" color="orange">返回结果</font></summary> 
<pre><code class="language-json">
{
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "中国人",
      "start_offset" : 2,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "中国",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 3
    },
    {
      "token" : "国人",
      "start_offset" : 3,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 4
    }
  ]
}
</code></pre></details>

能够看出不同的分词器，分词有明显的区别，所以以后定义一个索引不能再使用默认的 mapping 了，要手工建立 mapping, 因为要选择分词器。

