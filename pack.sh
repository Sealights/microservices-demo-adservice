docker build -t adservice .
docker tag adservice:latest 159616352881.dkr.ecr.eu-west-1.amazonaws.com/microservices-demo-adservice:latest
docker push 159616352881.dkr.ecr.eu-west-1.amazonaws.com/microservices-demo-adservice:latest
