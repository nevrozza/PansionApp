package com.nevrozq.pansion

import isTestMode

//
//val sqlUrl =  System.getenv("DATABASE_CONNECTION_STRING") //"jdbc:postgresql://localhost:5432/pansionApp"//
//val sqlUser = System.getenv("POSTGRES_USER")// "postgres"//
//val sqlPassword = System.getenv("POSTGRES_PASSWORD")// "6556"//
//
//val sslAlias = System.getenv("SSL_ALIAS")//"www.pansion.app" //
//val sslPass = System.getenv("SSL_PASSWORD")//"123456" //
//
//val h_port = System.getenv("SERVER_PORT").toInt()// 8080
//val https_port = System.getenv("SERVER_HTTPS_PORT").toInt()//8443//
//
//val ratingDelay = System.getenv("RATING_DELAY").toInt()//5//


val sqlUrl =
    if (isTestMode) "jdbc:postgresql://localhost:5432/pansionApp" else System.getenv("DATABASE_CONNECTION_STRING") //
val sqlUser = if (isTestMode) "postgres" else System.getenv("POSTGRES_USER")//
val sqlPassword = if (isTestMode) "6556" else System.getenv("POSTGRES_PASSWORD")//

val sslAlias = if (isTestMode) "www.pansion.app" else System.getenv("SSL_ALIAS")//
val sslPass = if (isTestMode) "123456" else System.getenv("SSL_PASSWORD")//

val h_port = if (isTestMode) 8080 else System.getenv("SERVER_PORT").toInt()//
val https_port = if (isTestMode) 8443 else System.getenv("SERVER_HTTPS_PORT").toInt()//

val ratingDelay = if (isTestMode) 5 else System.getenv("RATING_DELAY").toInt()//