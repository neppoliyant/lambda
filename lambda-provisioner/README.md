
# steps for running exported api gateway

- use Provisioner to create docker host on digitalocean
- ssh root@${IP}
- docker login --email=shane@digitalsanctum.com --username=digitalsanctum --password=s0crat3s
- docker pull digitalsanctum/lambda-api
- start the container

***REMOVED***

- verify with

    curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Shane", "lastName":"Witbeck"}' 'http://${IP}:8080/hello'
