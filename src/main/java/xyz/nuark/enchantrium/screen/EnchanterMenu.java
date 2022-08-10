package xyz.nuark.enchantrium.screen;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nuark.enchantrium.block.ModBlocks;
import xyz.nuark.enchantrium.block.entity.custom.EnchanterBlockEntity;
import xyz.nuark.enchantrium.network.Networking;
import xyz.nuark.enchantrium.network.message.PacketEnchantItem;
import xyz.nuark.enchantrium.screen.slot.FilteredSlot;
import xyz.nuark.enchantrium.util.EnchantmentUtil;

import java.util.List;

public class EnchanterMenu extends AbstractContainerMenu {
    private final EnchanterBlockEntity blockEntity;
    private final Level level;

    private ItemStack inputStack = new ItemStack(Items.AIR);
    private List<EnchantmentInstance> enchantments = Lists.newArrayList();
    private boolean unbreakingSet = false;
    private int currentEnchantmentIndex = -1;
    private final Object lock = new Object();

    public EnchanterMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public EnchanterMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.ENCHANTER_MENU.get(), containerId);
        checkContainerSize(inv, 3);
        blockEntity = ((EnchanterBlockEntity) entity);
        this.level = inv.player.level;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            this.addSlot(new FilteredSlot(
                    handler, 0, 8, 19,
                    stack -> EnchantmentUtil.canBeEnchanted(stack) && !stack.isEnchanted() && !stack.is(Items.BOOK)
            ));
            this.addSlot(new FilteredSlot(
                    handler, 1, 31, 19,
                    stack -> stack.is(Tags.Items.STORAGE_BLOCKS_LAPIS)
            ));
            this.addSlot(new FilteredSlot(
                    handler, 2, 54, 19,
                    stack -> stack.is(Tags.Items.INGOTS_NETHERITE)
            ));
        });
    }

    @Nullable
    public EnchantmentInstance getCurrentEnchantment() {
        synchronized (lock) {
            if (blockEntity.getInput().isEmpty() || blockEntity.getInput().isEnchanted()) {
                currentEnchantmentIndex = -1;
                return null;
            } else if (currentEnchantmentIndex == -1) {
                currentEnchantmentIndex = 0;
            }
            if (!this.inputStack.sameItem(blockEntity.getInput())) {
                this.enchantments = EnchantmentUtil.getApplicableEnchantments(blockEntity.getInput());
                currentEnchantmentIndex = 0;
                this.inputStack = blockEntity.getInput();
            }
            return this.enchantments.size() > 0 ? this.enchantments.get(currentEnchantmentIndex) : null;
        }
    }

    public boolean unbreakable() {
        return unbreakingSet;
    }

    public EnchantmentUtil.EnchantmentCost getEnchantmentRequirements() {
        return EnchantmentUtil.calculateEnchantmentPrice(this.enchantments.stream().filter(e -> e.level > 0).toList(), unbreakingSet);
    }

    public boolean requirementsMet(Player player, EnchantmentUtil.EnchantmentCost enchantmentRequirements) {
        return player.experienceLevel >= enchantmentRequirements.levels()
                && blockEntity.getEmeralds().getCount() >= enchantmentRequirements.lapis()
                && blockEntity.getNetherite().getCount() >= enchantmentRequirements.netherite();
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId < 0 || buttonId >= 6) {
            Util.logAndPauseIfInIde(player.getName() + " pressed invalid button id: " + buttonId);
            return false;
        }

        synchronized (lock) {
            if (buttonId == 0) {
                var enchantments = this.enchantments.stream().filter(e -> e.level > 0).toList();
                if (enchantments.isEmpty()) return true;
                Networking.sendToServer(new PacketEnchantItem(
                        blockEntity.getBlockPos(),
                        enchantments,
                        getEnchantmentRequirements(),
                        unbreakingSet
                ));
            } else if (buttonId == 1) {
                currentEnchantmentIndex = (currentEnchantmentIndex - 1 + enchantments.size()) % enchantments.size();
            } else if (buttonId == 2) {
                currentEnchantmentIndex = (currentEnchantmentIndex + 1) % enchantments.size();
            } else if (buttonId == 3) {
                var enchantment = this.enchantments.get(currentEnchantmentIndex);
                this.enchantments.set(currentEnchantmentIndex, new EnchantmentInstance(
                        enchantment.enchantment,
                        enchantment.level > 0 ? (enchantment.level - 1) : 0
                ));
            } else if (buttonId == 4) {
                var enchantment = this.enchantments.get(currentEnchantmentIndex);
                this.enchantments.set(currentEnchantmentIndex, new EnchantmentInstance(
                        enchantment.enchantment,
                        enchantment.level < enchantment.enchantment.getMaxLevel() ? (enchantment.level + 1) : enchantment.level
                ));
            } else {
                unbreakingSet = !unbreakingSet;
            }
        }
        return true;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.ENCHANTER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 150 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 208));
        }
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots and the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must be the number of slots you have!

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
}
