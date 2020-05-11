REST Based ATM Service using AKKA-HTTP with JWT

This is a simple REST based ATM Service application which is implemented using Scala, AKKA-HTTP, JWT (JSON Web Token) and MySQL.
The application has two external Actor namely - User and Operator.
It has the following offerings as feature to it's external Actors:
    1. USER - Check Balance, Deposit Money, Withdraw Money, Change Pin Code
    2. OPERATOR - Create Operator(s), Create User(s), Deposit Money, Check Balance of ATM

Note: Application currently assumes that we have only one ATM.
    
The API and Database DDL statements are given below:

========================================================================================================================

APIs --

API to create the Access Token:

Endpoint:

    POST:   localhost:9000/atm/users/token/create
    
Body:
    
    {
    	"debitCardId" : 11223344,
    	"pinCode": 7777
    }
    
    
**Copy the Access-Token generated as the output and pass that access-token with every further API call in the Header as-
    Authorization : Access Token

========================================================================================================================

API to check balance of User:        

Endpoint:

    GET:    localhost:9000/atm/users/check/{debitCardID}
   
Example:   

    GET:    localhost:9000/atm/users/check/123456789
    
========================================================================================================================

API to withdraw money by User:

Endpoint: 

    PUT:    localhost:9000/atm/users/withdraw

Body:

    {
    	"debitCardID": 11223344,
    	"amount": 300
    }
    
========================================================================================================================

API to deposit money by User:

Endpoint:

    PUT:    localhost:9000/atm/users/deposit
    
Body:

    {
    	"debitCardID": 11223344,
    	"amount": 300
    }
    
========================================================================================================================

API to change PIN by User:

Endpoint:

    PUT:    localhost:9000/atm/users/changepin
    
Body:

    {
    	"debitCardID": 112233445,
    	"oldPin": 7777,
    	"newPin": 9999
    }
    
========================================================================================================================

API to create User(s):

Endpoint:

    POST:   localhost:9000/atm/operator/create/users

Body:

    [
    	{
    		"userId": "U23456",
    		"name": "Alice",
    		"debitCardID": 11223344,
    		"pin": 7777,
    		"balance": 3000.00
    	},
    	{
    		"userId": "U1122",
    		"name": "Bob",
    		"debitCardID": 55667788,
    		"pin": 1234,
    		"balance": 5500.00
    	}
    ]
    
========================================================================================================================

API to create Operator(s):

Endpoint:

    POST:   localhost:9000/atm/operator/create/operator    
    
Body:

    [
    	{
    		"operID": "O123",
    		"password": "oper#"
    	}
    ]
    
========================================================================================================================

API to deposit money by Operator:

Endpoint:

    PUT:    localhost:9000/atm/operator/deposit
    
Body:

    {
    	"operID": "O123",
    	"depositAmount": 5000
    }
    
========================================================================================================================

API to check balance of ATM by Operator:

Endpoint:

    GET:    localhost:9000/atm/operator/balance
    
========================================================================================================================

Database DDLs --

create database atm;

use atm;

create table users (
userid varchar(20) primary key,
name varchar(30) NOT NULL,
debitcardid bigint NOT NULL unique, 
pin smallint NOT NULL,
balance double NOT NULL
);

create table atm (
atmid varchar(20) primary key,
location varchar(30) NOT NULL,
balance int NOT NULL
);

insert into atm values('Atm12345', 'Noida Sec 37', 20000);

create table operator (
operid varchar(10) primary key,
password varchar(10) NOT NULL
);