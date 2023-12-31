package org.container.terraman.api.common.service;

import org.container.terraman.api.common.model.AccountModel;
import org.container.terraman.api.common.repository.AccountRepository;
import org.container.terraman.api.common.util.CommonUtils;
import org.container.terraman.api.exception.ContainerTerramanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;


    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Select Account Info
     *
     * @param id the id
     * @return the AccountModel
     */
    public AccountModel getAccountInfo(int id) {
        AccountModel accountModel = null;
        try {
            accountModel = new AccountModel();
            accountModel = accountRepository.findById(id);
        } catch (Exception e) {
            throw new ContainerTerramanException(e.getMessage());
        }
        return accountModel;
    }
}
