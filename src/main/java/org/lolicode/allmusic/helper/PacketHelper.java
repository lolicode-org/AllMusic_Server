package org.lolicode.allmusic.helper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.lolicode.allmusic.Allmusic;
import org.lolicode.allmusic.music.Api;
import org.lolicode.allmusic.music.MusicObj;

import java.nio.charset.StandardCharsets;

public class PacketHelper {
    public static PacketByteBuf getPlayPacket(@NotNull MusicObj musicObj) {
        if (musicObj.url == null || musicObj.url.equals(""))
            return null;

        // What's these?
        // IDK, just copy from coloryr's code
        String data = "[Play]" + musicObj.url;

        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer(bytes.length + 1);
        buf.writeByte(666);
        buf.writeBytes(bytes);

        return new PacketByteBuf(buf);
    }

    public static PacketByteBuf getStopPacket() {
        byte[] bytes = "[Stop]".getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = Unpooled.buffer(bytes.length + 1);
        buf.writeByte(666);
        buf.writeBytes(bytes);

        return new PacketByteBuf(buf);
    }

    public static Text getPlayMessage(@NotNull MusicObj musicObj) {
        String player = musicObj.player;
        if (player == null || player.equals(""))
            player = "Default";
        return Text.of("§eNow playing: §a" + musicObj.name + " §e-§9 "
                + String.join(" & ",
                musicObj.ar.stream().map(artistObj -> artistObj.name).toArray(String[]::new))
                + " §eby §d" + player);
    }

    public static Text getVoteMessage(int count, int total) {
        return Text.of("§eVote count: §a" + count + " §e/ §9" + total +
                " §e(§a" + (int) (count * 100.0 / total) + "%§e of §a" + Allmusic.CONFIG.voteThreshold * 100 + "%§e)");
    }

    public static Text getOrderMessage(@NotNull MusicObj musicObj) {
        return Text.of("§eOrdered: §a" + musicObj.name + " §e-§9 "
                + String.join(" & ",
                musicObj.ar.stream().map(artistObj -> artistObj.name).toArray(String[]::new))
                + " §eby §d" + musicObj.player);
    }

    public static Text getOrderMessage() {
        return Text.of("§cGet music info failed.");
    }

    public static Text getOrderedMessage() {
        return Text.of("§cThis song has been ordered.");
    }

    public static Text getDelMessage(MusicObj musicObj) {
        return Text.of("§eDeleted: §a" + musicObj.name);
    }

    public static Text getDelMessage(int error) {
        if (error == 1) {
            return Text.of("§cInvalid index.");
        } else if (error == 2) {
            return Text.of("§cYou don't have sufficient permissions.");
        } else {
            return Text.of("§cDelete music failed.");
        }
    }

    public static Text getListMessage() {
        if (Allmusic.orderList.songs.size() == 0) {
            return Text.of("§cNo music in playing list.");
        } else {
            MutableText text = Text.literal("§ePlaying list: \n");
            int num = 0;
            for (MusicObj musicObj : Allmusic.orderList.songs) {
                text.append(Text.literal("§e" + (++num) + ". " + "§a" + musicObj.name + " §e-§9 "
                        + String.join(" & ",
                        musicObj.ar.stream().map(artistObj -> artistObj.name).toArray(String[]::new))
                        + " §eby §d" + musicObj.player))
                        .append(Text.literal(" [X]").setStyle(Style.EMPTY.withColor(Formatting.RED)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/music del " + num))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§cClick to delete it.")))));
                if (num != Allmusic.orderList.songs.size())
                    text.append(Text.literal("\n"));
            }
            return text;
        }
    }

    public static Text getSearchMessage(Api.SearchResult result) {
        if (result.result.songs == null || result.result.songs.length == 0) {
            return Text.of("§cNo result found.");
        } else {
            MutableText text = Text.literal("§aName §e-§9 Artist §e- §dAlbum" + "\n")
                    .setStyle(Text.empty().getStyle().withColor(TextColor.fromFormatting(Formatting.YELLOW)));
            int num = 0;
            for (Api.SearchResult.Result.OneSong song : result.result.songs) {
                text.append(Text.literal( "§e" + (++num) + ". " + "§a" + song.name + " §e-§9 "
                        + String.join(" & ",
                        song.artists.stream().map(artistObj -> artistObj.name).toArray(String[]::new))
                        + "§e - §d" + song.album.name + "\n").setStyle(
                        Text.empty().getStyle().withColor(TextColor.fromFormatting(Formatting.GREEN))
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.RUN_COMMAND,
                                        "/music add " + song.id))
                                .withHoverEvent(new HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        Text.of("Click to add it to playing list.")))));
            }
            MutableText pagePrev = Text.literal("<<");
            if (result.result.page == 1) {
                pagePrev.setStyle(Text.empty().getStyle().withColor(TextColor.fromFormatting(Formatting.GRAY)));
            } else {
                pagePrev.setStyle(Text.empty().getStyle().withColor(TextColor.fromFormatting(Formatting.BLUE))
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/music search page " + (result.result.page - 1) + " " + result.result.keyword))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Text.of("Click to go to previous page."))));
            }
            MutableText pageNext = Text.literal(">>");
            if (result.result.page == (result.result.songCount + 9) / 10) {
                pageNext.setStyle(Text.empty().getStyle().withColor(TextColor.fromFormatting(Formatting.GRAY)));
            } else {
                pageNext.setStyle(Text.empty().getStyle().withColor(TextColor.fromFormatting(Formatting.BLUE))
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/music search page " + (result.result.page + 1) + " " + result.result.keyword))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Text.of("Click to go to next page."))));
            }
            text.append(pagePrev).append(Text.of(
                            "§r ---- §ePage §a" + result.result.page + " §r/ §a" + (result.result.songCount + 9) / 10 + "§r ---- "))
                    .append(pageNext);
            return text;
        }
    }

    public static Text getSearchMessage() {
        return Text.of("§cSearch failed.");
    }
}
