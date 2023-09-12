package:
	mvnd clean install

run-jar:
	java -jar eux-nav-rinasak-webapp/target/eux-nav-rinasak-datasync.jar

run: package run-jar
