clear
mvn hpi:run -Djetty.port=8090 -Pjenkins,runsh -Djava.net.preferIPv4Stack=true -Djenkins.CLI.disabled=true
