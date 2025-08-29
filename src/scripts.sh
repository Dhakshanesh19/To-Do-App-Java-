#clean install skip tests
mvn clean install -DskipTests

#run your springboot app
mvn spring-boot:run

mvn gatling:test -Dgatling.simulationClass=simulations.SampleSimulation