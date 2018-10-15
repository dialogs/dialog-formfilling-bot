package im.dlg.botsdk.formfillingbot;

import com.google.gson.Gson;
import im.dlg.botsdk.Bot;
import im.dlg.botsdk.domain.Peer;
import im.dlg.botsdk.domain.User;
import im.dlg.botsdk.domain.interactive.InteractiveAction;
import im.dlg.botsdk.domain.interactive.InteractiveGroup;
import im.dlg.botsdk.domain.interactive.InteractiveSelect;
import im.dlg.botsdk.domain.interactive.InteractiveSelectOption;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class FormFillingBot {

    private static ConcurrentHashMap<Integer, Answer> answerMap = new ConcurrentHashMap<>();
    private static Map<String,Object> config = new HashMap<>();

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, ExecutionException {
        if (args.length != 1) {
            throw new FileNotFoundException("config.json");
        }

        Gson gson = new Gson();
        config = (Map<String,Object>) gson.fromJson(new FileReader(args[0]), config.getClass());
        Form userForm = Form.mapToForm((Map) config.get("form"));

        Bot bot = Bot.start((String)config.get("bot_token")).get();

        bot.messaging().onMessage(message ->
            bot.users().get(message.getSender()).thenAccept(userOpt -> userOpt.ifPresent(user -> {
                Peer sender = message.getSender();
                int senderId = sender.getId();

                if (userForm.getCallMessage().equals(message.getText())) {
                    Answer answer = new Answer(user.getName());
                    answerMap.put(senderId, answer);
                }

                if (answerMap.containsKey(senderId)) {
                    Answer answer = answerMap.get(senderId);
                    int step = answer.getStep();

                    if (answer.getStep() > 0 && userForm.getPrevInput(step).getType() == Input.INPUT_STRING) {
                        answer.setAnswer(message.getText());
                    }

                    if (userForm.isComplete(step)) {
                        bot.messaging().send(sender, userForm.getWaitMessage());

                        try {
                            GoogleSheet.updateSpreadSheet(answer.getAnswerList(), (String)config.get("google_auth"), (String)config.get("spreadsheet_id"),  (String)config.get("spreadsheet_name"), (String)config.get("spreadsheet_range"));
                            bot.messaging().send(sender, userForm.getCompleteMessage());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            bot.messaging().send(sender, userForm.getFailMessage());
                        }

                        answerMap.remove(senderId);
                    }
                    else {
                        step = Form.inputDateNUser(userForm, step, answer, user);

                        if (userForm.isComplete(step)) {
                            bot.messaging().send(sender, userForm.getWaitMessage());

                            try {
                                GoogleSheet.updateSpreadSheet(answer.getAnswerList(), (String)config.get("google_auth"), (String)config.get("spreadsheet_id"),  (String)config.get("spreadsheet_name"), (String)config.get("spreadsheet_range"));
                                bot.messaging().send(sender, userForm.getCompleteMessage());
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                bot.messaging().send(sender, userForm.getFailMessage());
                            }

                            answerMap.remove(senderId);
                        }
                        else {
                            userForm.getCurrentInput(step).sendOrder(bot, sender);
                            answer.nextStep();
                        }
                    }
                }
            }))
        );

        bot.interactiveApi().onEvent(event ->
            bot.users().get(event.getPeer()).thenAccept(userOpt -> userOpt.ifPresent(user -> {
                Peer sender = event.getPeer();
                int senderId = sender.getId();

                if (answerMap.containsKey(senderId)) {
                    Answer answer = answerMap.get(senderId);
                    int step = answer.getStep();

                    if (userForm.getPrevInput(step).getType() == Input.INPUT_INTERACTIVE && userForm.getPrevInput(step).getActionName().equals(event.getId())) {
                        answer.setAnswer(event.getValue());
                    }

                    if (userForm.isComplete(step)) {
                        bot.messaging().send(sender, userForm.getWaitMessage());

                        try {
                            GoogleSheet.updateSpreadSheet(answer.getAnswerList(), (String)config.get("google_auth"), (String)config.get("spreadsheet_id"),  (String)config.get("spreadsheet_name"), (String)config.get("spreadsheet_range"));
                            bot.messaging().send(sender, userForm.getCompleteMessage());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            bot.messaging().send(sender, userForm.getFailMessage());
                        }
                        answerMap.remove(senderId);
                    }
                    else {
                        step = Form.inputDateNUser(userForm, step, answer, user);

                        if (userForm.isComplete(step)) {
                            bot.messaging().send(sender, userForm.getWaitMessage());

                            try {
                                GoogleSheet.updateSpreadSheet(answer.getAnswerList(), (String)config.get("google_auth"), (String)config.get("spreadsheet_id"),  (String)config.get("spreadsheet_name"), (String)config.get("spreadsheet_range"));
                                bot.messaging().send(sender, userForm.getCompleteMessage());
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                bot.messaging().send(sender, userForm.getFailMessage());
                            }
                            answerMap.remove(senderId);
                        }
                        else {
                            userForm.getCurrentInput(step).sendOrder(bot, sender);
                            answer.nextStep();
                        }
                    }
                }
            }))
        );

        bot.await();
    }
}
