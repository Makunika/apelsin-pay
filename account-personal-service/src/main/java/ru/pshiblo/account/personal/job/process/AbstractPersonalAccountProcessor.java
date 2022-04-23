package ru.pshiblo.account.personal.job.process;

import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;

public abstract class AbstractPersonalAccountProcessor implements PersonalAccountProcessor {

    protected abstract boolean hasSupportType(PersonalAccountType type);
    protected abstract void processAccount(PersonalAccount deposit);

    @Override
    public void process(PersonalAccount account) {
        if (hasSupportType(account.getType()) && account.getIsEnabled()) {
            processAccount(account);
        }
    }
}
