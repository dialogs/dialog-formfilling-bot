package im.dlg.botsdk.formfillingbot;

import im.dlg.botsdk.domain.User;
import im.dlg.botsdk.domain.interactive.InteractiveAction;
import im.dlg.botsdk.domain.interactive.InteractiveGroup;
import im.dlg.botsdk.domain.interactive.InteractiveSelect;
import im.dlg.botsdk.domain.interactive.InteractiveSelectOption;

import java.util.*;

public class Form {
    private String callMessage;
    private String completeMessage;
    private String failMessage;
    private String waitMessage;
    private List<Input> inputList;
    private int maxStep;

    public Form(String callMessage, String completeMessage, String failMessage, String waitMessage, Input... inputs) {
        this.callMessage = callMessage;
        this.completeMessage = completeMessage;
        this.failMessage = failMessage;
        this.waitMessage = waitMessage;
        inputList = new ArrayList<>();

        for (Input input : inputs) {
            inputList.add(input);
        }

        this.maxStep = inputList.size();
    }

    public Input getCurrentInput(int step) {
        return inputList.get(step);
    }

    public Input getPrevInput(int step) {
        return inputList.get(step - 1);
    }

    public int getMaxStep() {
        return maxStep;
    }

    public boolean isComplete(int step) {
        if (step == maxStep) {
            return true;
        }
        return false;
    }

    public String getCompleteMessage() {
        return completeMessage;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public String getCallMessage() {
        return callMessage;
    }

    public String getWaitMessage() {
        return waitMessage;
    }

    public static Form mapToForm(Map<String, Object> map) {

        ArrayList<Object> survey = (ArrayList)map.get("survey");
        Iterator<Object> iterator = survey.iterator();
        Input[] inputs = new Input[survey.size()];

        Integer idx = 0;
        while(iterator.hasNext()) {
            Map<String, Object> object = (Map<String, Object>) iterator.next();
            Input input = null;

            if ("text".equals(object.get("type"))) {
                input = new Input((String) object.get("title"));
            }
            else if ("select".equals(object.get("type"))) {
                List<InteractiveSelectOption> selectOptions = new ArrayList<>();
                ArrayList<Object> options = (ArrayList<Object>) object.get("options");

                for (Object option : options) {
                    selectOptions.add(new InteractiveSelectOption(option.toString(), option.toString()));
                }

                ArrayList<InteractiveAction> actions = new ArrayList<>();
                InteractiveSelect interactiveSelect = new InteractiveSelect("Choose", "Choose", selectOptions);
                actions.add(new InteractiveAction(idx.toString(), interactiveSelect));
                InteractiveGroup secondInteractiveGroup = new InteractiveGroup((String) object.get("title"), (String) object.getOrDefault("description", ""), actions);

                input = new Input(secondInteractiveGroup, idx.toString());
            }
            else if ("date".equals(object.get("type"))) {
                input = new Input(Input.INPUT_DATE, (String) object.get("format"));
            }
            else if ("username".equals(object.get("type"))) {
                input = new Input(Input.INPUT_USERNAME);
            }

            inputs[idx] = input;
            idx++;
        }

        Form result = new Form((String) map.get("call_message"), (String) map.get("complete_message"), (String) map.get("fail_message"), (String) map.get("wait_message"), inputs);

        return result;
    }

    public static int inputDateNUser(Form userForm, int step, Answer answer, User user) {
        try {
            while(!userForm.isComplete(step) && (userForm.getCurrentInput(step).getType() == Input.INPUT_DATE
                    || userForm.getCurrentInput(step).getType() == Input.INPUT_USERNAME)) {

                Input input = userForm.getCurrentInput(step);

                if (input.getType() == Input.INPUT_DATE) {
                    answer.setAnswer(input.getSimpleDateFormat().format(new Date()));
                }
                else if (input.getType() == Input.INPUT_USERNAME) {
                    answer.setAnswer(user.getName());
                }
                else {
                    answer.setAnswer("");
                }

                step = answer.nextStep();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return step;
    }
}
