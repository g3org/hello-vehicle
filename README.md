# Hello, Vehicle API

Tested with 
* OpenJDK 8.0.265
* Gradle 6.8-rc-1

## Start API

```
./gradlew run
```

## Import Data

Import all data points from CSV:
```
./gradlew importCSV
```

Check, if all data is imported:
```
curl -H "Authorization: Bearer 123bf42" localhost:4567/status
```

## Cucumber Tests

run the tests
```
./gradlew test
```

check test report
```
open ./app/build/reports/tests/test/index.html 
```

## API

### authorization

add the authorization header to each requst:

```
Authorization: Bearer 123bf42
```

### status (GET `/status`)

```
curl -H "Authorization: Bearer 123bf42" localhost:4567/status
```

Status 200:

```
{"health":"green","dataPoints":"0"}
```

### add vehicle (PUT `/vehicle/{vehicleId}/`)

```
curl -XPUT -H "Authorization: Bearer 123bf42" localhost:4567/vehicle/my-vehicle-1 -d '{"timestamp":1234,"latitude":1.1,"longitude":2.2,"heading":9,"session":"34af2b","vehicle":"my-vehicle-1"}'
```

### get sessions for vehicle (GET `/vehicle/{vehicleId}/sessions`)

```
curl -H "Authorization: Bearer 123bf42" localhost:4567/vehicle/my-vehicle-1/sessions  
```

Status 200:
```json
["34af2b"]
```

### last position of vehicle (GET `/vehicle/{vehicleId}/last`)

```
curl -H "Authorization: Bearer 123bf42" localhost:4567/vehicle/my-vehicle-1/last 
```

Status 200:
```json
{"timestamp":1234,"latitude":1.1,"longitude":2.2,"heading":9,"session":"34af2b","vehicle":"my-vehicle-1"}
```