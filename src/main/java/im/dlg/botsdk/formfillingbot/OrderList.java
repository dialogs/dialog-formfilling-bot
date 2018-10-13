package im.dlg.botsdk.formfillingbot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderList {
    private String user;
    private List<Order> orderList;
    private List<Object> orderAnswerList;
    private String completeMessage;
    private int step;
    private int maxStep;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public OrderList(String user, Order ... orders) {
        this.user = user;
        orderList = new ArrayList<>();
        orderAnswerList = new ArrayList<>();

        for (Order order : orders) {
            orderList.add(order);
        }

        this.step = 0;
        this.maxStep = orderList.size();
    }

    public List<Order> orderList() {
        return orderList;
    }

    public void setOrderAnswer(String answer) {
        if (step > 0) {
            orderAnswerList.add(answer);
        }
    }

    public List<Object> getSheetData() {
        List<Object> result = new ArrayList<>();

        result.addAll(orderAnswerList);
        result.add(user);
        result.add(dateFormat.format(new Date()));

        return result;
    }

    public Order getCurrentOrder() {
        return orderList.get(step);
    }

    public Order getPrevOrder() {
        return orderList.get(step - 1);
    }

    public void nextStep() {
        step++;
    }

    public int getStep() {
        return step;
    }

    public boolean isComplete() {
        if (step == maxStep) {
            return true;
        }
        return false;
    }

    public void setCompleteMessage(String completeMessage) {
        this.completeMessage = completeMessage;
    }

    public String getCompleteMessage() {
        return completeMessage;
    }
}
