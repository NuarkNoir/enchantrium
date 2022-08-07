package xyz.nuark.enchantrium.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.block.ModBlocks;
import xyz.nuark.enchantrium.block.entity.custom.EnchanterBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Enchantrium.MOD_ID);

    public static final RegistryObject<BlockEntityType<EnchanterBlockEntity>> ENCHANTER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("enchanter_block_entity", () -> BlockEntityType.Builder.of(EnchanterBlockEntity::new, ModBlocks.ENCHANTER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
