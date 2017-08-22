package blue.sparse.bukkitk

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

inline fun JavaPlugin.command(name: String, description: String = "", usage: String = "$name [args]", vararg aliases: String, crossinline body: CommandContext.() -> Unit)
{
	val command: Command = object : Command(name, description, usage, aliases.toList())
	{
		override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean
		{
			try
			{
				body(CommandContext(sender, this, label, args))
			} catch (e: CommandContext.CommandInterrupt)
			{
			}

			return true
		}
	}
	BukkitKPlugin.register(this, command)
//	if(!BukkitKPlugin.register(this, command))
//		throw IllegalStateException("Command could not be registered because it conflicts with another")
}


inline fun <reified T : Event> JavaPlugin.listen(
		priority: EventPriority = EventPriority.NORMAL,
		ignoreCancelled: Boolean = true,
		crossinline body: T.() -> Unit): KListener<T>
{
	val listener: Listener = object : Listener
	{}

	val result = KListener(listener, T::class.java)

	Bukkit.getPluginManager().registerEvent(
			T::class.java,
			listener,
			priority,
			{ lstnr, event -> if (lstnr == listener && event is T) body.invoke(event) },
			this,
			ignoreCancelled
	)

	return result
}

fun JavaPlugin.delay(delay: Long, body: () -> Unit)
{
	Bukkit.getScheduler().scheduleSyncDelayedTask(this, body, delay)
}

fun JavaPlugin.repeat(delay: Long, body: () -> Unit)
{
	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, body, 0, delay)
}