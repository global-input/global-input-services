docker rm -f global_input_services
#docker run --network=iterativesolution_default --name global_input-services -p 8080:8080 -t dilshat/global-input-services 
docker-compose build
docker-compose up