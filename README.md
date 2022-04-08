# masters
Java project, but the databases are in from Docker containers

##Mongo
    docker run --name mongoDB -p 27888:27017 -d mongo
    docker start mongoDB
    docker exec -it mongoDB mongo

    use test
##CouchDB
    docker run --name couchdb -p 5984:5984 -d bitnami/couchdb:latest
        credentials -> admin couchdb
    docker start couchdb

##Docker
    docker ps
    docker container ls --all
    docker rm -f containerName
