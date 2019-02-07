# tothecomments API
API that finds reddit and Hacker News posts for a given url

## Requirements
* [OpenJDK 8](https://openjdk.java.net/install/)
* [Docker](https://docs.docker.com/install/)
* [Now CLI](https://github.com/zeit/now-cli) (Only necessary for deploys)

## Build

For development builds just invoke gradle normally:

```bash
./gradlew build
```

To build the docker image:

```bash
docker build .
```

## Develop

Start build server with file watcher
```bash
./gradlew -t build
```

Start the application (In a different terminal window/tab)
```bash
./gradlew run
```

Gradle will rebuild automatically on file change and server will hot-load changes

## Deploy

* Get reddit OAuth credentials from the [settings page](https://www.reddit.com/prefs/apps) (Application type should be web app)
* Set up your zeit.co account using the now cli
```bash
now login
```
* Store reddit credentials in your zeit.co account:
```bash
now secrets add reddit_client_id <client_id>
now secrets add reddit_client_secret <client_secret>
```
* Serve the app:
```bash
now deploy
```
