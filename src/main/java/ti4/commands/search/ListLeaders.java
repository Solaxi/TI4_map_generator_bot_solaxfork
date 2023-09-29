package ti4.commands.search;

import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ti4.generator.Mapper;
import ti4.helpers.Constants;
import ti4.helpers.Helper;
import ti4.message.MessageHelper;

public class ListLeaders extends SearchSubcommandData {

    public ListLeaders() {
        super(Constants.SEARCH_LEADERS, "List all leaders the bot can use");
        addOptions(new OptionData(OptionType.STRING, Constants.SEARCH, "Searches the text and limits results to those containing this string.").setAutoComplete(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String searchString = event.getOption(Constants.SEARCH, null, OptionMapping::getAsString);
        HashMap<String, String> leaderList = Mapper.getLeaderRepresentations();
        List<String> searchedList = leaderList.entrySet().stream()
            .map(e -> "`" + e.getKey() + "`= " + Helper.getEmojiFromDiscord(e.getKey()) + e.getValue())
            .filter(s -> searchString == null || s.toLowerCase().contains(searchString.toLowerCase()))
            .sorted().toList();

        String searchDescription = searchString == null ? "" : " search: " + searchString;
        String message = "**__Leader List__**" + searchDescription + "\n" + String.join("\n", searchedList);
        if (searchedList.size() > 3) {
            String threadName = event.getFullCommandName() + searchDescription;
            MessageHelper.sendMessageToThread(event.getChannel(), threadName, message);
        } else if (searchedList.size() > 0) {
            event.getChannel().sendMessage(message).queue();
        }
    }
}