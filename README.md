# Rating-System

Just playing around with events, ktor, kafka and kafka streams.

## How it works

The goal of this project is to create a rating system application.

1- The application make a post request in order to rate a movieId for then publish to a kafka topic.

2- The kafka streams will consume the topic and calculate the average rating for each movieId and publish to another
topic rating-averages.

3- The application will consume the rating-averages using a websocket connection.

> See the com.example.rating.adapter.ktor.plugin.Http and com.example.rating.adapter.ktor.plugin.Websockets in order to
> see the routes

## Credits

This project was based on the [Ktor-Kafka](https://github.com/gAmUssA/ktor-kafka/tree/main)
from [gAmUssA](https://github.com/gAmUssA)
