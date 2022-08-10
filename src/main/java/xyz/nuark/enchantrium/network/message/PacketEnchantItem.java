package xyz.nuark.enchantrium.network.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.nuark.enchantrium.block.entity.custom.EnchanterBlockEntity;
import xyz.nuark.enchantrium.util.EnchantmentUtil;

import java.util.List;
import java.util.function.Supplier;

public class PacketEnchantItem {
    private final BlockPos blockEntityPos;
    private final List<EnchantmentInstance> enchantments;
    private final EnchantmentUtil.EnchantmentCost enchantmentCost;
    private final boolean unbreakingSet;

    public PacketEnchantItem(BlockPos blockEntityPos, List<EnchantmentInstance> enchantments, EnchantmentUtil.EnchantmentCost enchantmentCost, boolean unbreakingSet) {
        this.blockEntityPos = blockEntityPos;
        this.enchantments = enchantments;
        this.enchantmentCost = enchantmentCost;
        this.unbreakingSet = unbreakingSet;
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EnchanterBlockEntity ebe = (EnchanterBlockEntity)ctx.getSender().level.getBlockEntity(blockEntityPos);
            if (ebe.enchant(enchantments, enchantmentCost, unbreakingSet)) {
                ctx.getSender().giveExperienceLevels(-enchantmentCost.levels());
            }
        });
        return true;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(blockEntityPos);
        friendlyByteBuf.writeCollection(enchantments, (buf, enchantment) -> {
            buf.writeResourceLocation(ForgeRegistries.ENCHANTMENTS.getKey(enchantment.enchantment));
            buf.writeVarInt(enchantment.level);
        });
        friendlyByteBuf.writeVarInt(enchantmentCost.levels());
        friendlyByteBuf.writeVarInt(enchantmentCost.lapis());
        friendlyByteBuf.writeVarInt(enchantmentCost.netherite());
        friendlyByteBuf.writeBoolean(unbreakingSet);
    }

    public static PacketEnchantItem decode(FriendlyByteBuf friendlyByteBuf) {
        return new PacketEnchantItem(
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readList((buf) -> new EnchantmentInstance(
                        ForgeRegistries.ENCHANTMENTS.getValue(buf.readResourceLocation()),
                        buf.readVarInt()
                )),
                new EnchantmentUtil.EnchantmentCost(
                        friendlyByteBuf.readVarInt(),
                        friendlyByteBuf.readVarInt(),
                        friendlyByteBuf.readVarInt()
                ),
                friendlyByteBuf.readBoolean()
        );
    }
}
