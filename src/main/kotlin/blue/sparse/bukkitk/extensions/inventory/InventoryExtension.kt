package blue.sparse.bukkitk.extensions.inventory

import blue.sparse.bukkitk.events.listen
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

operator fun Inventory.set(slot: Int, item: ItemStack) = setItem(slot, item)
operator fun Inventory.get(slot: Int): ItemStack? = getItem(slot)

inline fun Inventory.setItem(slot: Int, item: ItemStack, crossinline body: ItemStack.() -> Unit)
		= setItem(slot, item(item, body))

inline fun Inventory.setItem(slot: Int, material: Material, crossinline body: ItemStack.() -> Unit)
		= setItem(slot, item(material, body))

inline fun Inventory.addItem(item: ItemStack, crossinline body: ItemStack.() -> Unit)
		= addItem(item(item, body))

inline fun Inventory.addItem(material: Material, crossinline body: ItemStack.() -> Unit)
		= addItem(item(material, body))

fun JavaPlugin.inventory(title: String, rows: Int): KInventoryHolder
{
	return KInventoryHolder(this, Bukkit.createInventory(null, rows * 9, title))
}

inline fun JavaPlugin.inventory(title: String, rows: Int, body: KInventoryHolder.() -> Unit): KInventoryHolder
{
	return inventory(title, rows).apply(body)
}

inline fun HumanEntity.openInventory(plugin: JavaPlugin, title: String, rows: Int, body: KInventoryHolder.() -> Unit): KInventoryHolder
{
	val inv = plugin.inventory(title, rows).apply(body)
	openInventory(inv.inventory)
	return inv
}

val Inventory.slots get() = 0 until size

typealias ClickHandler = InventoryClickEvent.() -> Unit

//I don't like having this extend Inventory
class KInventoryHolder internal constructor(private val plugin: JavaPlugin, private val inventory: Inventory) : Inventory by inventory, InventoryHolder
{
	private val clickHandlers = HashMap<Int, MutableList<ClickHandler>>()

	private val listener = plugin.listen<InventoryClickEvent> { event ->
		if (event.clickedInventory != inventory) return@listen

		handleClickEvent(event.slot, event)
		handleClickEvent(-1, event)
	}

	private fun handleClickEvent(slot: Int, event: InventoryClickEvent)
	{
		clickHandlers[slot]?.forEach {
			try
			{
				it(event)
			} catch (e: Throwable)
			{
				plugin.logger.severe("Error in inventory click listener.")
				e.printStackTrace()
			}
		}
	}

	override fun getInventory(): Inventory = inventory

	fun onClick(handler: ClickHandler)
	{
		clickHandlers.getOrPut(-1) { ArrayList() }.add(handler)
	}

	fun onClick(slot: Int, handler: ClickHandler)
	{
		if (slot !in inventory.slots)
			throw IndexOutOfBoundsException("Slot index out of bounds: $slot")
		clickHandlers.getOrPut(slot) { ArrayList() }.add(handler)
	}
//	private val listeners = ArrayList<KListener<*>>()
//
//	fun onClick(plugin: JavaPlugin, handler: InventoryClickEvent.(KListener<InventoryClickEvent>) -> Unit)
//	{
//		listeners.add(plugin.listen<InventoryClickEvent> {
//			if(it.clickedInventory == inventory)
//				handler(it, this)
//		})
//	}

	protected fun finalize()
	{
		listener.unregister()
	}
}