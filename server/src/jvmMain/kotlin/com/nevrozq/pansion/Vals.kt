package com.nevrozq.pansion

val sqlUrl =  System.getenv("DATABASE_CONNECTION_STRING") //"jdbc:postgresql://localhost:5432/pansionApp"//
val sqlUser = System.getenv("POSTGRES_USER")// "postgres"//
val sqlPassword = System.getenv("POSTGRES_PASSWORD")// "6556"//

val sslAlias = System.getenv("SSL_ALIAS")//"www.pansion.app" //
val sslPass = System.getenv("SSL_PASSWORD")//"123456" //

val h_port = System.getenv("SERVER_PORT").toInt()// 8080
val https_port = System.getenv("SERVER_HTTPS_PORT").toInt()//8443//

val ratingDelay = System.getenv("SERVER_HTTPS_PORT").toInt()//5//