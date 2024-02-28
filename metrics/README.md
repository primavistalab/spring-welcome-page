#### prometheus exporters list
https://prometheus.io/docs/instrumenting/exporters/

#### prometheus

copy file `prometheus/prometheus.yml` to `/var/app/metrics/prometheus`

cd to `/var/app/metrics/prometheus`

run this script in the console:
```shell
C_LOCAL_NETWORK=workgroup-network
PROMETHEUS_PORT=9090

docker stop prometheus || true
docker rm prometheus || true
docker network create $C_LOCAL_NETWORK || true
docker run -d --name prometheus --network=$C_LOCAL_NETWORK --expose $PROMETHEUS_PORT --mount type=bind,source=./prometheus.yml,target=/etc/prometheus/prometheus.yml --restart unless-stopped prom/prometheus:v2.44.0
```
if you want to see prometheus WebUI (like targets), rerun the container and share port `-p $PROMETHEUS_PORT:$PROMETHEUS_PORT` then connect to http://your_host:9090/targets?search=

#### grafana

copy file `grafana/datasources.yml` to `/var/app/metrics/grafana`

mkdir `/var/app/metrics/grafana/data`

chmod 777 `/var/app/metrics/grafana/data`

run this script in the console:
```shell
C_LOCAL_NETWORK=workgroup-network
GRAFANA_PORT=3000

docker stop grafana || true
docker rm grafana || true
docker network create $C_LOCAL_NETWORK || true
docker run -d --name grafana --network=$C_LOCAL_NETWORK -p $GRAFANA_PORT:$GRAFANA_PORT --mount type=bind,source=./,target=/etc/grafana/provisioning/datasources --mount type=bind,source=./data,target=/var/lib/grafana --restart unless-stopped grafana/grafana:9.5.2
```
http://your_host:3000/dashboards

#### mysql metrics exporter

create exporter mysql user
```mysql
CREATE USER 'exporter'@'%' IDENTIFIED BY 'BA0F46E976' WITH MAX_USER_CONNECTIONS 3;
GRANT PROCESS, REPLICATION CLIENT, SELECT ON *.* TO 'exporter'@'%';
```

https://github.com/prometheus/mysqld_exporter

run this script in the console:
```shell
C_LOCAL_NETWORK=workgroup-network

docker stop mysqld-exporter || true
docker rm mysqld-exporter || true
docker network create $C_LOCAL_NETWORK || true
docker run -d --name mysqld-exporter --network=$C_LOCAL_NETWORK --expose 9104 -e MYSQLD_EXPORTER_PASSWORD="BA0F46E976" --restart unless-stopped prom/mysqld-exporter --mysqld.address=eee-catalog-mysql:3306 --mysqld.username=exporter --collect.info_schema.innodb_metrics
```

#### ubuntu metrics exporter

https://github.com/prometheus/node_exporter

```shell
C_LOCAL_NETWORK=workgroup-network

docker stop node-exporter || true
docker rm node-exporter || true
docker network create $C_LOCAL_NETWORK || true
docker run -d --name node-exporter --network=$C_LOCAL_NETWORK --pid="host" -v "/:/host:ro,rslave" --restart unless-stopped quay.io/prometheus/node-exporter:latest --path.rootfs=/host --web.listen-address=:9100
```

#### redis
mkdir `/var/app/redis/data`
cd `/var/app/redis`

```shell
C_LOCAL_NETWORK=workgroup-network

docker stop redis-instance || true
docker rm redis-instance || true
docker network create $C_LOCAL_NETWORK || true
docker run -d --name redis-instance --network=$C_LOCAL_NETWORK --expose=6379 -v "./data:/data" --restart unless-stopped redis:latest
#web UI for redis
docker run -d --name redis-web-ui --network=$C_LOCAL_NETWORK -p 5001:5001 --restart unless-stopped marian/rebrow
```

#### redis exporter

https://github.com/oliver006/redis_exporter

```shell
C_LOCAL_NETWORK=workgroup-network

docker stop redis-exporter || true
docker rm redis-exporter || true
docker network create $C_LOCAL_NETWORK || true
docker run -d --name redis-exporter --network=$C_LOCAL_NETWORK --expose=9121 --restart unless-stopped oliver006/redis_exporter --redis.addr=redis-instance:6379
```
