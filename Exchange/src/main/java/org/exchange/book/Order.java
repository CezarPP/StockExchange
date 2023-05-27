package org.exchange.book;

import org.common.symbols.Symbol;

public record Order(int id, Symbol symbol, float price, int quantity, Side side, boolean isCancel) {
}
