# Mosquitto Showcase

Java application sending messages to Eclipse Mosquitto broker, that is monitored by Node-RED.

My little exploration of how to do stuff. 

MQTT Version 3.1.1

## Mosquitto

[Home](https://mosquitto.org/)  
[Docker](https://hub.docker.com/_/eclipse-mosquitto)

* It is the same for both 3.1.1 and 5.0

### 1. Prepare local folders and the config file `mosquitto.conf`

```console
> mkdir c:\DockerShared\Mosquitto\
> mkdir c:\DockerShared\Mosquitto\Data
> vim c:\DockerShared\Mosquitto\mosquitto.conf
```

```ini
# Optional: Listener for WebSockets (port 9001)
listener 9001
protocol websockets

# Allow anonymous connections (set to false if you want authentication)
allow_anonymous true

# Optional: Password file for users (when allow_anonymous is false)
#password_file /mosquitto/config/passwords.txt

# Persistence (saves the messages when the broker restarts)
persistence true
persistence_location /mosquitto/data/

# Log to stdout (good for seeing logs in Docker container logs)
log_dest stdout

# Optional: Define logging level
log_type error
log_type warning
log_type notice
log_type information

# Optional: Set max client connections (default is no limit)
# max_connections 100

# Optional: Set client keep-alive time (default is 60 seconds)
# keepalive_interval 60
```

* The password file is generated with `mosquitto_passwd` utility

### 2. Pull & Run the Broker

```console
> docker run --name mosquitto -it -p 1883:1883 -p 9001:9001 ^
    -v c:\DockerShared\Mosquitto\mosquitto.conf:/mosquitto/config/mosquitto.conf ^
    -v c:\DockerShared\Mosquitto\Data:/mosquitto/data ^
    eclipse-mosquitto:2.0.19
```

### 3. Test the instance

Open two consoles with

```console
docker exec -it mosquitto /bin/sh
```

In the first, listen.

```bash
mosquitto_sub -h localhost -p 1883 -t "test/topic"
```

And in the other, send a message

```bash
mosquitto_pub -h localhost -p 1883 -t "test/topic" -m "test message"
```


## Node-RED

[Home](https://nodered.org/)  
[Docker](https://hub.docker.com/r/nodered/node-red)


### 1. Pull & Run the Server

```console
> docker run --name node-red -it -p 1880:1880 ^
    -v c:\DockerShared\Node-RED\Data:/data ^
    nodered/node-red:4.0.3-22
```

### 2. Configure the Debugger

1. Open the [Web Console](http://localhost:1880/)
2. Add *mqtt in* node (from *network* section).
3. Add new server, 
   * the address is from docker DNS: 'mosquitto',
   * select "*subscribe to single topic*" and
   * fill in the topic name `test/topic`.
4. Done.
5. Add *debug* node (*common* section).
6. Connect the nodes.
7. Deploy.

### Test

Now send some messages again (also this application sends the messages, and you should see them in the *debug* panel (2nd icon from the right, on the right side of the screen).

## Run the Docker Image

Run with docker plugin (pom-configured hostname is `mosquitto` as in docker DNS)

```console
> mvn clean package
> mvn docker:run -Dbroker.hostname=<optional mqtt broker hostname> 
```
or old school (default hostname is `localhost`)

```
> mvn clean package
> cd target
> java -jar demo-mosquitto-1.0.0.jar <optional mqtt broker hostname>
```

## Run with Docker Compose

Build everything, including the docker image, with maven:

```console
> mvn clean package
> docker compose up -d
```

Open the [Node-RED Web Console](http://localhost:1880/) and watch the incoming messages.

Now play with anything you want.
