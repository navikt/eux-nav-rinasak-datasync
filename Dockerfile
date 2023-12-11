FROM ghcr.io/navikt/baseimages/temurin:21

ADD eux-nav-rinasak-datasync-webapp/target/eux-nav-rinasak-datasync.jar /app/app.jar
