

# Docker Socket Activation

Instructions on how to use `systemd-socket-proxyd` to dynamically service requests by running a Docker container.

## Step 1: Create docker container

`docker create --name nginx8080 -p 8080:80 nginx`

variables:

- docker container name (nginx8080)
- docker host port (8080)
- docker container port (80)
- docker image name (nginx)

## Step 2: Create socket descriptor

Place the following content into `/etc/systemd/system/nginx-proxy.socket`:

```
    [Socket]
    ListenStream=80
    
    [Install]
    WantedBy=sockets.target
```

variables:

- socket file name (nginx-proxy.socket)
- `ListenStream` port (80)

## Step 3: Tell systemd to start it

```
systemctl enable nginx-proxy.socket
systemctl start nginx-proxy.socket
```

variables:

- socket file name (nginx-proxy.socket); same as step 2


## Step 4: Create the proxy service file

Place the following content into `/etc/systemd/system/nginx-proxy.service`:

```
[Unit]
Requires=nginx-docker.service
After=nginx-docker.service

[Service]
ExecStart=/lib/systemd/systemd-socket-proxyd 127.0.0.1:8080
```

variables:

- docker service file name (nginx-docker.service)
- service host (127.0.0.1)
- service port (8080)


## Step 5: Create the docker service file

Place the following content into `/etc/systemd/system/nginx-docker.service`:

```
[Unit]
Description=nginx container

[Service]
ExecStart=/usr/bin/docker start -a nginx8080
ExecStartPost=/usr/local/bin/waitport 127.0.0.1 8080

ExecStop=/usr/bin/docker stop nginx8080
```

variables:

- docker service file name (nginx-docker.service); same as in step 4
- description (nginx container)
- docker engine path (/usr/bin/docker)
- docker container name (nginx8080); same as in step 1
- `ExecStartPost` script path (/usr/local/bin/waitport); args should match service host/port from step 4

## Step 6: Create waitport script

Place the following content in `/usr/local/bin/waitport` and make it executable:

```
#!/bin/bash

host=$1
port=$2
tries=600

for i in `seq $tries`; do
    if /bin/nc -z $host $port > /dev/null ; then
      # Ready
      exit 0
    fi

    /bin/sleep 0.1
done

# FAIL
exit -1
```

It takes two arguments: service host and port (same as in step 3)

variables:

- waitport path (/usr/local/bin/waitport)
- tries (600)
- netcat path (/bin/nc)
- sleep period (0.1)

Checks if container is responding and retries every tenth of a second up to one minute.


## Step 7: Test

curl -v http://localhost:8080



