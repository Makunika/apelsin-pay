package ru.pshiblo.transaction.rabbit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitConsts {
    public final String MAIN_EXCHANGE = "exchange-example-1";

    public final String CLOSE_QUEUE = "close_transaction";
    public final String OPEN_QUEUE = "open_transaction";
    public final String CANCEL_QUEUE = "cancel_transaction";
    public final String COMMISSION_QUEUE = "commission_transaction";
    public final String CHECK_QUEUE = "check_transaction";
    public final String SEND_QUEUE = "send_transaction";
    public final String ERROR_QUEUE = "error_transaction";
    public final String LOG_QUEUE = "log_transaction";

    public final String CLOSE_ROUTE = "close_transaction_route";
    public final String OPEN_ROUTE = "open_transaction_route";
    public final String SEND_ROUTE = "send_transaction_route";
    public final String CANCEL_ROUTE = "cancel_transaction_route";
    public final String COMMISSION_ROUTE = "commission_transaction_route";
    public final String CHECK_ROUTE = "check_transaction_route";
    public final String ERROR_ROUTE = "error_transaction_route";
}
