# Stock Exchange

University project about building a functioning stock exchange.

The system design is inspired from this Jane Street [video](https://www.youtube.com/watch?v=b1e4t2k2KJY&t=22s).

Planned features:

* [ ] Limit orders (BUY, SELL, CANCEL)
* [x] Matching Engine
* [x] Limit Order Book
* [x] Client Ports Processes
* [x] Client GUI (Swing)
* [x] Very simple fix protocol implementation
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
