package xyz.nuark.enchantrium.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.network.message.PacketEnchantItem;
import xyz.nuark.enchantrium.network.message.PacketSyncEnchantedItemToClient;

public class Networking {
    public static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int nextPacketId() {
        return packetId++;
    }

    public static void register() {
        INSTANCE = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Enchantrium.MOD_ID, "main"))
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(PROTOCOL_VERSION::equals)
                .serverAcceptedVersions(PROTOCOL_VERSION::equals)
                .simpleChannel();

        INSTANCE.messageBuilder(PacketEnchantItem.class, nextPacketId(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(PacketEnchantItem::encode)
                .decoder(PacketEnchantItem::decode)
                .consumer(PacketEnchantItem::handle)
                .add();

        INSTANCE.messageBuilder(PacketSyncEnchantedItemToClient.class, nextPacketId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PacketSyncEnchantedItemToClient::encode)
                .decoder(PacketSyncEnchantedItemToClient::decode)
                .consumer(PacketSyncEnchantedItemToClient::handle)
                .add();
    }


    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
