package xyz.nuark.enchantrium.util;

import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class EnchantmentUtil {
    public static boolean canBeEnchanted(ItemStack stack) {
        return stack.getItem().isEnchantable(stack);
    }

    public static List<EnchantmentInstance> getApplicableEnchantments(ItemStack itemStack) {
        List<EnchantmentInstance> list = Lists.newArrayList();

        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
            if (enchantment.canEnchant(itemStack) && !enchantment.isCurse()) {
                list.add(new EnchantmentInstance(enchantment, 0));
            }
        }

        return list;
    }

    public static EnchantmentCost calculateEnchantmentPrice(List<EnchantmentInstance> enchantments, boolean hasUnbreaking) {
        if (enchantments.isEmpty()) {
            return new EnchantmentCost(0, 0, 0);
        }
        int lapis = Math.min(20 + enchantments.size() * (hasUnbreaking ? 6 : 3), 64);
        int netherite = Math.min(Math.max((hasUnbreaking ? 5 : -3) + enchantments.size() / 2, 0), 64);
        int levels = enchantments.stream().reduce(
                0,
                (acc, enchantment) -> acc + enchantment.level,
                Integer::sum
        );
        return new EnchantmentCost(levels, lapis, netherite);
    }

    public record EnchantmentCost(int levels, int lapis, int netherite) {
    }
}
