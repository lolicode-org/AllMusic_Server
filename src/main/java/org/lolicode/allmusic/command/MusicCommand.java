package org.lolicode.allmusic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.lolicode.allmusic.config.ModConfig;
import org.lolicode.allmusic.helper.LoginHelper;
import org.lolicode.allmusic.manager.MusicManager;

public class MusicCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> rootNode = CommandManager.literal("music")
                .requires(Permissions.require("allmusic", 0))
                .then(CommandManager.argument("url", StringArgumentType.greedyString())
                        .requires(Permissions.require("allmusic.add", 0))
                        .executes(context -> {
                            String url = StringArgumentType.getString(context, "url");
                            MusicManager.order(context.getSource().getServer(), context.getSource(), url);
                            return 0;
                        }))
                .build();
        LiteralCommandNode<ServerCommandSource> addNode = CommandManager.literal("add")
                .requires(Permissions.require("allmusic.add", 0))
                .then(CommandManager.argument("url", StringArgumentType.greedyString())
                        .executes(context -> {
                            String url = StringArgumentType.getString(context, "url");
                            MusicManager.order(context.getSource().getServer(), context.getSource(), url);
                            return 0;
                        })).build();
        LiteralCommandNode<ServerCommandSource> delNode = CommandManager.literal("del")
                .requires(Permissions.require("allmusic.del", 0))
                .then(CommandManager.argument("id", IntegerArgumentType.integer())
                        .executes(context -> {
                            int id = IntegerArgumentType.getInteger(context, "id");
                            MusicManager.del(context.getSource().getServer(), context.getSource(), id);
                            return 0;
                        })).build();
        LiteralCommandNode<ServerCommandSource> listNode = CommandManager.literal("list")
                .requires(Permissions.require("allmusic.list", 0))
                .executes(context -> {
                    MusicManager.list(context.getSource().getServer(), context.getSource());
                    return 0;
                }).build();
        LiteralCommandNode<ServerCommandSource> nextNode = CommandManager.literal("next")
                .requires(Permissions.require("allmusic.next", 1))
                .executes(context -> {
                    MusicManager.next(context.getSource().getServer());
                    return 0;
                }).build();
        LiteralCommandNode<ServerCommandSource> searchNode = CommandManager.literal("search")
                .requires(Permissions.require("allmusic.search", 0))
                .then(CommandManager.literal("page")
                        .then(CommandManager.argument("page", IntegerArgumentType.integer())
                                .then(CommandManager.argument("keyword", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            String keyword = StringArgumentType.getString(context, "keyword");
                                            int page = IntegerArgumentType.getInteger(context, "page");
                                            MusicManager.search(context.getSource().getServer(), context.getSource(), keyword, page);
                                            return 0;
                                        }))))
                .then(CommandManager.argument("keyword", StringArgumentType.greedyString())
                        .executes(context -> {
                            String keyword = StringArgumentType.getString(context, "keyword");
                            MusicManager.search(context.getSource().getServer(), context.getSource(), keyword, 1);
                            return 0;
                        })).build();
        LiteralCommandNode<ServerCommandSource> voteNode = CommandManager.literal("vote")
                .requires(Permissions.require("allmusic.vote", 0))
                .executes(context -> {
                    MusicManager.vote(context.getSource().getServer(), context.getSource());
                    return 0;
                }).build();
        LiteralCommandNode<ServerCommandSource> reloadNode = CommandManager.literal("reload")
                .requires(Permissions.require("allmusic.reload", 2))
                .executes(context -> {
                    ModConfig.reload(context.getSource().getServer(), context.getSource());
                    return 0;
                }).build();
        LiteralCommandNode<ServerCommandSource> loginNode = CommandManager.literal("login")
                .requires(Permissions.require("allmusic.login", 4))
                .then(CommandManager.literal("start")
                        .requires(Permissions.require("allmusic.login.start", 4))
                        .executes(context -> {
                            LoginHelper.genQr(context.getSource());
                            return 0;
                        }))
                .then(CommandManager.literal("check")
                        .requires(Permissions.require("allmusic.login.check", 4))
                        .executes(context -> {
                            LoginHelper.check(context.getSource());
                            return 0;
                        }))
                .then(CommandManager.literal("status")
                        .requires(Permissions.require("allmusic.login.status", 4))
                        .executes(context -> {
                            LoginHelper.status(context.getSource());
                            return 0;
                        }))
                .build();

        rootNode.addChild(addNode);
        rootNode.addChild(delNode);
        rootNode.addChild(listNode);
        rootNode.addChild(nextNode);
        rootNode.addChild(searchNode);
        rootNode.addChild(voteNode);
        rootNode.addChild(reloadNode);
        rootNode.addChild(loginNode);
        dispatcher.getRoot().addChild(rootNode);
    }
}
