package org.lolicode.allmusic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;
import okhttp3.OkHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lolicode.allmusic.command.MusicCommand;
import org.lolicode.allmusic.config.ModConfig;
import org.lolicode.allmusic.event.PlayerJoinCallback;
import org.lolicode.allmusic.music.MusicObj;
import org.lolicode.allmusic.music.SongList;
import org.lolicode.allmusic.task.PlayerJoin;
import org.lolicode.allmusic.task.ServerStop;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Allmusic implements DedicatedServerModInitializer {
    public static final String MOD_ID = "allmusic";
    public static final String MOD_NAME = "AllMusic";
    public static final String MOD_VERSION = "1.0.0";
    public static final Identifier ID = new Identifier("allmusic", "channel");
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    public static final ModConfig CONFIG = new ModConfig();
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    public static final Timer TIMER = new Timer();
    public static volatile TimerTask task = null;
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder().setNameFormat("AllMusic-Thread-%d").build());

    public static final SongList idleList = new SongList();
    public static final SongList orderList = new SongList();
    public static Set<String> currentVote = new HashSet<>();
    public static MusicObj currentMusic = null;

    @Override
    public void onInitializeServer() {
        if (ModConfig.load()) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MusicCommand.register(dispatcher));
            PlayerJoinCallback.EVENT.register(PlayerJoin::OnPlayerJoin);
            LOGGER.info("AllMusic mod loaded");
        } else {
            LOGGER.error("Failed to load mod config, mod will not work");
        }
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerStop.onServerStop());
    }
}
