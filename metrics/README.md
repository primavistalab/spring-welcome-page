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

https://github.com/prometheus/mysqld_exporter/tree/main?tab=readme-ov-file#collector-flags

run this script in the console:
```shell
C_LOCAL_NETWORK=workgroup-network

docker stop mysqld-exporter || true
docker rm mysqld-exporter || true
docker network create $C_LOCAL_NETWORK || true
docker run -d --name mysqld-exporter --network=$C_LOCAL_NETWORK -p 9104:9104 -e MYSQLD_EXPORTER_PASSWORD="BA0F46E976" --restart unless-stopped prom/mysqld-exporter --mysqld.address=eee-catalog-mysql:3306 --mysqld.username=exporter --collect.info_schema.innodb_metrics
```