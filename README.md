# Stock Exchange

University project about building a functioning stock exchange.

The system design is inspired from this Jane Street [video](https://www.youtube.com/watch?v=b1e4t2k2KJY&t=22s).

Planned exchange features:

* [x] Limit orders (BUY, SELL, CANCEL)
* [x] Limit Order Book
* [x] Matching Engine

Planned communication features:

* [x] Simple fix protocol implementation
* [x] Broadcast with order executions, rejects, etc.
* [x] Market data snapshots
* [x] Client Port Processes
* [x] Retransmitter for broadcasts

Other features:

* [x] Client GUI (Swing)
* [ ] Database for logging

For the Limit Order Book I want to try the following data structures:

* [x] Java TreeMap (Red-Black Tree)
* [ ] Adaptive Radix Tree (Trie)
* [ ] Treaps
* [ ] Others ?

Module structure of the project:

* exchange - Limit order book and matching engine
* client - Classes related to client processes
* common - Common classes -> fix protocol, symbols
