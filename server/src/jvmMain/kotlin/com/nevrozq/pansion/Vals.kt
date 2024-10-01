package com.nevrozq.pansion

val sqlUrl = "jdbc:postgresql://localhost:5432/pansionApp"//System.getenv("DATABASE_CONNECTION_STRING")
val sqlUser = "postgres"//System.getenv("POSTGRES_USER")
val sqlPassword = "6556"//System.getenv("POSTGRES_PASSWORD")

val sslAlias = "www.pansion.app" //System.getenv("SSL_ALIAS")
val sslPass = "123456" //System.getenv("SSL_PASSWORD").toInt()

val h_port = 8080//System.getenv("SERVER_PORT").toInt()
val https_port = 8443//System.getenv("SERVER_HTTPS_PORT").toInt()

val ratingDelay = 5//System.getenv("SERVER_HTTPS_PORT").toInt()