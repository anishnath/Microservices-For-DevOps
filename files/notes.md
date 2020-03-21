docker build -t anishnath/demo:monolithic -f Docker.monolithic .
docker run -p 8080:8080 anishnath/demo:monolithic