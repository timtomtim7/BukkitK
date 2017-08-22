package blue.sparse.bukkitk

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

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