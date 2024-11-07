docker run -d ^
  --name mongo-container ^
  -p 27017:27017 ^
  -e MONGO_INITDB_DATABASE=flights ^
  mongo:5.0.5