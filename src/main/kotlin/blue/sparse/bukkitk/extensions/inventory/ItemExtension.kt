package blue.sparse.bukkitk.extensions.inventory

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

val ItemStack?.empty get() = this == null || (type ?: Material.AIR) == Material.AIR || amount <= 0
fun ItemStack?.notEmptyOrNull() = if (empty) null else this

inline fun item(material: Material, amount: Int = 1, crossinline body: ItemStack.() -> Unit = {}): ItemStack
		= ItemStack(material, amount).apply(body)

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

fun ItemStack.enchant(enchantment: Enchantment, level: Int = 1) = addEnchantment(enchantment, level)
var ItemStack.lore: List<String>
	get() = itemMeta.lore ?: emptyList()
	set(value) = meta { lore = value }
var ItemStack.displayName: String?
	get() = itemMeta.displayName
	set(value) = meta { displayName = value }

operator fun Material.times(amount: Int) = ItemStack(this, amount)
operator fun ItemStack.times(amount: Int) = ItemStack(this).apply { this.amount *= amount }

fun ItemStack.enchantedEffect() = meta { enchantedEffect() }

fun ItemMeta.enchantedEffect()
{
	if (enchants.isNotEmpty()) return
	addEnchant(Enchantment.DURABILITY, 0, true)
	addItemFlags(ItemFlag.HIDE_ENCHANTS)
}