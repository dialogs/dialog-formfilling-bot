package im.dlg.botsdk.formfillingbot;

import im.dlg.botsdk.Bot;
import im.dlg.botsdk.domain.interactive.InteractiveAction;
import im.dlg.botsdk.domain.interactive.InteractiveGroup;
import im.dlg.botsdk.domain.interactive.InteractiveSelect;
import im.dlg.botsdk.domain.interactive.InteractiveSelectOption;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class FormfillingBot {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        final String OrderStartMessage = "Hey";
        ConcurrentHashMap<Integer, OrderList> orderListMap = new ConcurrentHashMap<>();

        Bot bot = Bot.start("df8c6600c2a45b711e097fd9e5e7f3cfee025e41").get();

        bot.messaging().onMessage(message ->
            bot.users().get(message.getSender()).thenAccept(userOpt -> userOpt.ifPresent(user -> {
                int senderId = message.getSender().getId();

                if (OrderStartMessage.equals(message.getText())) {
                    orderListMap.put(senderId, createOrderForm(user.getName()));
                }

                if (orderListMap.containsKey(senderId)) {
                    OrderList orderList = orderListMap.get(senderId);

                    if (orderList.getStep() > 0 && orderList.getPrevOrder().getType() == Order.ORDER_INPUT) {
                        orderList.setOrderAnswer(message.getText());
                    }

                    if (orderList.isComplete()) {
                        bot.messaging().send(message.getSender(), orderList.getCompleteMessage());

                        try {
                            GoogleSheet.updateSpreadSheet(orderListMap.get(senderId).getSheetData());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        orderListMap.remove(senderId);
                    }
                    else {
                        orderList.getCurrentOrder().sendOrder(bot, message.getSender());
                        orderList.nextStep();
                    }
                }
            }))
        );

        bot.interactiveApi().onEvent(event ->
            bot.users().get(event.getPeer()).thenAccept(userOpt -> userOpt.ifPresent(user -> {
                int senderId = event.getPeer().getId();

                System.out.println(event.getId());
                if (orderListMap.containsKey(senderId)) {
                    OrderList orderList = orderListMap.get(senderId);

                    if (orderList.getPrevOrder().getType() == Order.ORDER_INTERACTIVE && orderList.getPrevOrder().getActionName().equals(event.getId())) {
                        orderList.setOrderAnswer(event.getValue());
                    }

                    if (orderList.isComplete()) {
                        bot.messaging().send(event.getPeer(), orderList.getCompleteMessage());

                        try {
                            GoogleSheet.updateSpreadSheet(orderListMap.get(senderId).getSheetData());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        orderListMap.remove(senderId);
                    }
                    else {
                        orderList.getCurrentOrder().sendOrder(bot, event.getPeer());
                        orderList.nextStep();
                    }
                }
            }))
        );

        bot.await();
    }

    public static OrderList createOrderForm(String user) {
        Order first = new Order("Hi! Send me what you need in the office and I will create the purchase request.");

        List<InteractiveSelectOption> secondSelectOptions = new ArrayList<>();
        secondSelectOptions.add(new InteractiveSelectOption("Food", "Food"));
        secondSelectOptions.add(new InteractiveSelectOption("Not Food", "Not Food"));

        ArrayList<InteractiveAction> secondActions = new ArrayList<>();
        InteractiveSelect secondInteractiveSelect = new InteractiveSelect("Choose", "Choose", secondSelectOptions);
        secondActions.add(new InteractiveAction("action_1", secondInteractiveSelect));
        InteractiveGroup secondInteractiveGroup = new InteractiveGroup("Is it food or not food?", "", secondActions);

        Order second = new Order(secondInteractiveGroup, "action_1");

        List<InteractiveSelectOption> thirdSelectOptions = new ArrayList<>();
        thirdSelectOptions.add(new InteractiveSelectOption("DF", "DF"));
        thirdSelectOptions.add(new InteractiveSelectOption("Pok", "Pok"));
        thirdSelectOptions.add(new InteractiveSelectOption("Nov", "Nov"));
        thirdSelectOptions.add(new InteractiveSelectOption("Znam", "Znam"));

        ArrayList<InteractiveAction> thirdActions = new ArrayList<>();
        InteractiveSelect thirdInteractiveSelect = new InteractiveSelect("Choose", "Choose", thirdSelectOptions);
        thirdActions.add(new InteractiveAction("action_2", thirdInteractiveSelect));
        InteractiveGroup thirdInteractiveGroup = new InteractiveGroup("In which office is this order?", "", thirdActions);

        Order third = new Order(thirdInteractiveGroup, "action_2");

        OrderList order = new OrderList(user, first, second, third);
        order.setCompleteMessage("Thank you, your order is placed and will be purchased soon.\nThe office administrator will contact you as soon as the order arrives at the office.");

        return order;
    }
}
