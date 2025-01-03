package ti4.commands.agenda;

import com.amazonaws.util.StringUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.commands.GameStateSubcommand;
import ti4.helpers.Constants;
import ti4.map.Game;
import ti4.map.Player;
import ti4.message.MessageHelper;

class AddLaw extends GameStateSubcommand {

    public AddLaw() {
        super(Constants.ADD_LAW, "Add Agenda as Law", true, true);
        addOptions(new OptionData(OptionType.INTEGER, Constants.AGENDA_ID, "Agenda ID, which is found between the ()").setRequired(true).setAutoComplete(true));
        addOptions(new OptionData(OptionType.STRING, Constants.ELECTED, "Elected non-player game object (e.g. secret objective, planet, etc.)"));
        addOptions(new OptionData(OptionType.STRING, Constants.FACTION_COLOR, "Elected faction").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption(Constants.AGENDA_ID);
        if (option == null) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "No agenda ID defined.");
            return;
        }

        Game game = getGame();
        Player player = getPlayer();
        String optionText;
        boolean playerWasElected = !StringUtils.isNullOrEmpty(event.getOption(Constants.FACTION_COLOR, null, OptionMapping::getAsString));
        if (playerWasElected) {
            optionText = player.getFaction();
        } else {
            optionText = event.getOption(Constants.ELECTED, null, OptionMapping::getAsString);
        }

        Player electedPlayer = game.getPlayerFromColorOrFaction(optionText);
        if (electedPlayer != null) {
            optionText = electedPlayer.getFaction();
        }

        boolean success = game.addLaw(option.getAsInt(), optionText);
        if (success) {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Law added.");
        } else {
            MessageHelper.sendMessageToChannel(event.getChannel(), "Law ID not found.");
        }
    }
}
