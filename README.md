# masters
Java project, but the databases are in from Docker containers
Run main and send query requests through API to init benchmarking

##Mongo commands
    create mongodb database:
    docker run --name mongoDB -p 27888:27017 -d mongo
                                       
##CouchDB commands
    create couchdb database:
    docker run --name couchdb -p 5984:5984 -d bitnami/couchdb:latest
    default credentials -> admin couchdb

##Docker commands
    docker start containerName                          -> start existing conatainer
    docker stop containerName                           -> stop existing conatainer
    docker exec -it containerName appName               -> interactive shell
    docker ps                                           -> active containers 
    docker container ls --all                           -> all container
    docker rm -f containerName                          -> delete existing container
