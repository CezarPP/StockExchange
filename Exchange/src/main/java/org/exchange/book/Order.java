package org.exchange.book;

import org.common.symbols.Symbol;

public record Order(int id, Symbol symbol, int price, int quantity, Side side) {
}
