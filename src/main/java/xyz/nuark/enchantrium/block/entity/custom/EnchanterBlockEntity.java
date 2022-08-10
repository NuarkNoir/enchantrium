package xyz.nuark.enchantrium.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nuark.enchantrium.Enchantrium;
import xyz.nuark.enchantrium.block.entity.ModBlockEntities;
import xyz.nuark.enchantrium.screen.EnchanterMenu;
import xyz.nuark.enchantrium.util.EnchantmentUtil;

import java.util.List;

public class EnchanterBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public EnchanterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ENCHANTER_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return new TextComponent("Enchanter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerID, Inventory inventory, Player player) {
        return new EnchanterMenu(containerID, inventory, this);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, inventory);
        } else {
            Enchantrium.LOGGER.error("EnchanterBlockEntity.drops() - level is null");
        }
    }

    public ItemStack getInput() {
        return itemHandler.getStackInSlot(0);
    }

    public ItemStack getEmeralds() {
        return itemHandler.getStackInSlot(1);
    }

    public ItemStack getNetherite() {
        return itemHandler.getStackInSlot(2);
    }

    public boolean enchant(List<EnchantmentInstance> enchantments, EnchantmentUtil.EnchantmentCost enchantmentCost, boolean setUnbreakable) {
        ItemStack input = itemHandler.extractItem(0, 1, false);
        itemHandler.extractItem(1, enchantmentCost.lapis(), false);
        itemHandler.extractItem(2, enchantmentCost.netherite(), false);
        for (EnchantmentInstance enchantment : enchantments) {
            input.enchant(enchantment.enchantment, enchantment.level);
        }
        input.getOrCreateTag().putBoolean("Unbreakable", setUnbreakable);

        itemHandler.setStackInSlot(0, input);
        itemHandler.insertItem(0, input, false);

        return true;
    }
}
