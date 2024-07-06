package com.bot.app.exceptions;

public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(String commandName) {
        super(String.format("The command \"%s\" does not exist!", commandName));
    }
}
