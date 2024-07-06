package com.bot.app.commands;

/**
 * An interface for commands given to the bot
 */
public interface Command {

    /**
     * Gets the name of the command
     *
     * @return the name of the command
     */
    String getName();

    /**
     * Executes the command associated code
     */
    void execute();
}
