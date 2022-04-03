package ru.pshiblo.deposit.job.process;

import ru.pshiblo.deposit.core.domain.PersonalAccount;

public interface PersonalAccountProcessor {
    void process(PersonalAccount account);
}
