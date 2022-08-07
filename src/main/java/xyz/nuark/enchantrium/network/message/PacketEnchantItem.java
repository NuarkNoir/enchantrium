package xyz.nuark.enchantrium.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.block.entity.custom.EnchanterBlockEntity;
import xyz.nuark.enchantrium.network.Networking;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class PacketEnchantItem {
    private final BlockPos blockEntityPos;
    private final List<EnchantmentInstance> enchantments;

    public PacketEnchantItem(BlockPos blockEntityPos, List<EnchantmentInstance> enchantments) {
        this.blockEntityPos = blockEntityPos;
        this.enchantments = enchantments;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EnchanterBlockEntity ebe = (EnchanterBlockEntity)ctx.getSender().level.getBlockEntity(blockEntityPos);
            ebe.enchant(enchantments);
        });
        return true;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(blockEntityPos);
        friendlyByteBuf.writeCollection(enchantments, (buf, enchantment) -> {
            buf.writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(enchantment.enchantment));
            buf.writeVarInt(enchantment.level);
        });
    }

    public static PacketEnchantItem decode(FriendlyByteBuf friendlyByteBuf) {
        return new PacketEnchantItem(
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readList((buf) -> new EnchantmentInstance(
                        ForgeRegistries.ENCHANTMENTS.getValue(buf.readResourceLocation()),
                        buf.readVarInt()
                ))
        );
    }
}
