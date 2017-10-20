package blue.sparse.bukkitk.extensions.inventory

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

val ItemStack?.empty get() = this == null || type == Material.AIR || amount <= 0
fun ItemStack?.notEmptyOrNull() = if (empty) null else this

inline fun item(material: Material, crossinline body: ItemStack.() -> Unit): ItemStack
		= ItemStack(material).apply(body)

inline fun item(item: ItemStack, crossinline body: ItemStack.() -> Unit): ItemStack
		= item.apply(body)

inline fun ItemStack.meta(body: ItemMeta.() -> Unit)
{
	itemMeta = itemMeta.apply(body)
}

inline fun <reified T : ItemMeta> ItemStack.typedMeta(body: T.() -> Unit)
{
	itemMeta = itemMeta.apply { body(this as T) }
}

fun ItemStack.enchantedEffect() = meta { enchantedEffect() }

fun ItemMeta.enchantedEffect()
{
	if (enchants.isNotEmpty()) return
	addEnchant(Enchantment.DURABILITY, 0, true)
	addItemFlags(ItemFlag.HIDE_ENCHANTS)
}

//inline fun ItemStack.meta(body: ItemMeta.() -> Unit) = meta<ItemMeta>(body)`