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
  "ads": [
    {
      "redirect_url": "/product/L9ECAV7KIM",
      "text": "Loafers for sale. Buy one, get second one for free"
    },
    {
      "redirect_url": "/product/2ZYFJ3GM2N",
      "text": "Hairdryer for sale. 50% off."
    }
  ]
}
```

### Request ads for multiple categories

* Request

```text
curl -svg "localhost:9556/ads?category=decor&context_keys=accessories" | jq .
```

* Response

```json
{
  "ads": [
    {
      "redirect_url": "/product/0PUK6V6EV0",
      "text": "Candle holder for sale. 30% off."
    },
    {
      "redirect_url": "/product/1YMWWN1N4O",
      "text": "Watch for sale. Buy one, get second kit for free"
    }
  ]
}
```

### Request ads with POST

* Request

```text
curl -svg "localhost:9556/ads" -d '{"context_keys": ["decor", "accessories", "decor" ] }' | jq .
```

* Response

```json
{
  "ads": [
    {
      "redirect_url": "/product/0PUK6V6EV0",
      "text": "Candle holder for sale. 30% off."
    },
    {
      "redirect_url": "/product/1YMWWN1N4O",
      "text": "Watch for sale. Buy one, get second kit for free"
    }
  ]
}
```

