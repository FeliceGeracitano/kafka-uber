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
helm install kuber cp-helm-charts -f ./values.yaml helm install --namespace kuber
```

- destroy cluster

```bash
helm unistall kuber --namespace kuber ?
```
