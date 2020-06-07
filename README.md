![FE Build CI](https://github.com/FeliceGeracitano/kafka-uber/workflows/FE%20Build%20CI/badge.svg?branch=master)
![BE build CI](https://github.com/FeliceGeracitano/kafka-uber/workflows/BE%20build%20CI/badge.svg?branch=master)

# kafka-uber
Streaming application POC

https://medium.com/@felice.geracitano/zero-to-demo-streaming-application-frontend-ec6cb95ee7d2
https://medium.com/@felice.geracitano/zero-to-streaming-application-backend-bf18fd1207ae
https://medium.com/@felice.geracitano/zero-to-streaming-application-infrastructure-6e4b98d63e1

### Run dependecies
```bash
$ cd infra
$ docker-compose up --build
```
> Confluent monitor dashboard available at: http://localhost:9021/clusters  
> Kibana available at http://localhost:5601/

### Run frontend
```bash
$ cd frontend
$ npm run dev
```
> Webapp available at: http://localhost:5000/

### Run backend
```bash
$ cd backend
$ ./gradlew bootRun
```

### Architecture
<p align="center">
<img src="https://user-images.githubusercontent.com/6695231/83954767-026a8800-a844-11ea-8a9d-0441fcd84696.png" data-canonical-src="https://user-images.githubusercontent.com/6695231/83954767-026a8800-a844-11ea-8a9d-0441fcd84696.png" width="600" />
</p>

### Messages Flow
<p align="center">
<img src="https://user-images.githubusercontent.com/6695231/83687943-77d81d80-a5e4-11ea-8e81-cfd8efc0c90d.png" data-canonical-src="ttps://user-images.githubusercontent.com/6695231/83687943-77d81d80-a5e4-11ea-8e81-cfd8efc0c90d.png" width="600" />
</p>

### Demos
<p align="center">
<img src="https://user-images.githubusercontent.com/6695231/83688255-00ef5480-a5e5-11ea-8e69-264be3d4dbfc.gif" data-canonical-src="https://user-images.githubusercontent.com/6695231/83688255-00ef5480-a5e5-11ea-8e69-264be3d4dbfc.gif" width="600" />
</p>
<p align="center">
<img src="https://user-images.githubusercontent.com/6695231/83688657-a86c8700-a5e5-11ea-91f5-b066e756fffb.png" data-canonical-src="https://user-images.githubusercontent.com/6695231/83688657-a86c8700-a5e5-11ea-91f5-b066e756fffb.png" width="600" />
</p>
