![FE Build CI](https://github.com/FeliceGeracitano/kafka-uber/workflows/FE%20Build%20CI/badge.svg?branch=master)
![BE build CI](https://github.com/FeliceGeracitano/kafka-uber/workflows/BE%20build%20CI/badge.svg?branch=master)

# kafka-uber

Kafka Uber Demo App

### installation

```
git clone --recurse-submodules https://github.com/FeliceGeracitano/kafka-uber.git
```

### frontend

- svelte
- mapbox

### backend

- kotlin

### infra

```
$ cd git/examples/cp-all-in-one
$ docker-compose up
```

> dashboard running at: http://localhost:9021/

#### deprecated

- kubernetes?
- kafka
- dependecies:
  - helm

```bash
brew install helm
```

- deploy cluster

```bash
$ kubectl config set-context --current --namespace=kafka-uber

$ cd ./infra
helm install kafka-uber cp-helm-charts -f ./values.yaml
```

- destroy cluster

```bash
helm unistall kafka-uber ?
```

## kafka topic

### trip

| key      | value                                                                              |
| -------- | ---------------------------------------------------------------------------------- |
| `tripId` | {"STATUS": "", "riderId": String,"driverId": String, from: Location, to: Location} |

### rider

| key       | value                                    |
| --------- | ---------------------------------------- |
| `riderId` | {"location": Location, currentTripId:""} |

### driver

| key        | value                                    |
| ---------- | ---------------------------------------- |
| `driverId` | {"location": Location, currentTripId:""} |

## TRIP STATUSES

- REQEUSTING
- CONFIRMED
- STARTED
- ENDED
