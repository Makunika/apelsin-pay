package ru.pshiblo.card.rabbit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RabbitConsts {

    public final String MAIN_EXCHANGE = "exchange-example-1";

    public final String CARD_CHECK_TO_Q = "check_to_transaction_q_card";
    public final String CARD_CHECK_FROM_Q = "check_from_transaction_q_card";
    public final String CARD_AFTER_SEND_Q = "card_after_send_q";

    public final String CARD_AFTER_SEND_ROUTE = "card_after_send_route";
    public final String SEND_ROUTE = "send_transaction_route";
    public final String CANCEL_ROUTE = "cancel_transaction_route";
    public final String CARD_TO_CHECK_ROUTE = "card_to_check_route";
    public final String CARD_FROM_CHECK_ROUTE = "card_from_check_route";
    public final String ADD_PAYMENT_ROUTE = "ADD_PAYMENT_route";

}
