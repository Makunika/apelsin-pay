package ru.pshiblo.account.personal.job.process;

import ru.pshiblo.account.personal.core.domain.PersonalAccount;

public interface PersonalAccountProcessor {
    void process(PersonalAccount account);
}
