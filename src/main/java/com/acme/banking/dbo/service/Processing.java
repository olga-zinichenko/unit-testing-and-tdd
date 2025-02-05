package com.acme.banking.dbo.service;

import com.acme.banking.dbo.domain.Account;
import com.acme.banking.dbo.domain.Cash;
import com.acme.banking.dbo.domain.Client;
import com.acme.banking.dbo.repository.AccountRepository;
import com.acme.banking.dbo.repository.ClientRepository;

import java.util.Collection;
import java.util.Objects;

public class Processing {

    private final ClientRepository clients;
    private final AccountRepository accounts;
    private final Cash cash;

    public Processing(ClientRepository clients, AccountRepository accounts, Cash cash) {
        this.clients = clients;
        this.accounts = accounts;
        this.cash = cash;
    }

    public Client createClient(String name) {
        Client client = new Client(name);
        return clients.save(client);
    }

    public Collection<Account> getAccountsByClientId(int clientId) {
        Client client = Objects.requireNonNull(
                clients.findById(clientId),
                String.format("Client by id=%s not found", clientId)
        );
        Collection<Account> accounts = client.getAccounts();
        if (accounts.isEmpty()) {
            throw new IllegalStateException("Client has not accounts");
        }
        return accounts;
    }

    public void transfer(int fromAccountId, int toAccountId, double amount) {
        Account from = Objects.requireNonNull(accounts.findById(fromAccountId), String.format("Account by id=%s not found", fromAccountId));
        Account to = Objects.requireNonNull(accounts.findById(toAccountId), String.format("Account by id=%s not found", toAccountId));

        from.withdraw(amount);
        to.deposit(amount);

        accounts.save(from);
        accounts.save(to);
    }

    public void cash(double amount, int fromAccountId) {
        cash.log(amount, fromAccountId);
    }
}
