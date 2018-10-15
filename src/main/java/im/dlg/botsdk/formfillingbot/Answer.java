package im.dlg.botsdk.formfillingbot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Answer {
    private List<Object> answerList;
    private String user;
    private int step;

    public Answer(String user) {
        this.user = user;
        this.step = 0;
        this.answerList = new LinkedList<>();
    }

    public int nextStep() {
        step = step + 1;
        return step;
    }

    public int getStep() {
        return step;
    }

    public List<Object> getAnswerList() {
        List<Object> result = new ArrayList<>();
        result.addAll(answerList);
        return result;
    }

    public void setAnswer(Object object) {
        answerList.add(object);
    }
}
