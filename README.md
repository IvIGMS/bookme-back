Requisitos:

maven 3.9.9 (o superior)
java 17 (o superior)
docker para ejecutar el docker compose

Pasos:
Ejecutar el docker compose para crear la bbdd en docker.
mvn compile
levantar proyecto:

De primeras hay dos endpoints disponibls:

      Registro:

curl --location 'http://localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "friasgilivan@gmail.com",
  "password": "123456",
  "firstname": "Ivan",
  "lastname": "Frias",
  "role": "USER"
}'

      Login:

curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
  "email": "friasgilivan@gmail.com",
  "password": "123456"
}'
