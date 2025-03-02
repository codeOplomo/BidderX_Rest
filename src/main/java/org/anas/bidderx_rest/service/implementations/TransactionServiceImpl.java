package org.anas.bidderx_rest.service.implementations;

import org.anas.bidderx_rest.domain.Transaction;
import org.anas.bidderx_rest.repository.TransactionRepository;
import org.anas.bidderx_rest.service.TransactionService;
import org.springframework.stereotype.Service;


@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
