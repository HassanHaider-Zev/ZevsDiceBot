package com.hassanhaider.ZevsDiceBot;

import com.hassanhaider.ZevsDiceBot.DiceEngine.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import javax.security.auth.login.LoginException;

public class ZevsDiceBot {
    private final Dotenv config; //inilitaizes Dotenv file manager
    private final ShardManager shardManager; //initializes shard manager

    //builds bot when called
    public ZevsDiceBot() throws LoginException {
        //load enviorment variables
        config = Dotenv.configure().load();
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"));

        //build shardmanager
        builder.setStatus(OnlineStatus.ONLINE);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        shardManager = builder.build();

        //register listeners
        shardManager.addEventListener(new EventListener());
    }

    public static void main(String[] args) {
        try {
            ZevsDiceBot bot = new ZevsDiceBot();
        } catch (LoginException e) {
            System.out.println("ERROR: Bot token is invalid. Login Failed.");
        }
    }
}
