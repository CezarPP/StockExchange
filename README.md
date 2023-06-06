# Stock Exchange

University project about building a functioning stock exchange.

The system design is inspired by this Jane Street [video](https://www.youtube.com/watch?v=b1e4t2k2KJY&t=22s).

Planned exchange features:

* [x] Limit orders (BUY, SELL, CANCEL)
* [x] Limit Order Book
* [x] Matching Engine

Planned communication features:

* [x] Simple fix protocol implementation
* [x] Multicast with order executions, rejects, etc.
* [x] Market data snapshots
* [x] Client Port Processes
* [x] Retransmitter for multicast

Other features:

* [x] Client GUI (Swing)
* [x] MongoDB for logging

For the Limit Order Book, I want to try the following data structures:

* [x] Java TreeMap (Red-Black Tree)
* [x] [ART](https://db.in.tum.de/~leis/papers/ART.pdf) (Adaptive Radix Tree)
* [x] Treap

Module structure of the project:

* exchange - Limit order book and matching engine
* client - Classes related to client processes
* common - Common classes -> fix protocol, symbols