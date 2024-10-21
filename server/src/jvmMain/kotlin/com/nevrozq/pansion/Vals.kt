package com.nevrozq.pansion

val sqlUrl =  System.getenv("DATABASE_CONNECTION_STRING") //"jdbc:postgresql://localhost:5432/pansionApp"//
val sqlUser = System.getenv("POSTGRES_USER")// "postgres"//
val sqlPassword = System.getenv("POSTGRES_PASSWORD")// "6556"//

val sslAlias = System.getenv("SSL_ALIAS")//"www.pansion.app" //
val sslPass = System.getenv("SSL_PASSWORD")//"123456" //

val h_port = System.getenv("SERVER_PORT").toInt()// 8080
val https_port = System.getenv("SERVER_HTTPS_PORT").toInt()//8443//

val ratingDelay = System.getenv("RATING_DELAY").toInt()//5//


//val sqlUrl = "jdbc:postgresql://localhost:5432/pansionApp"//  System.getenv("DATABASE_CONNECTION_STRING") //
//val sqlUser =  "postgres"//System.getenv("POSTGRES_USER")//
//val sqlPassword = "6556"// System.getenv("POSTGRES_PASSWORD")//
//
//val sslAlias = "www.pansion.app" //System.getenv("SSL_ALIAS")//
//val sslPass = "123456" //System.getenv("SSL_PASSWORD")//
//
//val h_port = 8080//System.getenv("SERVER_PORT").toInt()//
//val https_port = 8443// System.getenv("SERVER_HTTPS_PORT").toInt()//
//
//val ratingDelay = 5//System.getenv("RATING_DELAY").toInt()//