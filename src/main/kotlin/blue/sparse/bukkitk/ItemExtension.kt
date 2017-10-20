package blue.sparse.bukkitk

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

inline fun ItemStack.edit(body: ItemStack.() -> Unit): ItemStack
{
	val context = ItemStack(this)
	body.invoke(context)
	return context
}

inline fun item(item: ItemStack = ItemStack(Material.AIR), crossinline body: ItemStack.() -> Unit): ItemStack
{
	return item.edit(body)
}

inline fun <reified T : ItemMeta> ItemStack.meta(body: T.() -> Unit)
{
	val meta = itemMeta ?: return
	if (meta is T)
	{
		body(meta)
		itemMeta = meta
	} else throw IllegalArgumentException("Invalid ItemMeta type")
}