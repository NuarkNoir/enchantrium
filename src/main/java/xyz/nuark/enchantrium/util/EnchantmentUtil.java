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
}
