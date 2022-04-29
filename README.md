# masters project
- Java project, but the databases are in from Docker containers
- Start containers before starting API
- Run main() and send API requests to init benchmarking
---
##Included
- Coverage over 80%
- NO_BENCH data generator
- Database loader
- Query runner
---
##Requirements
- Java11
- Docker
---
##Mongo commands
    create mongodb database:
    docker run --name mongoDB -p 27888:27017 -d mongo
                                       
##CouchDB commands
    create couchdb database:
    docker run --name couchdb -p 5984:5984 -d bitnami/couchdb:latest
    default credentials -> admin couchdb

##Aerospike commands
    create aerospike database:
    sudo docker run -d --name aerospike -p 3000-3002:3000-3002 aerospike:ce-5.7.0.16

##Docker commands
    docker start containerName                          -> start existing conatainer
    docker stop containerName                           -> stop existing conatainer
    docker exec -it containerName appName               -> interactive shell
    docker ps                                           -> active containers 
    docker container ls --all                           -> all container
    docker rm -f containerName                          -> delete existing container
---
###cURL for APIs
####Init databases
```
curl --location --request POST 'http://localhost:4567/start' \
--header 'Content-Type: application/json' \
--data-raw '{
    "userId": "USER",
    "fileName": "/test.json",
    "databaseName": "test",
    "reloadDatabase": false,
    "types": [
        "MONGO",
        "COUCH_DB",
        "AEROSPIKE"
    ]
}'
```

####Run queries
```
curl --location --request POST 'http://localhost:4567/run' \
--header 'Content-Type: application/json' \
--data-raw '{
    "sessionId": "USER-2022-04-08T17:09:32.181411",
    "colName": "test",
    "numberOfRuns": 2,
    "querySet": [
        {
            "name": "test1",
            "type": "SEARCH",
            "queries": [
                {
                    "db": "MONGO",
                    "query": ["{ ord_qty: 501 }"]
                },
                {
                    "db": "COUCH_DB",
                    "query": ["{\n    \"selector\": {\n        \"_id\": \"5677d313fad7da08e362a512\"    },\n    \"fields\": [\"_id\", \"_rev\"],\n    \"limit\": 1,\n    \"skip\": 0,\n    \"execution_stats\": true\n}"]
                },
                {
                    "db": "AEROSPIKE",
                    "query": ["$.*"]
                }
            ]
        }
    ]
}'
```

####Get results
```
curl --location --request GET 'http://localhost:4567/results/USER-2022-04-08T17:09:32.181411'
```

####Generate csv
```
curl --location --request GET 'http://localhost:4567/generate/USER-2022-04-08T17:09:32.181411'
```

####criterion
    sudo apt-get install libghc-criterion-dev
