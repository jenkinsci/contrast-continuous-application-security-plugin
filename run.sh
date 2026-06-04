clear
mvn hpi:run -Djetty.port=8090 -Prunsh -Djava.net.preferIPv4Stack=true -Djenkins.CLI.disabled=true -Denforcer.skip=true
