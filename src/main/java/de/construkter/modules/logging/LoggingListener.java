package de.construkter.modules.logging;

import de.construkter.ressources.BotConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class LoggingListener extends ListenerAdapter {
    EmbedBuilder eb = new EmbedBuilder();
    BotConfig config = new BotConfig();

    private final Map<String, String> messageCache = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String messageId = message.getId();
        String messageContent = message.getContentRaw();

        messageCache.put(messageId, messageContent);
    }
    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        assert logChannel != null;
        Message message = event.getMessage();
        String contentNew = event.getMessage().getContentRaw();
        String contentOld = message.getContentDisplay();
        eb.setTitle("Nachricht bearbeitet");
        eb.setDescription("**User: ** " + event.getAuthor().getName() + "\n" +
                "**Kanal: ** " + event.getChannel().getName() + "\n" +
                "**Davor: ** " + contentNew + "\n" +
                "**Nachher: ** " + contentOld);
        eb.setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
        eb.setTimestamp(Instant.now());
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        String messageId = event.getMessageId();

        if (messageCache.containsKey(messageId)) {
            String deletedMessageContent = messageCache.get(messageId);
            eb.setTitle("Nachricht gelöscht");
            eb.setDescription("**User:** null" + "\n" +
                    "**Kanal: ** " + event.getChannel().getName() + "\n" +
                    "**Nachricht: ** " + deletedMessageContent);
        } else {
            eb.setTitle("Nachricht gelöscht");
            eb.setDescription("**User: ** null" + "\n" +
                    "**Kanal: ** " + event.getChannel().getName() + "\n" +
                    "**Nachricht: ** " + " Message wasn't cached");
        }
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        Channel channel = event.getChannel();
        eb.setTitle("Kanal erstellt");
        eb.setDescription("**Channel: ** " + channel.getName());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        Channel channel = event.getChannel();
        eb.setTitle("Kanal gelöscht");
        eb.setDescription("**Channel: ** " + channel.getName());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Nutzer gebannt");
        eb.setDescription("**Moderator: ** " + event.getUser().getName());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Nutzer entbannt");
        eb.setDescription("**Moderator: ** " + event.getUser().getName());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Nutzer Server leave");
        eb.setDescription("**Nutzer: ** " + event.getUser().getName());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Nutzer Server join");
        eb.setDescription("**Nutzer: ** " + event.getUser().getName());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildUpdateIcon(GuildUpdateIconEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Server Icon update");
        eb.setDescription("Thumbnail: Old Icon \n**Image:** New Icon");
        eb.setThumbnail(event.getOldIconUrl());
        eb.setImage(event.getNewIconUrl());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Server Name update");
        eb.setDescription("**New-Name: ** " + event.getNewName()
                + " \n**Old-Name: ** " + event.getOldName());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Server Owner update");
        eb.setDescription("**Old-Owner: ** " + event.getOldOwner() + " \n**New-Owner: ** " + event.getNewOwner());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Server Invite Create");
        eb.setDescription("**User:** " + Objects.requireNonNull(event.getInvite().getInviter()).getName() + "\n" +
                "**Einladung:** " + event.getInvite().getCode());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public void onGuildInviteDelete(GuildInviteDeleteEvent event) {
        TextChannel logChannel = event.getJDA().getTextChannelById(config.getProperty("logging-channel"));
        eb.setTitle("Server Invite Delete");
        eb.setDescription("**Einladung:** " + event.getCode());
        eb.setTimestamp(Instant.now());
        assert logChannel != null;
        logChannel.sendMessageEmbeds(eb.build()).queue();
    }
}