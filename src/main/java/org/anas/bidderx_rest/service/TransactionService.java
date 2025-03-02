package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.Transaction;

public interface TransactionService {
    Transaction save(Transaction transaction);
}
