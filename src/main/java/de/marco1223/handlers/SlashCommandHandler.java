package de.marco1223.handlers;

import de.marco1223.interfaces.ISlashCommandHandler;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandHandler extends ListenerAdapter {

    private final Map<String, ISlashCommandHandler> commands = new HashMap<>();
    private boolean updated = false;

    // Adds multiple commands to the map
    public void addCommands(ISlashCommandHandler... slashCommands) {
        for (ISlashCommandHandler slashCommand : slashCommands) {
            CommandData commandData = slashCommand.getCommandData();
            // Associate the command's name to the slash command object
            commands.put(commandData.getName(), slashCommand);
        }
    }

    // Listens for a GuildReadyEvent and then updates the global commands
    // This will run multiple times, so we need to make sure it only runs once

    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if (updated) return; // Return if already updated
        updated = true;

        // Add the commands on the global scope
        event.getJDA().updateCommands()
                .addCommands(commands.values().stream().map(ISlashCommandHandler::getCommandData).toList())
                .queue();
    }

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Get our slash command by name
        ISlashCommandHandler slashCommand = commands.get(event.getName());

        // The slash command exists with such name, execute it
        slashCommand.execute(event);

    }

}
