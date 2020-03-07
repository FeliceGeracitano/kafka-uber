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
