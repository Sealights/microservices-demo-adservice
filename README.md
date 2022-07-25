# microservices-demo-adservice

Sample cloud-native application with 10 microservices showcasing Kubernetes, Istio, gRPC and
OpenTelemetry.

# Http server

## Configuration

* Default http port 9556
* Port could be re-defined with `HTTP_PORT` environment variable

## Example of usage

### Request random category products

* Request

```text
curl -svg localhost:9556/ads | jq .
```

* Response

```json
{
  "random": [
    {
      "productPath": "/product/1YMWWN1N4O",
      "text": "Watch for sale. Buy one, get second kit for free"
    },
    {
      "productPath": "/product/6E92ZMYYFZ",
      "text": "Mug for sale. Buy two, get third one for free"
    }
  ]
}
```

### Request ads for multiple categories

* Request

```text
curl -svg "localhost:9556/ads?category=decor&category=accessories" | jq .
```

* Response

```json
{
  "decor": [
    {
      "productPath": "/product/0PUK6V6EV0",
      "text": "Candle holder for sale. 30% off."
    }
  ],
  "accessories": [
    {
      "productPath": "/product/1YMWWN1N4O",
      "text": "Watch for sale. Buy one, get second kit for free"
    }
  ]
}
```
