@echo off

docker build -f .\user\user.Dockerfile -t user-app .
docker build -f .\order\order.Dockerfile -t order-app .

docker-compose up -d