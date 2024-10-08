package ti4.commands.uncategorized;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import ti4.buttons.Buttons;
import ti4.commands.Command;
import ti4.commands.cardsac.ACInfo;
import ti4.commands.cardspn.PNInfo;
import ti4.commands.cardsso.SOInfo;
import ti4.generator.Mapper;
import ti4.helpers.ButtonHelper;
import ti4.helpers.ButtonHelperAbilities;
import ti4.helpers.Constants;
import ti4.helpers.Emojis;
import ti4.helpers.Helper;
import ti4.map.Game;
import ti4.map.GameManager;
import ti4.map.Player;
import ti4.message.BotLogger;
import ti4.message.MessageHelper;

public class CardsInfo implements Command, InfoThreadCommand {

    @Override
    public String getActionID() {
        return Constants.CARDS_INFO;
    }

    public boolean accept(SlashCommandInteractionEvent event) {
        return acceptEvent(event, getActionID());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        GameManager gameManager = GameManager.getInstance();
        Game game;
        if (!gameManager.isUserWithActiveGame(userID)) {
            MessageHelper.replyToMessage(event, "Set your active game using: /set_game gameName");
            return;
        } else {
            game = gameManager.getUserActiveGame(userID);
            String color = Helper.getColor(game, event);
            if (!Mapper.isValidColor(color)) {
                MessageHelper.replyToMessage(event, "Color/Faction not valid");
                return;
            }
        }

        Player player = game.getPlayer(userID);
        player = Helper.getGamePlayer(game, player, event, null);
        if (player == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Player could not be found");
            return;
        }
        game.checkPromissoryNotes();
        PNInfo.checkAndAddPNs(game, player);
        sendCardsInfo(game, player, event);
    }

    public static void sendCardsInfo(Game game, Player player, GenericInteractionCreateEvent event) {
        if (player == null)
            return;
        String headerText = player.getRepresentation(true, true) + CardsInfoHelper.getHeaderText(event);
        MessageHelper.sendMessageToPlayerCardsInfoThread(player, game, headerText);
        sendCardsInfo(game, player);
    }

    public static void sendCardsInfo(Game game, Player player) {
        SOInfo.sendSecretObjectiveInfo(game, player);
        ACInfo.sendActionCardInfo(game, player);
        PNInfo.sendPromissoryNoteInfo(game, player, false);
        sendVariousAdditionalButtons(game, player);
        MessageHelper.sendMessageToPlayerCardsInfoThread(player, game,
            "You may whisper to people from here by starting a message with to[color] or to[faction]." +
                "\nYou may schedule a message to yourself (delivered at start of your next turn) by starting a message with tofutureme"
                +
                "\nYou may schedule a message to others (delivered at start of their next turn) by starting a message with tofuture[color] or tofuture[faction]");

    }

    public static void sendVariousAdditionalButtons(Game game, Player player) {
        List<Button> buttons = new ArrayList<>();
        Button transaction = Buttons.blue("transaction", "Transaction");
        buttons.add(transaction);
        Button modify = Buttons.gray("getModifyTiles", "Modify Units");
        buttons.add(modify);
        if (game.playerHasLeaderUnlockedOrAlliance(player, "naalucommander")) {
            Button naalu = Buttons.gray("naaluCommander", "Do Naalu Commander")
                .withEmoji(Emoji.fromFormatted(Emojis.Naalu));
            buttons.add(naalu);
        }
        if (player.hasAbility("oracle_ai") || player.getPromissoryNotesInPlayArea().contains("dspnauge")) {
            Button augers = Buttons.gray("initialPeak", "Peek At Next Objective")
                .withEmoji(Emoji.fromFormatted(Emojis.augers));
            buttons.add(augers);
        }
        if (player.hasUnexhaustedLeader("mykomentoriagent")) {
            Button nekroButton = Buttons.gray("exhaustAgent_mykomentoriagent",
                "Use Myko-Mentori Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.mykomentori));
            buttons.add(nekroButton);
        }
        if (player.hasUnexhaustedLeader("hacanagent")) {
            Button hacanButton = Buttons.gray("exhaustAgent_hacanagent",
                "Use Hacan Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.Hacan));
            buttons.add(hacanButton);
        }
        if (ButtonHelper.isPlayerElected(game, player, "minister_peace")) {
            Button hacanButton = Buttons.gray("ministerOfPeace", "Use Minister of Peace")
                .withEmoji(Emoji.fromFormatted(Emojis.Agenda));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("vadenagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_vadenagent",
                "Use Vaden Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.vaden));
            buttons.add(hacanButton);
        } // olradinagent
        if (player.hasUnexhaustedLeader("olradinagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_olradinagent",
                "Use Olradin Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.olradin));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("edynagent")) {
            Button hacanButton2 = Buttons.gray("presetEdynAgentStep1", "Preset Edyn Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.edyn));
            buttons.add(hacanButton2);
        }
        if (player.hasUnexhaustedLeader("celdauriagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_celdauriagent",
                "Use Celdauri Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.celdauri));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("cheiranagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_cheiranagent",
                "Use Cheiran Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.cheiran));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("freesystemsagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_freesystemsagent",
                "Use Free Systems Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.freesystems));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("florzenagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_florzenagent",
                "Use Florzen Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.florzen));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("nokaragent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_nokaragent",
                "Use Nokar Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.nokar));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("zelianagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_zelianagent",
                "Use Zelian Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.zelian));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("mirvedaagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_mirvedaagent",
                "Use Mirveda Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.mirveda));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("cymiaeagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_cymiaeagent",
                "Use Cymiae Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.cymiae));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("mortheusagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_mortheusagent",
                "Use Mortheus Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.mortheus));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("zealotsagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_zealotsagent",
                "Use Rhodun Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.zealots));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("rohdhnaagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_rohdhnaagent",
                "Use Roh'Dhna Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.rohdhna));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("veldyragent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_veldyragent",
                "Use Veldyr Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.veldyr));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("gledgeagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_gledgeagent",
                "Use Gledge Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.gledge));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("khraskagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_khraskagent",
                "Use Khrask Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.khrask));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("nivynagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_nivynagent",
                "Use Nivyn Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.nivyn));
            buttons.add(hacanButton);
        }
        if (player.hasUnexhaustedLeader("ghotiagent")) {
            Button hacanButton = Buttons.gray("getAgentSelection_ghotiagent",
                "Use Ghoti Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.ghoti));
            buttons.add(hacanButton);
        }
        if (player.getNomboxTile().getUnitHolders().get("space").getUnits().size() > 0) {
            Button release = Buttons.gray("getReleaseButtons", "Release captured units")
                .withEmoji(Emoji.fromFormatted(Emojis.Cabal));
            buttons.add(release);
        }
        if (player.hasRelicReady("e6-g0_network")) {
            buttons.add(Buttons.green("exhauste6g0network", "Exhaust E6-G0 Network Relic to Draw AC"));
        }
        if (player.hasTech("pa") && ButtonHelper.getPsychoTechPlanets(game, player).size() > 1) {
            Button psycho = Buttons.green("getPsychoButtons", "Use Psychoarcheology");
            psycho = psycho.withEmoji(Emoji.fromFormatted(Emojis.BioticTech));
            buttons.add(psycho);
        }
        if (player.hasUnexhaustedLeader("nekroagent")) {
            Button nekroButton = Buttons.gray("exhaustAgent_nekroagent",
                "Use Nekro Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.Nekro));
            buttons.add(nekroButton);
        }
        if (player.hasUnexhaustedLeader("vaylerianagent")) {
            Button nekroButton = Buttons.gray("exhaustAgent_vaylerianagent",
                "Use Vaylerian Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.vaylerian));
            buttons.add(nekroButton);
        }
        if (player.ownsUnit("ghost_mech")
            && ButtonHelper.getNumberOfUnitsOnTheBoard(game, player, "mech", false) > 0
            && !ButtonHelper.isLawInPlay(game, "articles_war")) {
            Button ghostButton = Buttons.gray("creussMechStep1_", "Use Creuss Mech")
                .withEmoji(Emoji.fromFormatted(Emojis.Ghost));
            buttons.add(ghostButton);
        }
        if (player.ownsUnit("nivyn_mech2")
            && ButtonHelper.getNumberOfUnitsOnTheBoard(game, player, "mech", false) > 0
            && !game.getLaws().containsKey("articles_war")) {
            Button ghostButton = Buttons.gray("nivynMechStep1_", "Use Nivyn Mech")
                .withEmoji(Emoji.fromFormatted(Emojis.nivyn));
            buttons.add(ghostButton);
        }
        if (player.hasUnexhaustedLeader("kolleccagent")) {
            Button nekroButton = Buttons.gray("exhaustAgent_kolleccagent",
                "Use Kollecc Agent")
                .withEmoji(Emoji.fromFormatted(Emojis.kollecc));
            buttons.add(nekroButton);
        }
        if (player.hasAbility("cunning")) {
            buttons.add(Buttons.green("setTrapStep1", "Set a Trap"));
            buttons.add(Buttons.red("revealTrapStep1", "Reveal a Trap"));
            buttons.add(Buttons.gray("removeTrapStep1", "Remove a Trap"));
        }

        if (player.hasAbility("divination") && ButtonHelperAbilities.getAllOmenDie(game).size() > 0) {
            StringBuilder omenDice = new StringBuilder();
            for (int omenDie : ButtonHelperAbilities.getAllOmenDie(game)) {
                omenDice.append(" ").append(omenDie);
            }
            omenDice = new StringBuilder(omenDice.toString().trim());
            Button augers = Buttons.gray("getOmenDice", "Use an omen die (" + omenDice + ")")
                .withEmoji(Emoji.fromFormatted(Emojis.mykomentori));
            buttons.add(augers);
        }
        Button playerPref = Button.secondary("offerPlayerPref", "Player Settings");
        buttons.add(playerPref);
        Button listGames = Button.secondary("searchMyGames", "List My Games");
        buttons.add(listGames);
        buttons.add(Buttons.green("showObjInfo_both", "Scoring Info"));
        if (!game.isFowMode()) {
            buttons.add(Button.secondary("chooseMapView", "Map Features"));
        }
        boolean hadAnyUnplayedSCs = false;
        for (Integer SC : player.getSCs()) {
            if (!game.getPlayedSCs().contains(SC)) {
                hadAnyUnplayedSCs = true;
            }
        }
        if (!hadAnyUnplayedSCs) {
            buttons.add(Buttons.red("resolvePreassignment_Pre Pass " + player.getFaction(), "Pass on Next Turn"));
        }
        buttons.add(Buttons.REFRESH_INFO);

        List<String> phasesBeforeAction = List.of("action", "strategy", "playerSetup");
        boolean hasSummary = false;
        for (int x = 1; x <= game.getRound(); ++x) {
            if (!game.getStoredValue("endofround" + x + player.getFaction()).isEmpty())
                hasSummary = true;
        }
        if (game.getRound() > 1 || !phasesBeforeAction.contains(game.getPhaseOfGame()) || hasSummary) {
            // after the action phase round 1, show the edit summary button by default
            buttons.add(Buttons.EDIT_SUMMARIES);
        }
        buttons.add(Buttons.POST_NOTEPAD);
        buttons.add(Buttons.EDIT_NOTEPAD);
        buttons.add(Buttons.green("cardsInfo", "Cards Info Refresh"));

        String message = "You may use these buttons to do various things:";

        // Refresh the various buttons if they're the last message in the thread
        player.getCardsInfoThread().retrieveMessageById(player.getCardsInfoThread().getLatestMessageId()).queue(
            msg -> {
                if (msg != null && message.equals(msg.getContentRaw())) {
                    msg.delete().queue();
                }
            }, BotLogger::catchRestError);

        MessageHelper.sendMessageToChannelWithButtons(player.getCardsInfoThread(), message, buttons);
    }

    protected String getActionDescription() {
        return "Send to your Cards Info thread: Scored & Unscored SOs, ACs, and PNs in both hand and Play Area";
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void registerCommands(CommandListUpdateAction commands) {
        // Moderation commands with required options
        commands.addCommands(
            Commands.slash(getActionID(), getActionDescription())
                .addOptions(new OptionData(OptionType.STRING, Constants.LONG_PN_DISPLAY, "Long promissory display, y or yes to show full promissory text").setRequired(false)));
    }

}
