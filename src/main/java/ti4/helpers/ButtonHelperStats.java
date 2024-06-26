package ti4.helpers;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import ti4.map.Game;
import ti4.map.Player;
import ti4.message.MessageHelper;

public class ButtonHelperStats {

    public static void convertComms(ButtonInteractionEvent event, Game game, Player player, int amt) {
        String message, ident = player.getRepresentation();
        if (player.getCommodities() >= amt) {
            player.setCommodities(player.getCommodities() - amt);
            player.setTg(player.getTg() + amt);
            message = "Converted " + amt + " Commodities to " + amt + " tg";
        } else {
            player.setTg(player.getTg() + player.getCommodities());
            player.setCommodities(0);
            message = "Converted all remaining commodities (less than " + amt + ") into tg";
        }
        if (game.isFoWMode()) FoWHelper.pingAllPlayersWithFullStats(game, event, player, message);

        if (event.getMessage().getContentRaw().toLowerCase().contains("space station")) {
            ButtonHelper.deleteMessage(event);
            message += " using their space station";
        }

        ButtonHelper.fullCommanderUnlockCheck(player, game, "hacan", event);
        MessageHelper.sendMessageToChannel(player.getCorrectChannel(), ident + " " + message);
        if (event.getMessage().getContentRaw().contains("explore")) {
            ButtonHelper.deleteMessage(event);
        }
    }

    public static void gainComms(GenericInteractionCreateEvent event, Game game, Player player, int amt, boolean deleteMsg) {
        gainComms(event, game, player, amt, deleteMsg, false);
    }

    public static void gainComms(GenericInteractionCreateEvent event, Game game, Player player, int amt, boolean deleteMsg, boolean skipOutput) {
        String message, ident = player.getFactionEmojiOrColor();
        int initComm = player.getCommodities();
        if (player.getCommodities() + amt >= player.getCommoditiesTotal()) {
            player.setCommodities(player.getCommoditiesTotal());
            int gained = player.getCommodities() - initComm;
            message = "Gained " + gained + " Commodities (comms are now at max)";
        } else {
            player.setCommodities(player.getCommodities() + amt);
            message = "Gained " + amt + " Commodities (" + initComm + "->" + player.getCommodities() + ")";
        }
        int finalComm = player.getCommodities();

        if (!skipOutput) MessageHelper.sendMessageToChannel(player.getCorrectChannel(), ident + " " + message);
        if (game.isFoWMode()) FoWHelper.pingAllPlayersWithFullStats(game, event, player, message);

        if (deleteMsg) ButtonHelper.deleteMessage(event);
        afterGainCommsChecks(game, player, finalComm - initComm);
    }

    public static void replenishComms(GenericInteractionCreateEvent event, Game game, Player player, boolean skipOutput) {
        String message, ident = player.getFactionEmojiOrColor();
        int initComm = player.getCommodities();
        if (player.getCommodities() < player.getCommoditiesTotal()) {
            player.setCommodities(player.getCommoditiesTotal());
            message = "Replenished commodities (" + initComm + "->" + player.getCommodities() + ")";
        } else {
            message = "Already at maximum commodities.";
        }
        int finalComm = player.getCommodities();

        if (!skipOutput) MessageHelper.sendMessageToChannel(player.getCorrectChannel(), ident + " " + message);
        if (game.isFoWMode()) FoWHelper.pingAllPlayersWithFullStats(game, event, player, message);

        afterGainCommsChecks(game, player, finalComm - initComm);
        ButtonHelper.resolveMinisterOfCommerceCheck(game, player, event);
        ButtonHelperAgents.cabalAgentInitiation(game, player);
    }

    public static void gainTGs(GenericInteractionCreateEvent event, Game game, Player player, int amt, boolean skipOutput) {
        if (amt == 0) return;

        int init = player.getTg();
        player.setTg(init + amt);

        String message = "has gained " + amt + " trade goods (" + init + "->" + player.getTg() + ")";
        if (!skipOutput) MessageHelper.sendMessageToChannel(player.getCorrectChannel(), player.getRepresentation() + " " + message);
        if (game.isFoWMode()) FoWHelper.pingAllPlayersWithFullStats(game, event, player, message);

        // After gain tg checks
        ButtonHelperAbilities.pillageCheck(player, game);
        ButtonHelperAgents.resolveArtunoCheck(player, game, 1);
    }

    public static void afterGainCommsChecks(Game game, Player player, int realGain) {
        if (player.hasAbility("military_industrial_complex") && ButtonHelperAbilities.getBuyableAxisOrders(player, game).size() > 1) {
            String axis = player.getRepresentation(true, true) + " you have the opportunity to buy axis orders";
            MessageHelper.sendMessageToChannelWithButtons(player.getCorrectChannel(), axis, ButtonHelperAbilities.getBuyableAxisOrders(player, game));
        }
        if (player.getLeaderIDs().contains("mykomentoricommander") && !player.hasLeaderUnlocked("mykomentoricommander")) {
            ButtonHelper.commanderUnlockCheck(player, game, "mykomentori", null);
        }
    }

}
