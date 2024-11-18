package ti4.service;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import ti4.message.MessageHelper;
import ti4.users.UserSettings;
import ti4.users.UserSettingsManager;

@UtilityClass
public class PingIntervalService {

    public static void set(GenericInteractionCreateEvent event, UserSettings settings, int pingInterval) {
        if (pingInterval < 0) {
            pingInterval = 0;
        }
        settings.setPersonalPingInterval(pingInterval);

        if (event == null) {
            return;
        }
        String message = "Set Personal Ping Interval to: `" + pingInterval + "`";
        MessageHelper.sendMessageToEventChannel(event, message);
    }

    public static void set(GenericInteractionCreateEvent event, int pingInterval) {
        UserSettings userSettings = UserSettingsManager.get(event.getUser().getId());
        set(event, userSettings, pingInterval);
    }
}