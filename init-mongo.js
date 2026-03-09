// init-mongo.js
db = db.getSiblingDB('carbon-calc');

db.getCollection("energyEmissionFactor").insertMany([
  { "_id": "AC", "factor": 0.45 },
  { "_id": "AL", "factor": 0.5 },
  { "_id": "AM", "factor": 0.75 },
  { "_id": "AP", "factor": 0.7 },
  { "_id": "BA", "factor": 0.55 },
  { "_id": "CE", "factor": 0.52 },
  { "_id": "DF", "factor": 0.6 },
  { "_id": "ES", "factor": 0.5 },
  { "_id": "GO", "factor": 0.48 },
  { "_id": "MA", "factor": 0.65 },
  { "_id": "MT", "factor": 0.43 },
  { "_id": "MS", "factor": 0.44 },
  { "_id": "MG", "factor": 0.53 },
  { "_id": "PA", "factor": 0.58 },
  { "_id": "PB", "factor": 0.51 },
  { "_id": "PE", "factor": 0.54 },
  { "_id": "PI", "factor": 0.56 },
  { "_id": "PR", "factor": 0.4 },
  { "_id": "RJ", "factor": 0.57 },
  { "_id": "RN", "factor": 0.5 },
  { "_id": "RO", "factor": 0.68 },
  { "_id": "RR", "factor": 0.8 },
  { "_id": "RS", "factor": 0.42 },
  { "_id": "SC", "factor": 0.4 },
  { "_id": "SE", "factor": 0.54 },
  { "_id": "SP", "factor": 0.47 },
  { "_id": "TO", "factor": 0.65 }]
);


db.getCollection("transportationEmissionFactor").insertMany([
  { "_id": "CAR", "factor": 0.19 },
  { "_id": "MOTORCYCLE", "factor": 0.09 },
  { "_id": "PUBLIC_TRANSPORT", "factor": 0.04 },
    { "_id": "BICYCLE", "factor": 0 }
]);


db.getCollection("solidWasteEmissionFactor").insertMany([
    { "_id": "AC", "recyclableFactor": 0.45, "nonRecyclableFactor": 0.99 },
    { "_id": "AL", "recyclableFactor": 0.43, "nonRecyclableFactor": 0.95 },
    { "_id": "AM", "recyclableFactor": 0.5, "nonRecyclableFactor": 1.05 },
    { "_id": "AP", "recyclableFactor": 0.48, "nonRecyclableFactor": 1.02 },
    { "_id": "BA", "recyclableFactor": 0.42, "nonRecyclableFactor": 0.96 },
    { "_id": "CE", "recyclableFactor": 0.44, "nonRecyclableFactor": 0.98 },
    { "_id": "DF", "recyclableFactor": 0.41, "nonRecyclableFactor": 0.93 },
    { "_id": "ES", "recyclableFactor": 0.46, "nonRecyclableFactor": 0.97 },
    { "_id": "GO", "recyclableFactor": 0.43, "nonRecyclableFactor": 0.94 },
    { "_id": "MA", "recyclableFactor": 0.47, "nonRecyclableFactor": 1.01 },
    { "_id": "MT", "recyclableFactor": 0.42, "nonRecyclableFactor": 0.93 },
    { "_id": "MS", "recyclableFactor": 0.41, "nonRecyclableFactor": 0.91 },
    { "_id": "MG", "recyclableFactor": 0.45, "nonRecyclableFactor": 0.95 },
    { "_id": "PA", "recyclableFactor": 0.48, "nonRecyclableFactor": 1.02 },
    { "_id": "PB", "recyclableFactor": 0.43, "nonRecyclableFactor": 0.96 },
    { "_id": "PE", "recyclableFactor": 0.44, "nonRecyclableFactor": 0.98 },
    { "_id": "PI", "recyclableFactor": 0.45, "nonRecyclableFactor": 0.97 },
    { "_id": "PR", "recyclableFactor": 0.4, "nonRecyclableFactor": 0.9 },
    { "_id": "RJ", "recyclableFactor": 0.46, "nonRecyclableFactor": 0.98 },
    { "_id": "RN", "recyclableFactor": 0.43, "nonRecyclableFactor": 0.96 },
    { "_id": "RO", "recyclableFactor": 0.49, "nonRecyclableFactor": 1.03 },
    { "_id": "RR", "recyclableFactor": 0.5, "nonRecyclableFactor": 1.05 },
    { "_id": "RS", "recyclableFactor": 0.4, "nonRecyclableFactor": 0.91 },
    { "_id": "SC", "recyclableFactor": 0.41, "nonRecyclableFactor": 0.92 },
    { "_id": "SE", "recyclableFactor": 0.44, "nonRecyclableFactor": 0.98 },
    { "_id": "SP", "recyclableFactor": 0.42, "nonRecyclableFactor": 0.94 },
    { "_id": "TO", "recyclableFactor": 0.48, "nonRecyclableFactor": 1.02 }
]);