# money-transfers

[![Build Status](https://travis-ci.org/micwypych/money-transfers.svg?branch=master)](https://travis-ci.org/micwypych/money-transfers)

##### Table of Contents  
[Project Overview](#overview)  
[How to run application](#runapp)  
[How to run tests](#runtests)  
[How to run load tests](#runloadtests) 
[Simplifications](#simplifications)   
[Dependencies](#dependencies)  

The project is built by the Travis-CI and the insight into the current build is by the badge above.


## How to run application ##
<a name="runapp"/>


Project is managed by Gradle and the gradle wrapper is provided which downloads the required verison of gradle. 
Therefore to download gradle, download all dependencies, build it, and run it only single command is neccessary to run:
```
./gradlew run
```
This should download all the dependencies and run the application. The application is available under:
[http://localhost:4567/](http://localhost:4567/account/1). And provides single endpoint __/account__


## How to run tests ##
<a name="runtests"/>

The unit tests are written in Groovy using [Spock](http://spockframework.org/) and are located in the standard [directory](src/test/groovy/com/revolut/transfer/account/). There is a single two user interraction [acceptance test/integration test](src/test/groovy/com/revolut/transfers/AppIntegrationTest.groovy). All of them are run by the following command

```
./gradlew test
```

In order to generate the test covarage report, one can run:

```
./gradlew jacocoTestReport
```

## How to run load tests ##
<a name="runloadtests"/>

The load tests are written in Scala using [Gatling](http://spockframework.org/) and are located in the standard [directory](src/gatling/simulations/). The tests can be run by the script which starts the application, creates the first account and starts the script:
```
./scripts/run_load_tests.sh
```

or manually by the gradle task (but the server has to be already running, and the account with id=1 has to be in PLN):
```
./gradlew gatlingRun
```



## Project overview ##
<a name="overview"/>

Application allows to make trasfers between local accounts. 
The accounts can be created with initial balance and for any currency.
However, transfers are required to be between accounts with the same currency.

The account is modeled in the spirit of DDD. 
The [Account](src/main/java/com/revolut/transfers/account/domain/Account.java) is an Enitty which is an Aggragate Root with [Entries](src/main/java/com/revolut/transfers/account/domain/Entry.java) representing Value Objects.
[AccountId](src/main/java/com/revolut/transfers/account/domain/AccountId.java) is another Value Object.
Therefore, the buisness constraint that it is impossible to make deduction from the account which balance will result below the zero is easily achievable. 
The standard optimazation was made that the overall balance is kept in the Account entity (as a sum of all entries).
The id of the account is intended to be provided by the implementation of the [AccountRepository](src/main/java/com/revolut/transfers/account/domain/AccountReposiotry.java)
The deposit and withdraw are factory methods that generate the Entries inside the account and keep the buisness constraint.
```
class Account {
  Account(AccountId id, CurrencyUnit currency)
  void deposit(MonetaryAmount amount)
  void withdraw(MonetaryAmount amount)
}
```

To enable transfers between accounts there is a [TransferService](src/main/java/com/revolut/transfers/account/domain/TransferService.java) which for simplicity allows for operation on the Account entity as well (but hides the low level witdraw and deposit operations).

```
interface TransferService {
    void transfer(AccountId from, AccountId to, MonetaryAmount amount);

    Account createAccount(CurrencyUnit accountCurrencyUnit);

    Account createAccountWithInitialBalance(CurrencyUnit accountCurrencyUnit, BigDecimal initialBalance);

    Account retriveAccount(AccountId accountId);

    void deleteAccount(AccountId accountId);
}
```

There are two implementations of this interface one in the infrastructure package parallel to domain and another in the domain package.
The one in domain, [TransferServiceImpl](src/main/java/com/revolut/transfers/account/domain/TransferServiceImpl.java) provides all buisness operations as expected, and the one in the infrastructure [TransactionalTransferService](src/main/java/com/revolut/transfers/account/infrastucture/TransactionalTransferService.java) is a decorator that enables transaction suppor on all the actions (it is less readable than Spring's @Transactional but effect is the same seperation buisness logic in POJO from ugly java code to support required stuff).

The rest api is provided in a declarative way with [Spark](http://sparkjava.com/): it makes testing more difficult (however, the rest controller is separated and easily tested with Stubs and Mocks), but the api is quite readable and explicit. 

The data store access is hidden by the Collection like interface AccountRepository.
The data store is provided by JPA/Hibernate and H2 in-memory database. I do not consider Hibernate lightweight nor the best one but it is quite a standard that is why it is here. 
The Hibernate, unfortunatelly, requires to provide some additional setters to otherwise immutable Entry (bug in the version 5.0, reportedly fixed in 5.2), to provide default constructors (which generate objects in weird state), and adds a lot of annotations that uglifies the the code. The first two points are remedied by making methods non public (package access required by JPA, private by Hibernate) but I do not cosider it clean...

Due to use of Money API some Gson converters has to be added to the project. 


## Simplifications ##
<a name="simplifications"/>

There are few simplifications made which can be categorised as buisness logic simplifications and engineering ones.
  * The transfers are required to be only between local accounts (no external transfers are allowed to e.g. other banks). It could be easily implemented as withdraw and deposit are possible on a single account.
  * The transfers are required to be between accounts with no convertion between currencies (exception thrown otherwise). It could be again quite easily implemented using moneta api of convertion or better requesting external service/context dealing with convertion rates. In that case convertionn withdraw can be applied to the account together with another withdraw due to additional fee. 
  * The logging is limited only to the stuff made by the libraries and a single log without any siginificat information when the request comes to the server.
  * There is no additional data in the Entry nor in the Account (like e.g. userid, description and so one, but it can be added)
  * There is no locking facility (like e.g. optimistic lock versioning, but it can be added to the Account entity in the future as it is supported by the Hibernate).
  
## Dependencies ##
<a name="dependencies"/>

All dependencies are listed in the gradle.build file but below is the curated list:
  * spark (rest api)
  * gson (json)
  * moneta (currency)
  * spock (unit tests and integration tests, mocking api)
  * gatling (load testing)
  * hibernate (data store)
  * btm (transactions)
  
  
  
