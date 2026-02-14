
# Context

Our software is a record-keeping system for a retirement plan. Plan members make contributions to the plan to save for
retirement, and those contributions are invested into their selection of investment vehicle (e.g. index fund).

We use a third-party bank ("custodian bank") to hold plan members' cash and investments. 
The custodian bank maintains a single account for the plan, in which the cash and investments of all of the plan
members are held.  
When a member makes a contribution, the custodian bank sends us a confirmation of the contribution.
When we invest members' contributions, the custodian bank sends us a confirmation when the trade settles. 
Daily, we receive a flat text file that contains confirmations of these and other transactions.  
You can see an example of such a file at `src/test/resources/transactions.txt`, and the fields are further explained below.
Transaction types may appear in the file in any order.

The processing of these transactions is critical for the system to function correctly and to maintain trust with 
the members. If the system does not accurately reflect the state of the account, money could be lost. 

In this project, your colleague has implemented processing of contribution transactions. 
Processing includes reading the contribution transactions from the file and calling a remote service over HTTP 
to record the contribution.

Unfortunately, there is a lot of room for improvement in your colleague's code. 
It cannot be considered production-ready given the criticality of the processing to the system; there are 
problems with error handling and observability as well as a lack of test coverage.  


# Problem

Your task is to add processing of trade settlement transactions to this project. 
Like with contributions, trade settlements are processed by reading the lines from the file and 
calling a remote service over HTTP. 
The skeleton in TradeSettlement indicates what fields the remote service requires, and there is 
a skeleton method in ReconciliationService to perform the remote call. 
Please note there is no need to implement the remote service; assume it already exists and cannot be changed.

While adding the new processing, please also refactor and change the existing code to bring it to a standard 
you would feel comfortable to deploy in a mission-critical system. If there are improvements you would like to 
do that you don't have time to complete (or that are outside the scope of this project), or if there are questions 
 or assumptions that affect your choices, please describe those in the file `TODO.md`.
 
  
# Field explanation

| Field                   | Size                         | Description                                                                                                                             |
|-------------------------|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| ACCT                    | 6                            | Common Wealth's bank account number                                                                                                     |
| ACCT NM                 | <=40                         | Common Wealth's bank account name                                                                                                       |
| ENTERED                 | 9                            | Entered date                                                                                                                            |
| POSTED                  | 9                            | Posted date                                                                                                                             |
| DATE SETTLED            | 9                            | Settlement date                                                                                                                         |
| DATE TRADED             | 9                            | Trade date                                                                                                                              |
| TRANSACTION DESCRIPTION | <=40                         | One of a list of transaction types, see below                                                                                           |
| QUANTITY                | <=15, 4 fixed decimal places | Units purchased (positive) / sold (negative)                                                                                            |
| RATE                    | <=14, <=6 decimal places     | Unit price                                                                                                                              |
| DESCR                   | <=40                         | For contributions: a transaction ID we supplied to the custodian and that we use for reconciliation; for settlements: name of the unit  |
| AMOUNT                  | <=13, 2 fixed decimal places | Transaction amount                                                                                                                      |
| SYMBOL-CUSIP            | <=12                         | CUSIP, ticker or bank number                                                                                                            |
| EXPLANATION             | <=840                        | For contributions: a member account number that we supplied to the custodian and that we use for reconciliation; for settlements: blank |
| JOB                     | <=123                        | Custodian bank's reference                                                                                                              |


## Transaction types explanation

| Transaction Type         | Description                    |
|--------------------------|--------------------------------|
| Purchase Cash Settlement | Purchase of investment units.  |
| Sale Cash Settlement     | Sale of investment units.      |
| Contribution             | Cash deposit into the account. |
