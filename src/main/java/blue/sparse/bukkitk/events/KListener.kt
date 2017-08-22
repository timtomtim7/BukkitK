package blue.sparse.bukkitk.events

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.plugin.java.JavaPlugin

class KListener<T : Event>(private val listener: Listener, val event: Class<T>)
{
	private var registered: Boolean = true

	fun unregister()
	{
		if (!registered)
			throw IllegalStateException("KListener already unregistered")

		HandlerList.unregisterAll(listener)
		registered = false
	}
}

inline fun <reified T : Event> JavaPlugin.listen(
		priority: EventPriority = EventPriority.NORMAL,
		ignoreCancelled: Boolean = true,
		crossinline body: KListener<T>.(T) -> Unit): KListener<T>
{
	val listener: Listener = object : Listener
	{}

	val result = KListener(listener, T::class.java)

	Bukkit.getPluginManager().registerEvent(
			T::class.java,
			listener,
			priority,
			{ lstnr, event -> if (lstnr == listener && event is T) body.invoke(result, event) },
			this,
			ignoreCancelled
	)

	return result
}

fun Cancellable.cancel()
{
	this.isCancelled = true
}