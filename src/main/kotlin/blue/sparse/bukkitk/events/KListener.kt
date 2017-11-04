package blue.sparse.bukkitk.events

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin

abstract class KListener<T : Event>(val eventClass: Class<T>): Listener, EventExecutor
{
	private var registered: Boolean = true

	fun unregister()
	{
		if (!registered)
			throw IllegalStateException("KListener already unregistered")

		HandlerList.unregisterAll(this)
		registered = false
	}

	fun isRegistered() = registered

	@Suppress("UNCHECKED_CAST")
	override fun execute(listener: Listener, event: Event)
	{
		if(listener != this || !eventClass.isInstance(event))
			return

		listen(event as? T ?: return)
	}

	abstract fun listen(event: T)
}

inline fun <reified T: Event> JavaPlugin.listen(
		priority: EventPriority = EventPriority.NORMAL,
		ignoreCancelled: Boolean = true,
		crossinline body: KListener<T>.(T) -> Unit
): KListener<T>
{
	val listener = object: KListener<T>(T::class.java)
	{
		override fun listen(event: T) = body(this, event)
	}

	Bukkit.getPluginManager().registerEvent(T::class.java, listener, priority, listener, this, ignoreCancelled)
	return listener
}

fun Cancellable.cancel()
{
	this.isCancelled = true
}