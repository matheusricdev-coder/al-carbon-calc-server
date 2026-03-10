# AL Carbon Calculator

## Description

Create the backend for a carbon calculator, using Java, Spring Boot and MongoDB.

There are only 3 endpoints that need to be implemented:

### [POST] /open/start-calc

Starts the calculation process. Receives the user basic info and stores a new calculation in the database. Returns the
calculation's id
to be used in the following endpoints. For this endpoint, every parameter is mandatory (name, email, phoneNumber and
UF).

### [PUT] /open/info

Receives information needed to calculate the user's carbon emission (energy consumption, transportation and solid waste
production) and stores it in the database.

Please consider `recyclePercentage` as a double from 0 to 1.0, representing the percentage of recyclable solid waste.

If this endpoint is called a second time for the same id, all its parameters must be overwritten.

### [GET] /open/result/{id}

Returns the carbon footprint for the calculation with the given id.

All these endpoints are already defined in the class `OpenRestController`. You should implement the methods in this
class.

## Calculator logic

There are emission factors already saved in the database for energy consumption (`EnergyEmissionFactor.class`),
transportation (`TransportationEmissionFactor.class`) and solid waste (`SolidWasteEmissionFactor.class`). These factors
must be used to calculate the full carbon emission for this user, according to the following formulas:

### Energy consumption

The class `EnergyEmissionFactor` contains the emission factors for each brazilian state (UF). The emission follows the
formula:

```Carbon emission = energy consumption * emission factor```

### Transportation

The class `TransportationEmissionFactor` contains the emission factors for each type of transportation. The emission
follows the formula:

```Carbon emission = distance * transportation type emission factor```

### Solid waste

The class `SolidWasteEmissionFactor` contains the emission factors for recyclable and non-recyclable solid waste. The
emission follows the formula:

```Carbon emission = solid waste production * emission factor```
There are a few implemented classes to check the application's health, security and swagger configs and so on. There's
probably no need to modify them, but if you think it's necessary, go ahead.
