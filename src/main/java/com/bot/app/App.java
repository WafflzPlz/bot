package com.bot.app;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App
{
    private static final Dotenv dotenv = Dotenv.load();
    private static final String BOT_TOKEN = dotenv.get("BOT_TOKEN");

    public static void main( String[] args )
    {

        JDA api = JDABuilder.createDefault(BOT_TOKEN)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(new CommandListener())
                .build();
    }
}