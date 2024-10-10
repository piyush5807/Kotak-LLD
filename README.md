
This project is built using Java and Spring boot

Curl for initiating cash withdrawal : 
curl --location 'localhost:8080/transactions/withdraw' \
--header 'Content-Type: application/json' \
--data '{
    "amount": "500",
    "account": {
        "id": 10
    },
    "securityKey": "1009"
}'


Curl for retrieving transaction by transaction id:

curl --location 'localhost:8080/transactions/{transactionId}'
