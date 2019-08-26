# Flink Notification Bot

A notification bot to receive Travis webhook payload and send Email notifications to builds@flink.apache.org.

## How it Works?

1. Travis CI's [webhook notification](https://docs.travis-ci.com/user/notifications/#Webhook-notifications) delivers a POST request to the specified endpoint a JSON payload as described.
2. Encrypt the webhook url in the `.travis.yml` e.g: `travis encrypt "http://<ip>:9000/travis" --add notifications.webhooks.urls`.
 - Where `<ip>` is the VM ip address hosts the bot service. The `:9000/travis` is the port and path where the bot monitors.
 - Encrypting the webhoot url should address the issue of forked repos sending notifications to the mailing list.
3. The Bot receives the POST request, unparses the payload, generates it into an HTML email.
4. Send the email to builds@flink.apache.org using the configured email address and password.

## How to Deploys?

- Prepare a VM which has a public ip address.
- Clone/Download the project in the VM.
- Add `config.properties` file under `flink-notification-bot/src/main/resources`, and fill in `email.from`, and `email.password` and `email.to`. Take `config.properties.example` as an example.
- `mvn clean package -DskipTests` under the root of the project to build the project.
- run `./run.sh &` in background.
- Checks the `bot.log` to see whether the bot service is launched successfully.

## Feature requests

* [ ] Verifying the [request signature](https://docs.travis-ci.com/user/notifications/#verifying-webhook-requests).