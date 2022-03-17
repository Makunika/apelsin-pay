package ru.pshiblo.transaction.rabbit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitConsts {
    public final String MAIN_EXCHANGE = "exchange-example-1";

    public final String CLOSE_QUEUE = "close_transaction";
    public final String OPEN_QUEUE = "open_transaction";
    public final String CANCEL_QUEUE = "cancel_transaction";
    public final String COMMISSION_QUEUE = "commission_transaction";
    public final String SEND_QUEUE = "send_transaction";
    public final String ERROR_QUEUE = "error_transaction";
    public final String LOG_QUEUE = "log_transaction";
    public final String CHECK_FROM_QUEUE = "check_from_transaction";
    public final String CHECK_TO_QUEUE = "check_to_transaction";
    public final String APPLY_PAYMENTS_QUEUE = "APPLY_PAYMENTS_transaction";
    public final String ADD_PAYMENT_QUEUE = "ADD_PAYMENT_transaction";

    public final String OPEN_SYSTEM_QUEUE = "open_system_transaction";
    public final String SEND_SYSTEM_QUEUE = "send_system_transaction";


    public final String CLOSE_ROUTE = "close_transaction_route";
    public final String OPEN_ROUTE = "open_transaction_route";
    public final String SEND_ROUTE = "send_transaction_route";
    public final String CANCEL_ROUTE = "cancel_transaction_route";
    public final String CHECK_FROM_ROUTE = "check_from_route";
    public final String CHECK_TO_ROUTE = "check_to_route";
    public final String COMMISSION_ROUTE = "commission_transaction_route";
    public final String ERROR_ROUTE = "error_transaction_route";
    public final String APPLY_PAYMENTS_ROUTE = "APPLY_PAYMENTS_route";
    public final String ADD_PAYMENT_ROUTE = "ADD_PAYMENT_route";


    public final String DEPOSIT_AFTER_SEND_ROUTE = "deposit_after_send_route";
    public final String CARD_AFTER_SEND_ROUTE = "card_after_send_route";

    public final String DEPOSIT_FROM_CHECK_ROUTE = "deposit_from_check_route";
    public final String CARD_FROM_CHECK_ROUTE = "card_from_check_route";
    public final String DEPOSIT_TO_CHECK_ROUTE = "deposit_to_check_route";
    public final String CARD_TO_CHECK_ROUTE = "card_to_check_route";

    public final String OPEN_SYSTEM_ROUTE = "open_system_transaction_route";
    public final String SEND_SYSTEM_ROUTE = "send_system_transaction_route";


    public final String[] ALL_ROUTES = {
            CLOSE_ROUTE,
            OPEN_ROUTE,
            CANCEL_ROUTE,
            COMMISSION_ROUTE,
            DEPOSIT_FROM_CHECK_ROUTE,
            ADD_PAYMENT_ROUTE,
            DEPOSIT_TO_CHECK_ROUTE,
            CARD_FROM_CHECK_ROUTE,
            CARD_TO_CHECK_ROUTE,
            SEND_ROUTE,
            ERROR_ROUTE,
            OPEN_SYSTEM_ROUTE,
            SEND_SYSTEM_ROUTE
    };
}
