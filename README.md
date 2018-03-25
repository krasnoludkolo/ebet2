# ebet2

## Summary
Ebet2 is a web application which allows you to make safe bets on matches.

Current features:
1. Create leagues and add matches to them.
2. Make, update, delete bets on matches.
3. Automatically update user's score when match result is set.

Planned features:
1. Create user module to register users.
2. Add source to automatically download matches info from external source.
3. API security
4. CQRS 
5. Try to remove spring ;)
6. A few implementation details

## Frameworks and libraries

* spring boot, data 
* vavr
* lombok

Postgesql as database

## Architecture details
Application is divided into independent modules. All modules are hidden by package-scope
and provide facade with operations which other modules can use. When a module requires
some other module, he must use his facade. If needed module takes care
of IO (database, endpoints) on it own, it doesn't inform world e.g. which database it
is using.

Each domain part of the modules is written in pure java. I'm trying to remove dependencies to
infrastructure using e.g. repository pattern. This approach allows me to provide different
implementation in configuration class. For example: during a test I use 'in memory' version
of all modules, without IO, so I don't have to use Mockito to mock database because I
provide HashMap-based repository implementation. This is generally faster.

A big advantage of this kind of separation is a possibility to take some module and 
turn them into micro-service (I heard that it's fashionable).

**Modules:**
* League

Provides operation to create, delete etc. operations on leagues and matches.

* Bet

Provides possibilities to make, update etc. bets on matches.

* Results

Handle calculating users score in each league.

* autoimport - not finished yet

Importing data from external source

## Spring
I try to avoid using spring 'magic'. Why? To find another way to build application then @Component and @Autowired.
Currently I use spring to put together modules as beans but I believe I could use ratpack and jooq as well.